package com.example.jobbkalender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jobbkalender.DataClasses.Job;
import com.example.jobbkalender.DataClasses.WorkdayEvent;
import com.example.jobbkalender.dialogFragments.ChooseWorkplaceDialogFragment;
import com.example.jobbkalender.dialogFragments.TimePickerDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreateEvent extends AppCompatActivity implements TimePickerDialogFragment.OnInputListener, ChooseWorkplaceDialogFragment.OnInputListener {

    int editTextToChange = 0;
    TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
    ChooseWorkplaceDialogFragment chooseWorkplaceDialogFragment = new ChooseWorkplaceDialogFragment();

    List<Job> jobList = new ArrayList<>();
    String jobName = " ";

    private void loadJobs(){
        SharedPreferences pref = this.getSharedPreferences("Shared pref", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("JOBLIST",null);
        Log.d("JSON","Json read: " + json);
        Type type = new TypeToken<ArrayList<Job>>(){}.getType();
        try {
            jobList= gson.fromJson(json,type);
        } catch (Exception e){
            Log.e("Eroor","Failed to load jobs");
        }
        Log.d("List:", jobList.get(0).toString());
    }

    private Job getJobByName(String name){
        for (Job job: jobList
             ) {
            if(job.getName() == name){
                return job;
            }
        }
        return null;
    }

    @Override
    public void sendTime(String input) {
        Log.d("Set time: ", input);
        if(editTextToChange == 1){
            TextView timeInput = findViewById(R.id.timeInputFromCreateEvent);
            timeInput.setText(input);
        } else if(editTextToChange == 2){
            TextView timeInput = findViewById(R.id.timeInputToCreateEvent);
            timeInput.setText(input);
        }
    }
    @Override
    public void sendWorkplace( Job workplace ){
        Log.d("Set workplace:", workplace.toString());
        TextView t = findViewById(R.id.textViewSelectedWorkplaceCreateEvent);
        String name = workplace.getName();
        jobName = name;
        t.setText(name);
    }

    void showTimePickerDialog() {

        if(timePickerDialogFragment.isAdded())
            return; // Prevent illegal state
        timePickerDialogFragment.show(getSupportFragmentManager(), "Pick time:");
        Log.d("Dialog: ", "Time picker opened");
    }
    void showChooseWorkplaceDialog(){

        if(chooseWorkplaceDialogFragment.isAdded())
            return; // Prevent illegal state
        chooseWorkplaceDialogFragment.show(getSupportFragmentManager(), "Choose workplace: ");
        Log.d("Dialog","Choose workplace opened");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadJobs();
        setContentView(R.layout.activity_create_event);
        TextView timeInputFrom = findViewById(R.id.timeInputFromCreateEvent);
        TextView timeInputTo = findViewById(R.id.timeInputToCreateEvent);

        timeInputFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
                editTextToChange = 1;
            }
        });
        timeInputTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
                editTextToChange = 2;
            }
        });

        Button addWorkplace = findViewById(R.id.buttonChooseWorkplaceCreateEvent);
        addWorkplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseWorkplaceDialog();
            }
        });
        Button submitWorkday = findViewById(R.id.buttonSubmitWorkday);
        submitWorkday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
                TextView timeInputFrom = findViewById(R.id.timeInputFromCreateEvent);
                TextView timeInputTo = findViewById(R.id.timeInputToCreateEvent);
                LocalTime startTime = LocalTime.parse(timeInputFrom.getText().toString(), dateTimeFormatter.ofPattern("HH:mm"));
                LocalTime endTime = LocalTime.parse(timeInputTo.getText().toString(), dateTimeFormatter.ofPattern("HH:mm"));
                if(startTime.isAfter(endTime)){
                    Log.d("Error", "Workday cant end before it starts!");
                    return;
                }
                String date = getIntent().getStringExtra("DATE");
                LocalDate eventDate = LocalDate.parse(date,dateTimeFormatter.ofPattern("ddMMyyyy"));
                Log.e("Event date:", eventDate.toString());
                EditText editTextBreakTime = findViewById(R.id.editTextBreakTime);

                int breakTime = Integer.parseInt(editTextBreakTime.getText().toString());
                WorkdayEvent workdayEvent = new WorkdayEvent(eventDate,startTime,endTime,breakTime,getJobByName(jobName));
            }
        });
    }
}

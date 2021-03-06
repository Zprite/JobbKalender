package com.birkeland.terminus.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birkeland.terminus.NotificationService;

public class AlarmRebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Log.d("BOOT COMPLETED","STARTING NOTIFICATION SERVICE");
            Intent i = new Intent(context, NotificationService.class);
            context.startForegroundService(i);
        }
    }
}

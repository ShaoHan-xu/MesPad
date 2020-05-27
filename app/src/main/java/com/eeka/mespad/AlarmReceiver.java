package com.eeka.mespad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eeka.mespad.activity.MainActivity;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.manager.Logger;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String BROADCAST_Maintenance = "android.alarm.maintenance.action";
    public static final String BROADCAST_Efficiency = "android.alarm.efficiency.action";
    public static final String BROADCAST_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d("收到广播了。。。。" + action);
        if (BROADCAST_BOOT_COMPLETED.equals(action)) {
//            Intent i = new Intent(context, MainActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        } else if (BROADCAST_Maintenance.equals(action)) {
            PushJson pushJson = new PushJson();
            boolean isWeek = intent.getBooleanExtra("isWeek", false);
            pushJson.setType(PushJson.TYPE_Maintenance);
            pushJson.setMessage(isWeek + "");
            EventBus.getDefault().post(pushJson);
        } else if (BROADCAST_Efficiency.equals(action)) {
            PushJson pushJson = new PushJson();
            pushJson.setType(PushJson.TYPE_Efficiency);
            EventBus.getDefault().post(pushJson);
        }
    }
}
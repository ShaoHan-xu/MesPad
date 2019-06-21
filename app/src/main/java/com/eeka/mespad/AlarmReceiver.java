package com.eeka.mespad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eeka.mespad.bo.PushJson;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String BROADCAST_Maintenance = "android.alarm.maintenance.action";

    public void onReceive(Context context, Intent intent) {
        if (BROADCAST_Maintenance.equals(intent.getAction())) {
            PushJson pushJson = new PushJson();
            boolean isWeek = intent.getBooleanExtra("isWeek", false);
            pushJson.setType(PushJson.TYPE_Maintenance);
            pushJson.setMessage(isWeek + "");
            EventBus.getDefault().post(pushJson);
        }
    }
}
package com.inochi.unstopable.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) action = "";

        if (!action.isEmpty()){
            Intent service = new Intent(context, MainService.class);
            service.setAction(action);
            service.putExtras(intent);
            context.startService(service);
        }
    }
}

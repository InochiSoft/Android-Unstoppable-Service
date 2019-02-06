package com.inochi.unstopable.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.inochi.unstopable.helper.Constants;
import com.inochi.unstopable.item.NotifItem;
import com.inochi.unstopable.util.TestNotification;

import java.util.Calendar;

public class MainService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null){
            String action = intent.getAction();
            if (action == null) action = "";
            NotifItem notifItem = (NotifItem) intent.getSerializableExtra(Constants.Setting.NOTIF_ITEM);

            switch (action){
                case "com.htc.intent.action.QUICKBOOT_POWERON":
                case "android.intent.action.QUICKBOOT_POWERON":
                case "android.intent.action.BOOT_COMPLETED":
                case Constants.Action.CREATE_DAILY:
                    createDailyAlarm();
                    boolean isServiceRun = isAutoServiceRunning();
                    if (!isServiceRun){
                        startAutoService();
                    } else {
                        runAutoServiceNotification();
                    }
                    break;
                case Constants.Action.START_SERVICE:
                    startAutoService();
                    break;
                case Constants.Action.SHOW_NOTIFY:
                    if (notifItem != null)
                        TestNotification.notify(this, notifItem);
                    break;
                case Constants.Action.CLOSE_NOTIFY:
                    if (notifItem != null)
                        TestNotification.cancel(this, notifItem);
                    break;
            }
        }
        return START_STICKY;
    }

    private boolean isAutoServiceRunning() {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (AutoService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startAutoService(){
        Intent service = new Intent(this, AutoService.class);
        service.setAction(Constants.Action.START_SERVICE);
        startService(service);
    }

    @Override
    public void onDestroy() {
        try {
            startAutoService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createDailyAlarm(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);

        long millis = calendar.getTimeInMillis();

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TestReceiver.class);
        intent.setAction(Constants.Action.SHOW_NOTIFY);

        int id = (int) millis / 1000;

        NotifItem notifItem = new NotifItem();
        notifItem.setId(id);
        notifItem.setTicker("Daily Alarm is Running");
        notifItem.setTitle("Daily Alarm is Running");
        notifItem.setMessage("Daily Alarm is Running " + String.valueOf(millis));

        intent.putExtra(Constants.Setting.NOTIF_ITEM, notifItem);

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, millis,
                AlarmManager.INTERVAL_DAY, alarmIntent);

        ComponentName receiver = new ComponentName(this, TestReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void runAutoServiceNotification(){
        Calendar calendar = Calendar.getInstance();
        long millis = calendar.getTimeInMillis();
        int id = (int) millis / 1000;

        NotifItem notifItem = new NotifItem();
        notifItem.setId(id);
        notifItem.setTicker("Auto Service is Running");
        notifItem.setTitle("Auto Service is Running");
        notifItem.setMessage("Auto Service is Running " + String.valueOf(millis));

        TestNotification.notify(this, notifItem);
    }
}

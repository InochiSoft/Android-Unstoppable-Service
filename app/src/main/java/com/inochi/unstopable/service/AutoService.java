package com.inochi.unstopable.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.inochi.unstopable.helper.Constants;
import com.inochi.unstopable.item.NotifItem;
import com.inochi.unstopable.util.TestNotification;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AutoService extends Service {
    private Timer timer;
    private TimerTask timerTask;

    public AutoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null){
            String action = intent.getAction();
            if (action == null) action = "";

            switch (action){
                case Constants.Action.START_SERVICE:
                    startTimer();
                    break;
            }
        }
        return START_STICKY;
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                boolean isServiceRun = isMainServiceRunning();
                if (!isServiceRun){
                    Intent service = new Intent(AutoService.this, MainService.class);
                    service.setAction(Constants.Action.START_SERVICE);
                    startService(service);
                } else {
                    runMainServiceNotification();
                }
            }
        };
    }

    private void startTimer() {
        stopTimerTask();

        //set a new Timer
        timer = new Timer();

        runAutoServiceNotification();

        //initialize the TimerTask's job
        initTimerTask();

        //schedule the timer, to wake up every 1 second and check every 1 minute
        timer.schedule(timerTask, 1000, 1000 * 60); //
    }

    private boolean isMainServiceRunning() {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MainService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Intent service = new Intent(this, MainService.class);
            service.setAction(Constants.Action.START_SERVICE);
            startService(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void runMainServiceNotification(){
        Calendar calendar = Calendar.getInstance();
        long millis = calendar.getTimeInMillis();
        int id = (int) millis / 1000;

        NotifItem notifItem = new NotifItem();
        notifItem.setId(id);
        notifItem.setTicker("Main Service is Running");
        notifItem.setTitle("Main Service is Running");
        notifItem.setMessage("Main Service is Running " + String.valueOf(millis));

        TestNotification.notify(this, notifItem);
    }
}

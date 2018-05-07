package com.vincent.vpedometer.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.base.StepMode;
import com.vincent.vpedometer.config.MyConstants;
import com.vincent.vpedometer.dao.ShutDownDao;
import com.vincent.vpedometer.pojo.ShutDown;
import com.vincent.vpedometer.pojo.StepData;
import com.vincent.vpedometer.ui.activity.SplashActivity;
import com.vincent.vpedometer.utils.DbUtils;
import com.vincent.vpedometer.utils.ServerUtils;
import com.vincent.vpedometer.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/2/8.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class StepService extends Service implements StepMode.StepCallBack {
    private static final long SCREEN_OFF_RECEIVER_DELAY = 500l;
    private final String TAG = "StepService";
    private String DB_NAME = "basepedo";
    //30 seconds to store data once
    private static int duration = 30000;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private Messenger messenger = new Messenger(new MessenerHandler());
    private BroadcastReceiver mBatInfoReceiver;
    private WakeLock mWakeLock;
    private TimeCount time;
    //the date of today
    private String CURRENTDATE = "";
    private StepMode mMode;
    private static String mLevel = "1";

    /**
     * recevie the message from client and send back the message to client
     */
    private static class MessenerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_CLIENT:
                    try {
                        //get client side messenger to send the message to client
                        Messenger messenger = msg.replyTo;
                        //create a message(letter)
                        Message replyMsg = Message.obtain(null, MyConstants.MSG_FROM_SERVER);
                        //create paper containing information
                        Bundle bundle = new Bundle();
                        //write the information on paper
                        bundle.putInt("step", StepMode.CURRENT_STEP);
                        bundle.putString("level", mLevel);
                        //put the paper to letter
                        replyMsg.setData(bundle);
                        //find post officer to send the letter
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        //android.os.Debug.waitForDebugger();
        super.onCreate();
        Log.v(TAG, "onCreate");
        initBroadcastReceiver();
        startStep();
        startTimeCount();
    }

    /**
     * register broadcast receiver to handle each status of the phone.
     */
    private void initBroadcastReceiver() {
        Log.v(TAG, "initBroadcastReceiver");
        final IntentFilter filter = new IntentFilter();
        // add the screen off broadcast
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //date change broadcast
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        //shutdown broadcast
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // screen on
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // unlock screen
        filter.addAction(Intent.ACTION_USER_PRESENT);
        //before user long click the power button to shutdown phone,this will call my receiver to do
        //some data saving
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        //the Receiver  to receive the broadcast
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.v(TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.v(TAG, "screen off");
                    //when screen off my service will 60 seconds to store data
                    duration = 60000;
                    //some manufacturer would block the sensor call back when screen off
                    //so we need to run the service again
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startStep();
                        }
                    };
                    new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.v(TAG, "screen unlock");
                    save();
                    //when screen on the save data will be 30 seconds
                    duration = 30000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.v(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                    //save data when user need to shutdown the phone
                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_SHUTDOWN");
                    //save data when phone will be shutdown
                    save();
                    record();
                } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_DATE_CHANGED");
                    //when the date go to tomorrow,we init today data and set current step to zero
                    initTodayData();
                    clearStepData();
                    startStep();
                    Log.v(TAG, "data to zero：" + StepMode.CURRENT_STEP);
                    Step(StepMode.CURRENT_STEP);
                }
            }
        };
        //my receiver need to receive the filter content;
        registerReceiver(mBatInfoReceiver, filter);
    }


    /**
     * clear yesterday step and set step to 0 for today step recording
     */
    private void clearStepData() {
        StepMode.CURRENT_STEP = 0;
    }

    /**
     * query the sqlite database where today column is today
     */
    private void initTodayData() {
        CURRENTDATE = getTodayDate();
        //createDb means init the LiteOrm object for access the database,
        // when database has been create will not be created again
        DbUtils.createDb(this, DB_NAME);
        //get today step counting data and display it
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepMode.CURRENT_STEP = 0;
        } else if (list.size() == 1) {
            StepMode.CURRENT_STEP = list.get(0).getStep();
        } else {
            Log.v(TAG, "It's wrong！");
        }
    }


    /**
     * start to call step mode class's method to do the step counting
     * there are two mode the google providing step counting system
     * or
     * my implementing system to cal the step of the user
     */
    private void startStep() {
        //call google pedometer
        if (mMode == null) {
            mMode = new StepInPedometer(this, this);
            //get sensor status and running the step counting system.
            boolean isAvailable = mMode.getStep();
            Log.v(TAG, "StepInPedometer  execute!");
            //if google pedometer can not use we use acceleration to do step counting
            if (!isAvailable) {
                mMode = new StepInAcceleration(this, this);
                isAvailable = mMode.getStep();
                if (isAvailable) {
                    Log.v(TAG, "acceleration can execute!");
                }
            }
        }
    }


    /**
     * call back function when sensor has some reaction
     *
     * @param stepNum the step count will be parameter for call back
     */
    @Override
    public void Step(int stepNum) {
        StepMode.CURRENT_STEP = stepNum;
        Log.v(TAG, "Step:" + stepNum);
        updateNotification("today step count：：" + stepNum + " steps");
    }

    /**
     * start timer and set it every 1 second to call the implementing method(ontick)
     * when after duration time will call onfinish method
     */
    private void startTimeCount() {
        time = new TimeCount(duration, 1000);
        time.start();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    /**
     * bind service will return the messenger to client for communication
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        //      save();
        return messenger.getBinder();
    }

    /**
     * each time service has been called this method will be invoked, and return START_STICKY is telling system,
     * when the service has been killed by resource not enough,you need to start again when resource enough
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //save();
        initTodayData();
        updateNotification("today step count：" + StepMode.CURRENT_STEP + " steps");
        return START_STICKY;
    }

    /**
     * for formatting second to regular date
     *
     * @return
     */
    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    /**
     * set the service to Foreground service ro reduce being killed risk
     * update notification
     */
    private void updateNotification(String content) {
        builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);
        //set pending intent is for calling back my main activity when notification being clicked
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.v);
        builder.setTicker("Vpedometer");
        builder.setContentTitle(content);
        //the setting is not available to slide to delete the notification
        builder.setOngoing(true);
        //Set the text (second row) of the notification, in a standard notification.
        builder.setContentText(TimeUtils.countTotalKM(StepMode.CURRENT_STEP) + "km");
        //set time
        builder.setShowWhen(false);
        //render the notification
        Notification notification = builder.build();
        //the first paramter is 0 means do not display it
        startForeground(R.string.app_name, notification);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //set the notification in the status bar
        nm.notify(R.string.app_name, notification);
    }


    /**
     * delete the connection object
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * the timer class
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //if the timer is successfully finish,the service do the recording
            time.cancel();
            //save the data to database when timer is finish in thi time
            save();
            //and set the timer  start again
            startTimeCount();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    /**
     * save or update the data to database,such as today step counting
     */
    private void save() {
        Log.i("save", StepMode.CURRENT_STEP + "");
        int tempStep = StepMode.CURRENT_STEP;
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setToday(CURRENTDATE);
            data.setStep(tempStep);
            DbUtils.insert(data);
        } else if (list.size() == 1) {
            StepData data = list.get(0);
            data.setStep(tempStep);
            DbUtils.update(data);
        } else {
        }
        uploadSteps();
    }


    /**
     * upload step to server and level up the game character
     */
    private void uploadSteps() {
        if (!ServerUtils.isOnNetwork(this)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<StepData> queryAll = DbUtils.getQueryAll(StepData.class);
                int totalStep = 0;
                for (StepData stepData : queryAll) {
                    totalStep += stepData.getStep();
                }
                if (totalStep - getTotalsteps() > 30) {
                    mLevel = ServerUtils.uploadSteps(totalStep + "", StepService.this);
                    setTotalSteps(totalStep);
                }
            }
        }).start();

    }

    int mtotalSteps = 0;//determine to upload data to server or not

    int getTotalsteps() {
        return mtotalSteps;
    }

    void setTotalSteps(int steps) {
        this.mtotalSteps = steps;
    }

    /**
     * save the smartphone shutdown date in order to record the steps to database.
     */
    private void record() {
        ShutDownDao shutDownDao = new ShutDownDao(this);
        List<ShutDown> query = shutDownDao.query();
        List<StepData> queryAll = DbUtils.getQueryAll(StepData.class);
        int totalStep = 0;
        for (StepData stepData : queryAll) {
            totalStep += stepData.getStep();
        }
        if (query.size() == 0 || query.isEmpty()) {
            shutDownDao.add(getTodayDate(), totalStep);

        } else if (query.size() > 0) {
            ShutDown shutDown = query.get(0);
            shutDownDao.update(shutDown.getId(), getTodayDate(), totalStep);
        } else {
        }


    }

    /**
     * when the service is killed we need to close all the resource.
     */
    @Override
    public void onDestroy() {
        //cancel the Foreground process
        stopForeground(true);
        DbUtils.closeDb();
        unregisterReceiver(mBatInfoReceiver);
        //try to start the step service again when it was killed
        Intent intent = new Intent(this, StepService.class);
        startService(intent);
        super.onDestroy();
    }


//    private  void unlock(){
//        setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false);
//    }
//
//    private void setLockPatternEnabled(String systemSettingKey, boolean enabled) {
//
//        android.provider.Settings.Secure.putInt(getContentResolver(), systemSettingKey,enabled ? 1 : 0);
//    }

    /**
     * power manager to handle screen process
     *
     * @param context
     * @return
     */
    synchronized private PowerManager.WakeLock getLock(Context context) {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
        return (mWakeLock);
    }


}

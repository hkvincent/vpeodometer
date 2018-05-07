package com.vincent.vpedometer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vincent.vpedometer.services.StepService;

/**
 * when phone reboot, this class will do the open service job
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StepService.class);
        context.startService(i);
        Log.i("reciver", "reboot");
       // throw new UnsupportedOperationException("Not yet implemented");
    }
}

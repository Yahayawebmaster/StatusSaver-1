package com.collabcreation.statussaver.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Service.OverlappService;

import static android.content.Context.MODE_PRIVATE;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(Common.BG_SERVICE, MODE_PRIVATE);
        if (preferences.getBoolean(Common.isBgTrue, false)) {
            context.startService(new Intent(context, OverlappService.class));
        }
    }
}

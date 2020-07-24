package com.collabcreation.statussaver.Activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.Service.OverlappService;

public class SettingsActivity extends AppCompatActivity {
    SwitchCompat bgservice, autoSaver;
    LinearLayout behaviourSelector, bgServiceLayout;
    Dialog dialog;
    SharedPreferences preferences, autoSavePref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bgservice = findViewById(R.id.bgservice);
        autoSaver = findViewById(R.id.autosave);
        bgServiceLayout = findViewById(R.id.bgserviceLayout);
        behaviourSelector = findViewById(R.id.behaviourSelector);
        preferences = getSharedPreferences(Common.BG_SERVICE, MODE_PRIVATE);
        autoSavePref = getSharedPreferences(Common.AUTO_STATUS_SAVER, MODE_PRIVATE);
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_layout);
        RadioGroup group = dialog.findViewById(R.id.group);
        if (!autoSavePref.getString(Common.whichBehaviour, "").isEmpty()) {
            if (autoSavePref.getString(Common.whichBehaviour, "").equals(Common.BEHAVIOUR_IMAGE_ONLY)) {
                group.check(R.id.image);
            } else if (autoSavePref.getString(Common.whichBehaviour, "").equals(Common.BEHAVIOUR_VIDEO_ONLY)) {
                group.check(R.id.video);
            } else if (autoSavePref.getString(Common.whichBehaviour, "").equals(Common.BEHAVIOUR_BOTH)) {
                group.check(R.id.both);
            }
        } else {
            autoSavePref.edit().putString(Common.whichBehaviour, Common.BEHAVIOUR_BOTH).apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bgServiceLayout.setVisibility(View.GONE);
        } else {
            bgServiceLayout.setVisibility(View.VISIBLE);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.image:
                        autoSavePref.edit().putString(Common.whichBehaviour, Common.BEHAVIOUR_IMAGE_ONLY).apply();
                        break;
                    case R.id.video:
                        autoSavePref.edit().putString(Common.whichBehaviour, Common.BEHAVIOUR_VIDEO_ONLY).apply();
                        break;
                    case R.id.both:
                        autoSavePref.edit().putString(Common.whichBehaviour, Common.BEHAVIOUR_BOTH).apply();
                        break;
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        if (autoSavePref.getBoolean(Common.isBgTrue, false)) {
            bgservice.setChecked(true);
            autoSaver.setChecked(true);
            preferences.edit().putBoolean(Common.isBgTrue, true).apply();
            if (!checkServiceRunning(OverlappService.class)) {
                startService(new Intent(getApplicationContext(), OverlappService.class));
            }
        } else {
            autoSaver.setChecked(false);
        }

        if (preferences.getBoolean(Common.isBgTrue, false)) {
            bgservice.setChecked(true);
            if (!checkServiceRunning(OverlappService.class)) {
                startService(new Intent(getApplicationContext(), OverlappService.class));
            }
        } else {
            bgservice.setChecked(false);
            if (checkServiceRunning(OverlappService.class)) {
                stopService(new Intent(getApplicationContext(), OverlappService.class));
            }
        }

        autoSaver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoSavePref.edit().putBoolean(Common.isBgTrue, isChecked).apply();
                if (isChecked) {
                    bgservice.setChecked(isChecked);
                    if (autoSavePref.getString(Common.whichBehaviour, "").isEmpty()) {
                        autoSavePref.edit().putString(Common.whichBehaviour, Common.BEHAVIOUR_BOTH).apply();
                    }
                    preferences.edit().putBoolean(Common.isBgTrue, isChecked).apply();
                    if (!checkServiceRunning(OverlappService.class)) {
                        startService(new Intent(getApplicationContext(), OverlappService.class));
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    bgservice.setChecked(false);
                }
            }
        });

        behaviourSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        });

        bgservice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Common.isBgTrue, b).apply();
                if (b) {
                    if (!checkServiceRunning(OverlappService.class)) {
                        startService(new Intent(getApplicationContext(), OverlappService.class));
                    }
                } else {
                    autoSaver.setChecked(false);
                    autoSavePref.edit().putBoolean(Common.isBgTrue, false).apply();
                    if (checkServiceRunning(OverlappService.class)) {
                        stopService(new Intent(getApplicationContext(), OverlappService.class));
                    }
                }
            }

        });
    }

    public boolean checkServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }


}

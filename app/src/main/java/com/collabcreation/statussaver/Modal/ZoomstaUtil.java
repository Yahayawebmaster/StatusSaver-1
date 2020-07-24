package com.collabcreation.statussaver.Modal;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.collabcreation.statussaver.BuildConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tirgei on 10/31/17.
 */

public class ZoomstaUtil {
    public static final String PREFS_NAME = Common.LOGIN;

    public static void clearPref(Context c) {
        c.getSharedPreferences(Common.LOGIN, 0).edit().clear().commit();
    }

    public static boolean setStringPreference(Context c, String value, String key) {
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getStringPreference(Context c, String key) {
        return c.getSharedPreferences(PREFS_NAME, 0).getString(key, "");
    }

    public static boolean setIntegerPreference(Context c, int value, String key) {
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static Integer getIntegerPreference(Context c, String key) {
        return c.getSharedPreferences(PREFS_NAME, 0).getInt(key, 0);
    }

    public static Boolean setBooleanPreference(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static Boolean getBooleanPreference(Context context, String key) {
        return context.getSharedPreferences(PREFS_NAME, 0).getBoolean(key, false);
    }

    public static void showToast(Activity context, String message, int status) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static void saveArrayList(Context context, List<String> favedUsers) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();

        Gson gson = new Gson();
        String jsonUsers = gson.toJson(favedUsers);

        editor.putString("favedUsers", jsonUsers);

        editor.commit();
    }

    public static ArrayList<String> getUsers(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        List<String> users;

        if (settings.contains("favedUsers")) {
            String jsonUsers = settings.getString("favedUsers", null);
            Gson gson = new Gson();
            String[] userItems = gson.fromJson(jsonUsers, String[].class);

            users = Arrays.asList(userItems);
            users = new ArrayList<>(users);

            if (!users.isEmpty())
                return (ArrayList<String>) users;
            else
                return null;


        } else
            return null;

    }
}

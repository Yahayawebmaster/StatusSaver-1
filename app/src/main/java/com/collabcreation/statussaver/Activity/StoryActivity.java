package com.collabcreation.statussaver.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.InstaVideoModal;
import com.collabcreation.statussaver.Modal.User;
import com.collabcreation.statussaver.R;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.INSTA_PROFILE;

public class StoryActivity extends AppCompatActivity {
    String requesturl = null;
    InstaVideoModal instaVideoModal;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        dialog = new ProgressDialog(StoryActivity.this,R.style.Dialogtheme);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if ("text/plain".equals(getIntent().getType())) {
                String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if (url.contains("instagram.com")) {
                    if (dialog != null && !dialog.isShowing()) {
                        dialog.show();
                    }
                    final String Url[] = url.split("\\?");
                    if (Url[0].contains("https://www.instagram.com/p/") || Url[0].contains("https://www.instagram.com/tv/")) {
                        requesturl = Url[0] + "?__a=1";
                    } else {
                        requesturl = Url[0] + "/?__a=1";
                    }
                    openInstagram();
                } else {
                    finish();
                }
            }
        }
    }

    private void openInstagram() {
        StringRequest request = new StringRequest(Request.Method.GET, requesturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                instaVideoModal = new Gson().fromJson(response, InstaVideoModal.class);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                User user = new User();
                user.setFullName(instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getFullName());
                user.setId(instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getId());
                user.setUsername(instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getUsername());
                user.setProfilePicUrlHd(instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getProfilePicUrl());
                Intent intent = new Intent(getApplicationContext(), InstaProfileActivity.class)
                        .putExtra(Common.MEDIA_TYPE, INSTA_PROFILE)
                        .putExtra("user", user);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast toast = Toasty.error(getBaseContext(), "Error while fetching! ", Toasty.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(StoryActivity.this);
        requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

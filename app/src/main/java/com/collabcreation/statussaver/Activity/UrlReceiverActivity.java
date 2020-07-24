package com.collabcreation.statussaver.Activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.collabcreation.statussaver.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.ACTION_BGDOWNLOAD;

public class UrlReceiverActivity extends AppCompatActivity {
    String requesturl = null;
    InstaVideoModal instaVideoModal;
    ArrayList<String> urls;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_receiver);
        dialog = new ProgressDialog(UrlReceiverActivity.this,R.style.Dialogtheme);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if ("text/plain".equals(getIntent().getType())) {
                String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if (url.contains("instagram.com")) {
                    if (dialog != null && !dialog.isShowing()){
                        dialog.show();
                    }
                    registerReceiver(BackgoundRec, new IntentFilter(ACTION_BGDOWNLOAD));
                    final String Url[] = url.split("\\?");
                    if (Url[0].contains("https://www.instagram.com/p/") || Url[0].contains("https://www.instagram.com/tv/")) {
                        requesturl = Url[0] + "?__a=1";
                    } else {
                        requesturl = Url[0] + "/?__a=1";
                    }
                    openInstagram();
                }else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        unregisterReceiver(BackgoundRec);
    }

    BroadcastReceiver BackgoundRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(Common.MEDIA_TYPE).equals(Common.INSTA_VIDEO)) {
                String filename = "Insta Video" + System.currentTimeMillis();
                Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Uri uri = Uri.parse(intent.getStringExtra("url"));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(filename);

                request.setVisibleInDownloadsUi(false);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".mp4");

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                finish();
            }
            else if (intent.getStringExtra(Common.MEDIA_TYPE).equals(Common.INSTA_IMAGE)) {
                String filename = "Insta Image" + System.currentTimeMillis();
                Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Uri uri = Uri.parse(intent.getStringExtra("url"));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(filename);

                request.setVisibleInDownloadsUi(false);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".jpg");

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                finish();
            }
            else if (intent.getStringExtra(Common.MEDIA_TYPE).equals(Common.INSTA_PROFILE)) {
                String filename = instaVideoModal.getGraphql().getUser().getUsername() + "'s Profile Image";
                Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Uri uri = Uri.parse(intent.getStringExtra("url"));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(filename);

                request.setVisibleInDownloadsUi(false);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".jpg");

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                finish();
            }

        }
    };


    private void openInstagram() {
        StringRequest request = new StringRequest(Request.Method.GET, requesturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                instaVideoModal = new Gson().fromJson(response, InstaVideoModal.class);
                if (instaVideoModal.getGraphql().getShortcodeMedia().getTypename().equals(Common.INSTA_VIDEO)) {
                    if (!instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getIsPrivate()) {
                        Intent intent = new Intent().setAction(ACTION_BGDOWNLOAD)
                                .putExtra(Common.URL_TYPE, Common.SINGLE_URL).putExtra(Common.MEDIA_TYPE, Common.INSTA_VIDEO).putExtra("url", instaVideoModal.getGraphql().getShortcodeMedia().getVideoUrl());
                        sendBroadcast(intent);
                    } else {
                        if (dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toasty.error(getBaseContext(), "can not download from private account", Toasty.LENGTH_LONG).show();
                    }


                } else if (instaVideoModal.getGraphql().getShortcodeMedia().getTypename().equals(Common.INSTA_IMAGE)) {
                    if (!instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getIsPrivate()) {
                        Intent intent = new Intent().setAction(ACTION_BGDOWNLOAD)
                                .putExtra(Common.URL_TYPE, Common.SINGLE_URL).putExtra(Common.MEDIA_TYPE, Common.INSTA_IMAGE).putExtra("url", instaVideoModal.getGraphql().getShortcodeMedia().getDisplayResources().get(2).getSrc());
                        sendBroadcast(intent);

                    } else {
                        if (dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toasty.error(getBaseContext(), "can not download from private account", Toasty.LENGTH_LONG).show();
                    }
                } else if (instaVideoModal.getGraphql().getShortcodeMedia().getTypename().equals(Common.INSTA_SLIDE)) {
                    urls = new ArrayList<>();
                    urls.clear();
                    for (int i = 0; i < instaVideoModal.getGraphql().getShortcodeMedia().getEdgeSidecarToChildren().getEdges().size(); i++) {
                        if (instaVideoModal.getGraphql().getShortcodeMedia().getEdgeSidecarToChildren()
                                .getEdges().get(i).getNode().getTypename().equals(Common.INSTA_IMAGE)) {
                            urls.add(instaVideoModal.getGraphql().getShortcodeMedia().getEdgeSidecarToChildren()
                                    .getEdges().get(i).getNode().getDisplayResources().get(2).getSrc());
                        } else if (instaVideoModal.getGraphql().getShortcodeMedia().getEdgeSidecarToChildren()
                                .getEdges().get(i).getNode().getTypename().equals(Common.INSTA_VIDEO)) {
                            urls.add(instaVideoModal.getGraphql().getShortcodeMedia().getEdgeSidecarToChildren()
                                    .getEdges().get(i).getNode().getVideoUrl());
                        }
                    }
                    if (dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Intent intent = new Intent(getApplicationContext(), InstaSliderActivity.class)
                            .putExtra(Common.URL_TYPE, Common.MULTI_URL).putStringArrayListExtra("urls", urls);
                    startActivity(intent);
                    finish();

                }


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

        RequestQueue requestQueue = Volley.newRequestQueue(UrlReceiverActivity.this);
        requestQueue.add(request);
    }
}

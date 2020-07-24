package com.collabcreation.statussaver.Service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.collabcreation.statussaver.Activity.InstaProfileActivity;
import com.collabcreation.statussaver.Activity.InstaSliderActivity;
import com.collabcreation.statussaver.Activity.SettingsActivity;
import com.collabcreation.statussaver.Activity.YoutubeActivity;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.InstaVideoModal;
import com.collabcreation.statussaver.Modal.Story;
import com.collabcreation.statussaver.Modal.User;
import com.collabcreation.statussaver.Modal.UserObject;
import com.collabcreation.statussaver.Modal.ZoomstaUtil;
import com.collabcreation.statussaver.R;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.cookie.SM;
import cz.msebera.android.httpclient.protocol.HTTP;
import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.ACTION_BGDOWNLOAD;
import static com.collabcreation.statussaver.Modal.Common.BEHAVIOUR_BOTH;
import static com.collabcreation.statussaver.Modal.Common.BEHAVIOUR_IMAGE_ONLY;
import static com.collabcreation.statussaver.Modal.Common.BEHAVIOUR_VIDEO_ONLY;
import static com.collabcreation.statussaver.Modal.Common.copyFile;
import static com.collabcreation.statussaver.Modal.Common.getBWOurDirectory;
import static com.collabcreation.statussaver.Modal.Common.getBWhatsappDirectory;
import static com.collabcreation.statussaver.Modal.Common.getGBOurDirectory;
import static com.collabcreation.statussaver.Modal.Common.getGBWhatsappDirectory;
import static com.collabcreation.statussaver.Modal.Common.getOurDirectory;
import static com.collabcreation.statussaver.Modal.Common.getWhatsappDirectory;
import static com.collabcreation.statussaver.Modal.Common.isBgTrue;
import static com.collabcreation.statussaver.Modal.Common.whichBehaviour;

public class OverlappService extends Service {
    private static String session_id, user_id;
    InstaVideoModal instaVideoModal;
    ClipboardManager clipboardManager;
    String url = "";
    private static final int CIRCLE_TIME = 3000;
    ArrayList<String> urls;
    String previousText = "";
    RemoteViews collapsedView = null;
    SharedPreferences preferences;
    Intent intent1 = null;
    String thumb = "", profileurl = "";
    public BroadcastReceiver BackgoundRec;
    FileObserver observer;

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String TIMETABLEGP_CHANNEL_ID = "com.collabcreation.statussaver";
        String TIMETABLEGP_CHANNEL_NAME = "STATUSSAVER2019-20";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TIMETABLEGP_CHANNEL_ID, TIMETABLEGP_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.setAllowBubbles(true);
            }
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(channel);

            Notification builder = new NotificationCompat.Builder(getApplicationContext(), TIMETABLEGP_CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .setContentText("running...")
                    .setContentTitle("Instant Saver")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(PendingIntent.getActivity(getBaseContext(), 55,
                            new Intent(getBaseContext(), SettingsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(new NotificationCompat.Action(R.drawable.icon, "STOP", PendingIntent.getBroadcast(getApplicationContext(), 22,
                            new Intent().setAction("STOP"), PendingIntent.FLAG_UPDATE_CURRENT)))
                    .setAutoCancel(false)
                    .build();

            startForeground(1, builder);

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Instant Saver")
                    .setContentText("running...")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .addAction(new NotificationCompat.Action(R.drawable.icon, "STOP", PendingIntent.getBroadcast(getApplicationContext(), 22,
                            new Intent().setAction("STOP"), PendingIntent.FLAG_UPDATE_CURRENT)))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(PendingIntent.getActivity(getBaseContext(), 5,
                            new Intent(getBaseContext(), SettingsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(false);

            startForeground(1, builder.build());
        }

        return START_STICKY;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getSharedPreferences(Common.BG_SERVICE, MODE_PRIVATE)
                        .edit().putBoolean(Common.isBgTrue, false).apply();
                getSharedPreferences(Common.AUTO_STATUS_SAVER, MODE_PRIVATE)
                        .edit().putBoolean(Common.isBgTrue, false).apply();
                stopForeground(true);
            }
        }, new IntentFilter("STOP"));
        preferences = getSharedPreferences(Common.AUTO_STATUS_SAVER, MODE_PRIVATE);
        if (getWhatsappDirectory().exists()) {
            observer = new FileObserver(getWhatsappDirectory() + "/") {
                @Override
                public void onEvent(final int event, @Nullable final String path) {
                    if (preferences.getBoolean(isBgTrue, false)) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_BOTH)) {
                                    if (path != null) {
                                        if (!new File(getOurDirectory() + "/" + path).exists()) {
                                            try {
//                                                if (isNamedProcessRunning("Whatsapp")) {
                                                copyFile(new File(getWhatsappDirectory() + "/" + path), new File(getOurDirectory() + "/" + path));
//                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                } else if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_IMAGE_ONLY)) {
                                    if (path != null && path.contains(".jpg")) {
                                        if (!new File(getOurDirectory() + "/" + path).exists()) {
                                            try {
//                                                if (isNamedProcessRunning("Whatsapp")) {
                                                copyFile(new File(getWhatsappDirectory() + "/" + path), new File(getOurDirectory() + "/" + path));
//                                                    Toast.makeText(getBaseContext(), "Image Saved", Toast.LENGTH_SHORT).show();
//                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_VIDEO_ONLY)) {
                                    if (path != null && path.contains(".mp4")) {
                                        if (!new File(getOurDirectory() + "/" + path).exists()) {
                                            try {
//                                                if (isNamedProcessRunning("Whatsapp")) {
                                                copyFile(new File(getWhatsappDirectory() + "/" + path), new File(getOurDirectory() + "/" + path));
//                                                }
//                                                Toast.makeText(getBaseContext(), "Video Saved", Toast.LENGTH_SHORT).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }
                        });


                    }

                }
            };
            observer.startWatching();
        }
        else if (getGBWhatsappDirectory().exists()) {
            observer = new FileObserver(getGBWhatsappDirectory() + "/") {
                @Override
                public void onEvent(final int event, @Nullable final String path) {
                    if (preferences.getBoolean(isBgTrue, false)) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_BOTH)) {
                                    if (path != null) {
                                        if (!new File(getGBOurDirectory() + "/" + path).exists()) {
                                            try {
                                                copyFile(new File(getGBWhatsappDirectory() + "/" + path), new File(getGBOurDirectory() + "/" + path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                } else if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_IMAGE_ONLY)) {
                                    if (path != null && path.contains(".jpg")) {
                                        if (!new File(getGBOurDirectory() + "/" + path).exists()) {
                                            try {
                                                copyFile(new File(getGBWhatsappDirectory() + "/" + path), new File(getGBOurDirectory() + "/" + path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_VIDEO_ONLY)) {
                                    if (path != null && path.contains(".mp4")) {
                                        if (!new File(getGBOurDirectory() + "/" + path).exists()) {
                                            try {
                                                copyFile(new File(getGBWhatsappDirectory() + "/" + path), new File(getGBOurDirectory() + "/" + path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }
                        });


                    }

                }
            };
            observer.startWatching();
        }
        else if (getBWhatsappDirectory().exists()) {
            observer = new FileObserver(getBWhatsappDirectory() + "/") {
                @Override
                public void onEvent(final int event, @Nullable final String path) {
                    if (preferences.getBoolean(isBgTrue, false)) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_BOTH)) {
                                    if (path != null) {
                                        if (!new File(getBWOurDirectory() + "/" + path).exists()) {
                                            try {
                                                copyFile(new File(getBWhatsappDirectory() + "/" + path), new File(getBWOurDirectory() + "/" + path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                } else if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_IMAGE_ONLY)) {
                                    if (path != null && path.contains(".jpg")) {
                                        if (!new File(getBWOurDirectory() + "/" + path).exists()) {
                                            try {
                                                copyFile(new File(getBWhatsappDirectory() + "/" + path), new File(getBWOurDirectory() + "/" + path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (preferences.getString(whichBehaviour, "").equals(BEHAVIOUR_VIDEO_ONLY)) {
                                    if (path != null && path.contains(".mp4")) {
                                        if (!new File(getBWOurDirectory() + "/" + path).exists()) {
                                            try {
                                                copyFile(new File(getBWhatsappDirectory() + "/" + path), new File(getBWOurDirectory() + "/" + path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }
                        });


                    }

                }
            };
            observer.startWatching();
        }


        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {

                    try {
                        if (!previousText.equals(clipboardManager.getPrimaryClip().getItemAt(0).getText().toString())) {
                            url = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                            previousText = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                            registerReceiver(BackgoundRec, new IntentFilter(ACTION_BGDOWNLOAD));
                        } else {
                            url = "";
                        }

                    } catch (Exception e) {
                        Toasty.error(getBaseContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                    if (!url.isEmpty()) {

                        if (isYoutubeUrl(url)) {

                            collapsedView = new RemoteViews("com.collabcreation.statussaver",
                                    R.layout.notification_layout);
                            intent1 = new Intent(getApplicationContext(), YoutubeActivity.class)
                                    .putExtra(Common.URL_TYPE, Common.SINGLE_URL).putExtra("url", url);
                            showNotification(getApplicationContext(), intent1, collapsedView);


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    previousText = "";
                                }
                            }, CIRCLE_TIME);
                        }
                        else if (url.contains("instagram.com")) {
                            String requesturl = null;
                            final String Url[] = url.split("\\?");
                            if (Url[0].contains("https://www.instagram.com/p/") || Url[0].contains("https://www.instagram.com/tv/")) {
                                requesturl = Url[0] + "?__a=1";
                            } else {
                                requesturl = Url[0] + "/?__a=1";
                            }
                            StringRequest request = new StringRequest(Request.Method.GET, requesturl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    instaVideoModal = new Gson().fromJson(response, InstaVideoModal.class);
                                    if (instaVideoModal.getGraphql().getShortcodeMedia() == null) {
                                        collapsedView = new RemoteViews("com.collabcreation.statussaver",
                                                R.layout.notification_expand_layout);
                                        collapsedView.setTextViewText(R.id.text, "Download");
                                        collapsedView.setTextViewText(R.id.title, instaVideoModal.getGraphql().getUser().getUsername()
                                                + "(" + instaVideoModal.getGraphql().getUser().getFullName() + ")");
                                        profileurl = instaVideoModal.getGraphql().getUser().getProfilePicUrlHd();
                                        Log.d("profile url", "onResponse: " + profileurl + " uid " + instaVideoModal.getGraphql().getUser().getId());
                                        setUpNotificationForStory(instaVideoModal.getGraphql().getUser(), Common.INSTA_PROFILE);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                previousText = "";
                                            }
                                        }, CIRCLE_TIME);

                                    } else {
                                        if (instaVideoModal.getGraphql().getShortcodeMedia().getTypename().equals(Common.INSTA_VIDEO)) {
                                            if (!instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getIsPrivate()) {
                                                thumb = instaVideoModal.getGraphql().getShortcodeMedia().getDisplayResources().get(0).getSrc();
                                                setUpNotificationForSingle(instaVideoModal.getGraphql().getShortcodeMedia().getVideoUrl(), Common.INSTA_VIDEO);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        previousText = "";
                                                    }
                                                }, CIRCLE_TIME);
                                            } else {
                                                Toasty.error(getBaseContext(), "can not download from private account", Toasty.LENGTH_LONG).show();
                                            }


                                        } else if (instaVideoModal.getGraphql().getShortcodeMedia().getTypename().equals(Common.INSTA_IMAGE)) {
                                            if (!instaVideoModal.getGraphql().getShortcodeMedia().getOwner().getIsPrivate()) {
                                                thumb = instaVideoModal.getGraphql().getShortcodeMedia().getDisplayResources().get(0).getSrc();
                                                setUpNotificationForSingle(instaVideoModal.getGraphql().getShortcodeMedia().getDisplayResources().get(2).getSrc(), Common.INSTA_IMAGE);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        previousText = "";
                                                    }
                                                }, CIRCLE_TIME);
                                            } else {
                                                Toasty.error(getBaseContext(), "can not download from private account", Toasty.LENGTH_LONG).show();
                                            }
                                        } else if (instaVideoModal.getGraphql().getShortcodeMedia().getTypename().equals(Common.INSTA_SLIDE)) {
                                            urls = new ArrayList<>();
                                            urls.clear();
                                            thumb = instaVideoModal.getGraphql().getShortcodeMedia().getDisplayResources().get(0).getSrc();
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
                                            setUpNotification(urls);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    previousText = "";
                                                }
                                            }, CIRCLE_TIME);
                                        }
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast toast = Toasty.error(getBaseContext(), "Error while fetching! ", Toasty.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            });

                            RequestQueue requestQueue = Volley.newRequestQueue(OverlappService.this);
                            requestQueue.add(request);
                        }
                    }

                }
            });
        }


        BackgoundRec = new BroadcastReceiver() {
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
                    unregisterReceiver(BackgoundRec);
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
                    unregisterReceiver(BackgoundRec);
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
                    unregisterReceiver(BackgoundRec);
                }

            }
        };


    }

    private void setUpNotificationForStory(final User user, final String type) {
        final Intent[] intent = new Intent[1];
        collapsedView = new RemoteViews("com.collabcreation.statussaver",
                R.layout.notification_expand_layout);
        RequestBuilder requestBuilder = Glide.with(OverlappService.this)
                .asBitmap()
                .load(user.getProfilePicUrlHd())
                .placeholder(R.drawable.icon);
        requestBuilder.into(new SimpleTarget<Bitmap>() {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                intent[0] = new Intent(getApplicationContext(), InstaProfileActivity.class).putExtra(Common.MEDIA_TYPE, type).putExtra("user", user);
                showNotification(getApplicationContext(), intent[0], collapsedView);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                collapsedView.setImageViewBitmap(R.id.notification_image, resource);
                intent[0] = new Intent(getApplicationContext(), InstaProfileActivity.class).putExtra(Common.MEDIA_TYPE, type).putExtra("user", user);
                showNotification(getApplicationContext(), intent[0], collapsedView);
            }


            @Override
            public void removeCallback(@NonNull SizeReadyCallback cb) {

            }
        });


    }

    public static List<Story> stories(String userId, Context context) {
        List<Story> storyList = new ArrayList<>();
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL("https://i.instagram.com/api/v1/feed/user/" + userId + "/reel_media/").openConnection();
            httpConn.setRequestMethod(HttpGet.METHOD_NAME);
            httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
            httpConn.setRequestProperty("x-ig-capabilities", "3w==");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
            httpConn.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
            httpConn.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
            httpConn.setRequestProperty("authority", "i.instagram.com/");
            if (getUserId() == null) {
                setUserId(ZoomstaUtil.getStringPreference(context, "userid"));
            }
            if (getSessionid() == null) {
                setSessionId(ZoomstaUtil.getStringPreference(context, "sessionid"));
            }
            httpConn.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
            int responseCode = httpConn.getResponseCode();
            String result = buildResultString(httpConn);
            try {
                JSONArray array = new JSONObject(result).getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject itemObj = array.getJSONObject(i);
                    JSONArray video = itemObj.optJSONArray("video_versions");
                    Story story = new Story();
                    if (video != null) {
                        story.setMediaType(Story.TYPE_VIDEO);
                        JSONArray imageArray = itemObj.getJSONObject("image_versions2").getJSONArray("candidates");
                        story.setVideoThumb(imageArray.getJSONObject(imageArray.length() - 1).getString("url"));
                        story.setVideoUrl(video.getJSONObject(0).getString("url"));
                        storyList.add(story);
                        Log.d("Story Video", "stories: " + story.getDownloadUrl());
                    } else {
                        String url = itemObj.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url");
                        if (!url.endsWith(".jpg")) {
                            url = url + ".jpg";
                        }
                        story.setMediaType(Story.TYPE_IMAGE);
                        story.setImageUrl(url);
                        storyList.add(story);
                        Log.d("Story Image", "stories: " + story.getDownloadUrl());
                    }
                }


                /*for(int i=0; i<stories.size(); i++){
                    String model = stories.get(i);
                    Log.d("stories", "" + model);
                }*/
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e2) {
            System.out.println(e2);
        }
        return storyList;
    }

    public static class Stories extends AsyncTask<String, Void, List<Story>> {

        Context context;
        String uid;

        public Stories(Context context, String uid) {
            this.context = context;
            this.uid = uid;
        }

        protected List<Story> doInBackground(String... urls) {
            try {

                return stories(uid, context);
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            } finally {

            }
        }

        protected void onPostExecute(List<Story> stories) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public static List<UserObject> usersList(Context context) {

        URL url = null;
        try {
            url = new URL("https://i.instagram.com/api/v1/feed/reels_tray/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        try {
            httpURLConnection.setRequestMethod(HttpGet.METHOD_NAME);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        httpURLConnection.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpURLConnection.setRequestProperty("x-ig-capabilities", "3w==");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
        httpURLConnection.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
        httpURLConnection.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
        httpURLConnection.setRequestProperty("authority", "i.instagram.com/");

        if (getUserId() == null) {
            setUserId(ZoomstaUtil.getStringPreference(context, "userid"));
        }
        if (getSessionid() == null) {
            setSessionId(ZoomstaUtil.getStringPreference(context, "sessionid"));
        }
        httpURLConnection.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
        try {
            httpURLConnection.getResponseCode();
        } catch (IOException e22) {
            e22.printStackTrace();
        }
        String result = null;
        try {
            result = buildResultString(httpURLConnection);
        } catch (Exception e4) {
            e4.printStackTrace();
        }

        List<UserObject> userListResp = new ArrayList();
        try {
            JSONArray array = new JSONObject(result).getJSONArray("tray");
            for (int i = 0; i < array.length(); i++) {
                JSONObject userObj = array.getJSONObject(i).getJSONObject("user");
                UserObject object = new UserObject();
                object.setImage(userObj.get("profile_pic_url").toString());
                object.setRealName(userObj.get("full_name").toString());
                object.setUserName(userObj.get("username").toString());
                object.setUserId(userObj.get("pk").toString());
                userListResp.add(object);
                Log.d("User", "usersList: " + object.getImage());
            }
        } catch (Exception e42) {
            System.out.println(e42);
        }
        return userListResp;
    }

    private void setUpNotificationForSingle(final String urls, final String type) {
        if (!type.equals(Common.INSTA_PROFILE)) {
            collapsedView = new RemoteViews("com.collabcreation.statussaver",
                    R.layout.notification_expand_layout);
        }
        if (!thumb.isEmpty() || !profileurl.isEmpty()) {
            RequestBuilder requestBuilder = null;
            if (type.equals(Common.INSTA_PROFILE)) {
//                Toast.makeText(this, profileurl, Toast.LENGTH_SHORT).show();
                requestBuilder = Glide.with(OverlappService.this)
                        .asBitmap()
                        .load(profileurl)
                        .placeholder(R.drawable.icon);
            } else {
                requestBuilder = Glide.with(OverlappService.this)
                        .asBitmap()
                        .load(thumb)
                        .placeholder(R.drawable.icon);
            }
            requestBuilder.into(new SimpleTarget<Bitmap>() {
                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    intent1 = new Intent().setAction(ACTION_BGDOWNLOAD)
                            .putExtra(Common.URL_TYPE, Common.SINGLE_URL).putExtra(Common.MEDIA_TYPE, type).putExtra("url", urls);
                    showNotificationForSingle(getApplicationContext(), intent1, collapsedView);
                }

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    collapsedView.setImageViewBitmap(R.id.notification_image, resource);
                    intent1 = new Intent().setAction(ACTION_BGDOWNLOAD)
                            .putExtra(Common.URL_TYPE, Common.SINGLE_URL).putExtra(Common.MEDIA_TYPE, type).putExtra("url", urls);
                    showNotificationForSingle(getApplicationContext(), intent1, collapsedView);
                }


                @Override
                public void removeCallback(@NonNull SizeReadyCallback cb) {

                }
            });
        } else {
            collapsedView.setViewVisibility(R.id.notification_image, View.GONE);
            intent1 = new Intent().setAction(ACTION_BGDOWNLOAD)
                    .putExtra(Common.URL_TYPE, Common.SINGLE_URL).putExtra(Common.MEDIA_TYPE, type).putExtra("url", urls);
            showNotificationForSingle(getApplicationContext(), intent1, collapsedView);
        }

    }

    public static void setSessionId(String sessionIdInp) {
        session_id = sessionIdInp;
    }

    public static void setUserId(String ds_user_idInp) {
        user_id = ds_user_idInp;
    }

    public static String getUserId() {
        return user_id;
    }

    public static String getSessionid() {
        return session_id;
    }

    private static String buildResultString(HttpURLConnection httpconn) throws Exception {
        BufferedReader rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
        if ("gzip".equals(httpconn.getContentEncoding())) {
            rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(httpconn.getInputStream())));
        }
        StringBuffer result = new StringBuffer();
        String str;
        while (true) {
            str = rd.readLine();
            if (str == null) {
                return result.toString();
            }
            result.append(str);
        }
    }


    private void showNotificationForSingle(Context context, Intent intent, RemoteViews remoteView) {
        if (intent != null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel("StatusSAVER2019", "StatusSaver", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    channel.setAllowBubbles(true);
                }
                channel.setShowBadge(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                manager.createNotificationChannel(channel);

                Notification builder = new NotificationCompat.Builder(this, "StatusSAVER2019")
                        .setSmallIcon(R.drawable.icon)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setFullScreenIntent(PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT), true)
                        .setCustomContentView(remoteView)
                        .setContentIntent(PendingIntent.getBroadcast(context, 55,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setAutoCancel(true)
                        .build();

                manager.notify(10, builder);
                thumb = "";
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setCustomContentView(remoteView)
                        .setCustomBigContentView(remoteView)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setFullScreenIntent(PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT), true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(PendingIntent.getBroadcast(context, 5,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setAutoCancel(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setPriority(Notification.PRIORITY_HIGH);
                } else {
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }
                manager.notify(10, builder.build());
                thumb = "";
            }
        }


    }

    boolean isNamedProcessRunning(String processName) {
        if (processName == null)
            return false;

        ActivityManager manager =
                (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = null;
        if (manager != null) {
            processes = manager.getRunningAppProcesses();
        }
        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : process.pkgList) {
                        if (activeProcess.toLowerCase().contains(processName.toLowerCase())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private void setUpNotification(final ArrayList<String> urls) {
        collapsedView = new RemoteViews("com.collabcreation.statussaver",
                R.layout.notification_expand_layout);
        if (!thumb.isEmpty()) {
            Glide.with(OverlappService.this)
                    .asBitmap()
                    .load(thumb)
                    .placeholder(R.drawable.icon)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            intent1 = new Intent(getApplicationContext(), InstaSliderActivity.class)
                                    .putExtra(Common.URL_TYPE, Common.MULTI_URL).putStringArrayListExtra("urls", urls);
                            showNotification(getApplicationContext(), intent1, collapsedView);
                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            collapsedView.setImageViewBitmap(R.id.notification_image, resource);
                            intent1 = new Intent(getApplicationContext(), InstaSliderActivity.class)
                                    .putExtra(Common.URL_TYPE, Common.MULTI_URL).putStringArrayListExtra("urls", urls);
                            showNotification(getApplicationContext(), intent1, collapsedView);
                        }


                        @Override
                        public void removeCallback(@NonNull SizeReadyCallback cb) {

                        }
                    });
        } else {
            collapsedView.setViewVisibility(R.id.notification_image, View.GONE);
            intent1 = new Intent(getApplicationContext(), InstaSliderActivity.class)
                    .putExtra(Common.URL_TYPE, Common.MULTI_URL).putStringArrayListExtra("urls", urls);
            showNotification(getApplicationContext(), intent1, collapsedView);
        }
    }

    public static boolean isYoutubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!youTubeURl.isEmpty() && youTubeURl.matches(pattern)) {
            success = true;
        } else {
            // Not Valid youtube URL
            success = false;
        }
        return success;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (url != null) {
            url = null;
        }
        if (urls != null) {
            urls.clear();
        }
    }


    public void showNotification(Context context, Intent intent, RemoteViews remoteView) {
        if (intent != null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel("StatusSAVER2019", "StatusSaver", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    channel.setAllowBubbles(true);
                }
                channel.setShowBadge(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                manager.createNotificationChannel(channel);

                Notification builder = new NotificationCompat.Builder(this, "StatusSAVER2019")
                        .setSmallIcon(R.drawable.icon)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setFullScreenIntent(PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT), true)
                        .setCustomContentView(remoteView)
                        .setContentIntent(PendingIntent.getActivity(context, 55,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setAutoCancel(true)
                        .build();

                manager.notify(10, builder);
                thumb = "";
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setCustomContentView(remoteView)
                        .setCustomBigContentView(remoteView)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setFullScreenIntent(PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT), true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(PendingIntent.getActivity(context, 5,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setAutoCancel(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setPriority(Notification.PRIORITY_HIGH);
                } else {
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }
                manager.notify(10, builder.build());
                thumb = "";
            }
        }


    }
}

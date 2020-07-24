package com.collabcreation.statussaver.Activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.statussaver.Fragments.YoutubeDialog;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.YoutubeVideo;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.youtubeExtractor.VideoMeta;
import com.collabcreation.statussaver.youtubeExtractor.YouTubeExtractor;
import com.collabcreation.statussaver.youtubeExtractor.YtFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class YoutubeActivity extends AppCompatActivity implements YoutubeDialog.DialogDismiss {
    String url;
    YoutubeDialog bottomDialog;
    List<YoutubeVideo> youtubeVideos;
    ArrayList<YoutubeDialog.YtFragmentedVideo> formatsToShowList;
    private static final int ITAG_FOR_AUDIO = 140;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getStringExtra(Common.URL_TYPE).equals(Common.SINGLE_URL)) {
            url = getIntent().getStringExtra("url");


        }

        if (url != null) {
            Log.d("YOUTUBE URL", "onCreate: " +
                    url);
            getYoutubeDownloadUrl(url);
            dialog = new ProgressDialog(YoutubeActivity.this, R.style.Dialogtheme);
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.setTitle("Instant saver");
            dialog.setMessage("Fetching links to download....");
            dialog.show();

        }
    }

    private void getYoutubeDownloadUrl(final String youtubeLink) {
        @SuppressLint("StaticFieldLeak") YouTubeExtractor ytEx = new YouTubeExtractor(YoutubeActivity.this) {

            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
//                String url = "https://www.youtube.com/embed/" + videoMeta.getVideoId() + "?feature=oembed";
//                downloadFromUrl(url,videoMeta.getTitle().toString(),videoMeta.getTitle()+".mp4",false);

                if (ytFiles != null) {
                    formatsToShowList = new ArrayList<>();
                    formatsToShowList.clear();
                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);
                        Log.d("URI AVAILABLE", "onUrisAvailable: " + ytFile.getUrl());
                        if (ytFile.getMeta().getHeight() == -1 || ytFile.getMeta().getHeight() >= 360) {
                            addFormatToList(ytFile, ytFiles);
                        }
                    }
                    Collections.sort(formatsToShowList, new Comparator<YoutubeDialog.YtFragmentedVideo>() {
                        @Override
                        public int compare(YoutubeDialog.YtFragmentedVideo lhs, YoutubeDialog.YtFragmentedVideo rhs) {
                            return lhs.height - rhs.height;
                        }
                    });

                    youtubeVideos = new ArrayList<>();
                    youtubeVideos.clear();
                    for (YoutubeDialog.YtFragmentedVideo files : formatsToShowList) {
                        if (files.height != 1080 && files.height != 480)
                            youtubeVideos.add(new YoutubeVideo(videoMeta.getTitle(), files));
                        Log.d("Adding to dialog", "onExtractionComplete: " + videoMeta.getTitle() + " " + files.height);
                    }
                    bottomDialog = new YoutubeDialog(YoutubeActivity.this, getApplicationContext(), youtubeVideos);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    bottomDialog.show(getSupportFragmentManager(), "YOUTUBESLIDE");
                    Log.d("Complete adding ", "onExtractionComplete: bottomDialog");
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast toast = Toasty.error(getApplicationContext(), "Sorry! cannot download this video", Toasty.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    url = null;
                    finish();
                }
            }


        };
        ytEx.setIncludeWebM(false);
        ytEx.setParseDashManifest(true);
        ytEx.execute(youtubeLink);

    }

    private long downloadFromUrl(String youtubeUrl, String downloadTitle, String fileName, boolean hide) {
        Uri uri = Uri.parse(youtubeUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        if (hide) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        } else
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Toast toast = Toasty.success(getApplicationContext(), "Download Started", Toasty.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        long id = manager.enqueue(request);
        return id;
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getMeta().getHeight();
        if (height != -1) {
            for (YoutubeDialog.YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getMeta().getFps() == ytFile.getMeta().getFps())) {
                    return;
                }
            }
        }
        YoutubeDialog.YtFragmentedVideo frVideo = new YoutubeDialog.YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getMeta().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }

        formatsToShowList.add(frVideo);
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        url = null;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


}

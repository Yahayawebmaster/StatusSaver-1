package com.collabcreation.statussaver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import com.collabcreation.statussaver.Fragments.InstaBottomDialog;
import com.collabcreation.statussaver.Listener.InstaAdapter;
import com.collabcreation.statussaver.Modal.Common;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class InstaSliderActivity extends AppCompatActivity implements InstaAdapter, InstaBottomDialog.DialogDismiss {
    ArrayList<String> urls;
    InstaBottomDialog instaBottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra(Common.URL_TYPE).equals(Common.MULTI_URL)) {
            urls = new ArrayList<>();
            urls = getIntent().getStringArrayListExtra("urls");


        }

        if (urls != null) {

            instaBottomDialog = new InstaBottomDialog(getApplicationContext(),urls,InstaSliderActivity.this,InstaSliderActivity.this);
            instaBottomDialog.show(getSupportFragmentManager(),"INTASLIDE");
        }
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

    @Override
    public void onSingleImageClick(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String filename;
        if (url.contains(".jpg")) {
            filename = "Insta Image" + System.currentTimeMillis();
            request.setTitle(filename);

            request.setVisibleInDownloadsUi(false);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".jpg");
        } else {
            filename = "Insta Video" + System.currentTimeMillis();
            request.setTitle(filename);

            request.setVisibleInDownloadsUi(false);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".mp4");
        }

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast toast = Toasty.success(getApplicationContext(), "Download Started", Toasty.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        if (instaBottomDialog.isAdded()){
            instaBottomDialog.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        urls.clear();
        finish();
    }
}

package com.collabcreation.statussaver.Activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.statussaver.Fragments.InstaBottomProfileDialog;
import com.collabcreation.statussaver.Listener.InstaAdapter;
import com.collabcreation.statussaver.Modal.User;

import es.dmoral.toasty.Toasty;

public class InstaProfileActivity extends AppCompatActivity implements InstaAdapter, InstaBottomProfileDialog.DialogDismiss {
    User user;
    InstaBottomProfileDialog instaBottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {

            instaBottomDialog = new InstaBottomProfileDialog(getApplicationContext(), user, InstaProfileActivity.this, InstaProfileActivity.this);
            instaBottomDialog.show(getSupportFragmentManager(), "INTASLIDE");
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
            filename = "Insta Story" + System.currentTimeMillis();
            request.setTitle(filename);

            request.setVisibleInDownloadsUi(false);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + ".jpg");
        } else {
            filename = "Insta Story" + System.currentTimeMillis();
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
        if (instaBottomDialog.isAdded()) {
            instaBottomDialog.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        user = null;
        finish();
    }
}

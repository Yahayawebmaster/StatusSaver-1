package com.collabcreation.statussaver.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.collabcreation.statussaver.Adapter.SavedStatusAdapter;
import com.collabcreation.statussaver.Listener.SavedStatusListener;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.Status;
import com.collabcreation.statussaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.getThumb;

public class BWSavedActivity extends AppCompatActivity implements SavedStatusListener {
    ArrayList<Status> savedStatusList;
    RecyclerView savedList;
    ProgressBar process;
    SavedStatusAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bwsaved);
        savedList = findViewById(R.id.savedList);
        process = findViewById(R.id.process);
        swipeRefreshLayout = findViewById(R.id.refresh);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Saved Status");
        setSupportActionBar(toolbar);
        savedStatusList = new ArrayList<>();
        process.setVisibility(View.GONE);
        savedList.setHasFixedSize(true);
        savedList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        adapter = new SavedStatusAdapter(savedStatusList, getApplicationContext());
        adapter.setSavedStatusListener(this);
        savedList.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            getStatus();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    getStatus();
                }

            }
        });

    }

    private void getStatus() {
        if (Common.getBWOurDirectory().exists()) {
            process.setVisibility(View.VISIBLE);
            File[] statusFiles = Common.getBWOurDirectory().listFiles();
            if (statusFiles != null && statusFiles.length > 0) {
                Arrays.sort(statusFiles);
                savedStatusList.clear();
                for (File statusFile : statusFiles) {
                    Status status = new Status(statusFile, statusFile.getName(), statusFile.getAbsolutePath());
                    status.setThumbnail(getThumb(status));
                    if (!status.getFile().getName().endsWith(".nomedia")) {
                        savedStatusList.add(status);
                    }
                }


                adapter.notifyDataSetChanged();

            } else {
                Toasty.info(getApplicationContext(), "Nothing Saved", Toasty.LENGTH_SHORT).show();
            }
        }
        process.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStatus();
            } else {
                Toasty.warning(getApplicationContext(), "You must provide permission", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onThumnailClick(int postition, Status status) {
        View v = LayoutInflater.from(BWSavedActivity.this).inflate(R.layout.dialog, null, false);
        final Dialog dialog = new Dialog(BWSavedActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(v);
        dialog.show();
        ImageView thumbImg = v.findViewById(R.id.thumbImg);
        VideoView videoView = v.findViewById(R.id.thumbVideo);
        ImageView close = v.findViewById(R.id.close);

        if (status.isVideo()) {

            thumbImg.setVisibility(View.GONE);
            videoView.setVideoPath(status.getPath());
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            Glide.with(getApplicationContext())
                    .load(status.getFile())
                    .into(thumbImg);
            videoView.setVisibility(View.GONE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });
    }

    @Override
    public void onDeleteClick(final int position, final ArrayList<Status> statuses) {
        final File file = statuses.get(position).getFile();
        if (file.exists()) {
            final AlertDialog.Builder load = new AlertDialog.Builder(BWSavedActivity.this)
                    .setTitle("Delete")
                    .setMessage("Are you sure?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean isDeleted = file.delete();
                            if (isDeleted) {
                                Toasty.success(getApplicationContext(), "Deleted", Toasty.LENGTH_SHORT).show();
                                statuses.remove(position);
                                adapter.notifyItemRemoved(position);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri contentUri = Uri.fromFile(file);
                                    mediaScanIntent.setData(contentUri);
                                    sendBroadcast(mediaScanIntent);
                                } else {
                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));
                                }
                                if (statuses.isEmpty()) {
                                    finish();
                                }
                            } else {
                                Toasty.error(getApplicationContext(), "Failed to delete", Toasty.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog loadinng = load.create();
            loadinng.show();
        }
    }

    @Override
    public void onShareClick(Status status) {
        if (status.isVideo()) {
            Uri fileuri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", status.getFile());
            Intent share = new Intent(Intent.ACTION_SEND)
                    .setType("video/*")
                    .putExtra(Intent.EXTRA_STREAM, fileuri);
            startActivity(Intent.createChooser(share, "Share via"));
        } else {
            Uri fileuri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", status.getFile());
            Intent share = new Intent(Intent.ACTION_SEND)
                    .setType("image/*")
                    .putExtra(Intent.EXTRA_STREAM, fileuri);
            startActivity(Intent.createChooser(share, "Share via"));
        }
    }
}

package com.collabcreation.statussaver.Fragments;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.YoutubeVideo;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.youtubeExtractor.YtFile;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class YoutubeDialog extends BottomSheetDialogFragment {

    DialogDismiss dialogDismiss;
    List<YoutubeVideo> youtubeVideo;
    Context context;
    private LinearLayout mainLayout;
    ImageView close;

    public YoutubeDialog(DialogDismiss dialogDismiss, Context context, List<YoutubeVideo> youtubeVideo) {
        this.dialogDismiss = dialogDismiss;
        this.context = context;
        this.youtubeVideo = youtubeVideo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.youtubedownloadlayout, container, false);
        mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        close = view.findViewById(R.id.close);
        for (YoutubeVideo video : youtubeVideo) {
            Log.d("getting button", "onCreateView: " + video.getVideoTitle() + " " + video.getYtFrVideo().height + " ");
            addButtonToMainLayout(video.getVideoTitle(), video.getYtFrVideo());
        }
        Log.d("load done from dialog", "onCreateView: dialog ");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogDismiss.onDismiss(dialog);
    }


    private void addButtonToMainLayout(final String videoTitle, final YtFragmentedVideo ytFrVideo) {
        // Display some buttons and let the user choose the format
        String btnText;
        if (ytFrVideo.height == -1)
            btnText = "Audio " + ytFrVideo.audioFile.getMeta().getAudioBitrate() + " kbit/s";
        else

            btnText = (ytFrVideo.videoFile.getMeta().getFps() == 60) ? ytFrVideo.height + "p60" :
                    ytFrVideo.height + "p";
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);

        final Button btn = new Button(context);
        btn.setBackgroundResource(R.drawable.buttonbg);
        btn.setTextColor(Color.WHITE);
        btn.setLayoutParams(params);
        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(btn.getText().toString().contains("Audio")){
                    String filename;
                    if (videoTitle.length() > 55) {
                        filename = videoTitle.substring(0, 55);
                    } else {
                        filename = videoTitle;
                    }
                    filename = filename.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
                    filename += (ytFrVideo.height == -1) ? "" : "-" + ytFrVideo.height + "p";
                    String downloadIds = "";
                    boolean hideAudioDownloadNotification = false;
                    if (ytFrVideo.videoFile != null) {
                        downloadIds += downloadFromUrl(ytFrVideo.videoFile.getUrl(), videoTitle,
                                filename + "." + ytFrVideo.videoFile.getMeta().getExt(), false);
                        downloadIds += "-";
                        hideAudioDownloadNotification = true;
                    }
                    if (ytFrVideo.audioFile != null) {
                        downloadIds += downloadFromUrl(ytFrVideo.audioFile.getUrl(), videoTitle,
                                filename + "." + ytFrVideo.audioFile.getMeta().getExt(), hideAudioDownloadNotification);
                    }
                    if (ytFrVideo.audioFile != null)
                        cacheDownloadIds(downloadIds);

                }else {
                    String filename;
                    if (videoTitle.length() > 55) {
                        filename = videoTitle.substring(0, 55);
                    } else {
                        filename = videoTitle;
                    }
                    filename = filename.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
                    filename += (ytFrVideo.height == -1) ? "" : "-" + ytFrVideo.height + "p";
                    String downloadIds = "";
                    boolean hideAudioDownloadNotification = false;
                    if (ytFrVideo.videoFile != null) {
                        if (ytFrVideo.audioFile != null) {
                            dismiss();
                            Toasty.error(context, "Try other format!", Toasty.LENGTH_SHORT).show();
                            return;
                        }
                        downloadIds += downloadFromUrl(ytFrVideo.videoFile.getUrl(), videoTitle,
                                filename + "." + ytFrVideo.videoFile.getMeta().getExt(), false);
                        downloadIds += "-";
                        hideAudioDownloadNotification = true;
                    }
                    if (ytFrVideo.audioFile != null) {
                        dismiss();
                        Toasty.error(context, "Try other format!", Toasty.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        if (!btnText.contains("p60")) {
            mainLayout.addView(btn);
        }
    }

    private long downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName, boolean hide) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        if (hide) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        } else
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        long id = manager.enqueue(request);
        dismiss();
        return id;
    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(context.getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public interface DialogDismiss {
        void onDismiss(DialogInterface dialogInterface);
    }

    public static class YtFragmentedVideo {
        public int height;
        public YtFile audioFile;
        public YtFile videoFile;
    }

}

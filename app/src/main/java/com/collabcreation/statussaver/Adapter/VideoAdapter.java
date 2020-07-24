package com.collabcreation.statussaver.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.Status;
import com.collabcreation.statussaver.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.copyFile;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    ArrayList<Status> statuses;
    Context context;
    String which;
    private InterstitialAd mInterstitialAd;

    public VideoAdapter(ArrayList<Status> statuses, Context context, String which) {
        this.statuses = statuses;
        this.context = context;
        this.which = which;
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.fullscreenAd));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(listener);
    }

    private AdListener listener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_status, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Glide.with(context)
                .load(statuses.get(holder.getAdapterPosition()).getThumbnail())
                .into(holder.thumbnail);

        File destFile = null;
        switch (which) {
            case "GB":
                destFile = new File(Common.getGBOurDirectory() + File.separator + statuses.get(holder.getAdapterPosition()).getTitle());
                break;
            case "WH":
                destFile = new File(Common.getOurDirectory() + File.separator + statuses.get(holder.getAdapterPosition()).getTitle());

                break;
            case "BW":
                destFile = new File(Common.getBWOurDirectory() + File.separator + statuses.get(holder.getAdapterPosition()).getTitle());

                break;
        }
        if (destFile.exists()) {

            holder.download.setImageResource(R.drawable.ic_check_white_24dp);
            holder.download.setBackgroundResource(R.drawable.addbtnbggreen);


        } else {

            holder.download.setBackgroundResource(R.drawable.addbtnbg);
            holder.download.setImageResource(R.drawable.ic_file_download_black_24dp);


        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(context).inflate(R.layout.dialog, null, false);
                final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(v);
                dialog.show();
                ImageView thumbImg = v.findViewById(R.id.thumbImg);
                VideoView videoView = v.findViewById(R.id.thumbVideo);
                ImageView close = v.findViewById(R.id.close);

                thumbImg.setVisibility(View.GONE);
                videoView.setVideoPath(statuses.get(holder.getAdapterPosition()).getPath());
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });


                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });
            }
        });


        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File destFile = null;
                switch (which) {
                    case "GB":
                        destFile = new File(Common.getGBOurDirectory() + File.separator + statuses.get(holder.getAdapterPosition()).getTitle());

                        break;
                    case "WH":
                        destFile = new File(Common.getOurDirectory() + File.separator + statuses.get(holder.getAdapterPosition()).getTitle());

                        break;
                    case "BW":
                        destFile = new File(Common.getBWOurDirectory() + File.separator + statuses.get(holder.getAdapterPosition()).getTitle());

                        break;
                }
                if (destFile != null) {
                    if (destFile.exists()) {
                        Toasty.warning(context, "Status already saved", Toasty.LENGTH_SHORT).show();

                    } else {
                        try {
                            copyFile(statuses.get(holder.getAdapterPosition()).getFile(), destFile);
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                            Toasty.success(context, "Saved", Toasty.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(destFile);
                                mediaScanIntent.setData(contentUri);
                                context.sendBroadcast(mediaScanIntent);
                            } else {
                                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(destFile)));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        ImageView download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.ivThumbnail);
            download = itemView.findViewById(R.id.download);
        }
    }
}

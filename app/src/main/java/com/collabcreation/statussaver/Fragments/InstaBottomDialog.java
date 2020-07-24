package com.collabcreation.statussaver.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.collabcreation.statussaver.Listener.InstaAdapter;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class InstaBottomDialog extends BottomSheetDialogFragment {
    Context context;
    ArrayList<String> urls;
    ImageView dismiss;
    LinearLayout slider_layout;
    InstaAdapter listener;
    Button download;
    DialogDismiss dialogDismiss;

    public InstaBottomDialog(Context context, ArrayList<String> urls, InstaAdapter listener, DialogDismiss dialogDismiss) {
        this.context = context;
        this.urls = urls;
        this.listener = listener;
        this.dialogDismiss = dialogDismiss;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.instasliderlayout, container, false);
        slider_layout = v.findViewById(R.id.insta_main_layout);
        download = v.findViewById(R.id.download);
        dismiss = v.findViewById(R.id.dismiss);

        if (!urls.isEmpty()) {
            slider_layout.removeAllViews();
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutManager(new GridLayoutManager(
                    context, 2));
            recyclerView.setHasFixedSize(true);
            InstaDwnAdapter instaDwnAdapter = new InstaDwnAdapter();
            recyclerView.setAdapter(instaDwnAdapter);
            instaDwnAdapter.notifyDataSetChanged();
            slider_layout.addView(recyclerView);

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!urls.isEmpty()) {
                        for (int i = 0; i < urls.size(); i++) {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urls.get(i)));
                            String filename;
                            if (urls.get(i).contains(".jpg")) {
                                filename = "Insta Image" + System.currentTimeMillis();
                                request.setTitle(filename);

                                request.setVisibleInDownloadsUi(false);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), filename + ".jpg");
                            } else {
                                filename = "Insta Video" + System.currentTimeMillis();
                                request.setTitle(filename);

                                request.setVisibleInDownloadsUi(false);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), filename + ".mp4");
                            }

                            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                        }
                        Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        dismiss();
                    }

                }
            });

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        return v;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setCanceledOnTouchOutside(false);
    }

    public class InstaDwnAdapter extends RecyclerView.Adapter<InstaDwnAdapter.Holder> {


        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new InstaDwnAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.insta_slide_dw_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final InstaDwnAdapter.Holder holder, int position) {
            Glide.with(context)
                    .load(urls.get(holder.getAdapterPosition()))
                    .placeholder(R.drawable.icon)
                    .transition(DrawableTransitionOptions.withCrossFade(450))
                    .into(holder.image);

            if (urls.get(holder.getAdapterPosition()).contains(".jpg")) {
                holder.type.setImageResource(R.drawable.ic_image_black_24dp);
            } else {
                holder.type.setImageResource(R.drawable.ic_video_black_24dp);
            }


            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSingleImageClick(urls.get(holder.getAdapterPosition()));

                }
            });


        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            ImageView image, type;


            Holder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                type = itemView.findViewById(R.id.type);

            }
        }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface DialogDismiss {
        void onDismiss(DialogInterface dialogInterface);
    }
}

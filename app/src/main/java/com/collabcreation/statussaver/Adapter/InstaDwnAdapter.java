package com.collabcreation.statussaver.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.collabcreation.statussaver.Listener.InstaAdapter;
import com.collabcreation.statussaver.R;

import java.util.ArrayList;

public class InstaDwnAdapter extends RecyclerView.Adapter<InstaDwnAdapter.Holder> {

    ArrayList<String> urls;
    Context context;
    InstaAdapter listener;


    public InstaDwnAdapter(Context context, ArrayList<String> urls,InstaAdapter listener) {
        this.context = context;
        this.urls = urls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.insta_slide_dw_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
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
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urls.get(holder.getAdapterPosition())));
//                String filename;
//                if (urls.get(holder.getAdapterPosition()).contains(".jpg")) {
//                    filename = "Insta Image" + System.currentTimeMillis();
//                    request.setTitle(filename);
//
//                    request.setVisibleInDownloadsUi(false);
//
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//                    request.setDestinationInExternalPublicDir(Common.getInstaDirectory(), filename + ".jpg");
//                } else {
//                    filename = "Insta Video" + System.currentTimeMillis();
//                    request.setTitle(filename);
//
//                    request.setVisibleInDownloadsUi(false);
//
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//                    request.setDestinationInExternalPublicDir(Common.getInstaDirectory(), filename + ".mp4");
//                }
//
//                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                manager.enqueue(request);
//                Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_SHORT);
//                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 15);
//                toast.show();
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

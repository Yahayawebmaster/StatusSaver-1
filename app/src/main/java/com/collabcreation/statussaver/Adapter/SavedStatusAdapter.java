package com.collabcreation.statussaver.Adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.statussaver.Listener.SavedStatusListener;
import com.collabcreation.statussaver.Modal.Status;
import com.collabcreation.statussaver.R;

import java.util.ArrayList;

public class SavedStatusAdapter extends RecyclerView.Adapter<SavedStatusAdapter.ViewHolder> {
    ArrayList<Status> statuses;
    Context context;
    SavedStatusListener savedStatusListener;

    public SavedStatusAdapter(ArrayList<Status> statuses, Context context) {
        this.statuses = statuses;
        this.context = context;
    }

    public void setSavedStatusListener(SavedStatusListener savedStatusListener) {
        this.savedStatusListener = savedStatusListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_saved, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

                holder.thumbnail.setImageBitmap(statuses.get(holder.getAdapterPosition()).getThumbnail());

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        savedStatusListener.onShareClick(statuses.get(holder.getAdapterPosition()));
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        savedStatusListener.onDeleteClick(holder.getAdapterPosition(), statuses);
                    }
                });


                holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        savedStatusListener.onThumnailClick(holder.getAdapterPosition(), statuses.get(holder.getAdapterPosition()));
                    }
                });

    }


    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, delete, share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.ivThumbnail);
            delete = itemView.findViewById(R.id.delete);
            share = itemView.findViewById(R.id.share);
        }
    }
}

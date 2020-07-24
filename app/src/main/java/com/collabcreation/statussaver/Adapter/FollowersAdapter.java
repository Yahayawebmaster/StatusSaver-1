package com.collabcreation.statussaver.Adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.statussaver.Modal.UserObject;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.Service.OverlappService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.Holder> {
    List<UserObject> userObjects;
    Context context;

    public FollowersAdapter(Context context) throws ExecutionException, InterruptedException {
        this.userObjects = new GetStoriesFeed().execute().get();
        this.context = context;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        UserObject user = userObjects.get(holder.getAdapterPosition());
        Glide.with(context).load(user.getImage()).thumbnail(0.2f).into(holder.story_icon);
        holder.real_name.setText(user.getRealName());
        holder.user_name.setText(user.getUserName());
    }

    public class GetStoriesFeed extends AsyncTask<Void, String, List<UserObject>> {
        List<UserObject> list;

        private GetStoriesFeed() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected List<UserObject> doInBackground(Void... voids) {
            list.addAll(OverlappService.usersList(context));
            return list;
        }

        @Override
        protected void onPostExecute(final List<UserObject> userObjectList) {
            super.onPostExecute(userObjectList);
        }
    }

    @Override
    public int getItemCount() {
        return userObjects.size();
    }


    static class Holder extends RecyclerView.ViewHolder {
        CircleImageView story_icon;
        TextView user_name, real_name;

        Holder(@NonNull View itemView) {
            super(itemView);
            story_icon = itemView.findViewById(R.id.story_icon);
            real_name = itemView.findViewById(R.id.real_name);
            user_name = itemView.findViewById(R.id.user_name);
        }
    }
}

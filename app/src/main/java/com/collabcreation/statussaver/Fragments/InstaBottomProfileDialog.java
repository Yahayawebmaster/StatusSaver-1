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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.collabcreation.statussaver.Activity.InstagramOfficalLoginActivity;
import com.collabcreation.statussaver.Listener.InstaAdapter;
import com.collabcreation.statussaver.Modal.Story;
import com.collabcreation.statussaver.Modal.User;
import com.collabcreation.statussaver.Modal.ZoomstaUtil;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.Service.OverlappService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class InstaBottomProfileDialog extends BottomSheetDialogFragment {
    Context context;
    User user;
    ImageView dismiss;
    LinearLayout slider_layout, loginLayout;
    List<Story> storyList;
    InstaAdapter listener;
    Button download, btnLogin;
    CircleImageView profile;
    DialogDismiss dialogDismiss;
    Button buttonDownloadProfile;
    TextView storyTv;

    public InstaBottomProfileDialog(Context context, User user, InstaAdapter listener, DialogDismiss dialogDismiss) {
        this.context = context;
        this.user = user;
        this.storyList = new ArrayList<>();
        this.listener = listener;
        this.dialogDismiss = dialogDismiss;
        if (ZoomstaUtil.getBooleanPreference(context, "isLogin")) {
            try {
                this.storyList.addAll(new OverlappService.Stories(context, user.getId()).execute().get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.instaprofileout, container, false);
        slider_layout = v.findViewById(R.id.insta_main_layout);
        download = v.findViewById(R.id.download);
        dismiss = v.findViewById(R.id.dismiss);
        storyTv = v.findViewById(R.id.storyTv);
        btnLogin = v.findViewById(R.id.btnLogin);
        slider_layout.removeAllViews();
        loginLayout = v.findViewById(R.id.loginLayout);
        profile = v.findViewById(R.id.profile);
        buttonDownloadProfile = v.findViewById(R.id.btnDownloadProfile);
        storyTv.setText("Stories of " + user.getUsername());
        Glide.with(context)
                .load(user.getProfilePicUrlHd())
                .placeholder(R.drawable.icon)
                .error(new ColorDrawable(Color.RED))
                .into(profile);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                dismiss();
                startActivity(new Intent(getActivity(), InstagramOfficalLoginActivity.class));
            }
        });


        buttonDownloadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(user.getProfilePicUrlHd()));
                request.setTitle(user.getUsername() + " Profile");
                request.setVisibleInDownloadsUi(false);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), user.getUsername() + "Profile".trim() + ".jpg");
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                Toast toast = Toasty.success(context, "Download Started", Toasty.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                dismiss();
            }
        });

        if (!storyList.isEmpty()) {
            loginLayout.setVisibility(View.GONE);
            download.setVisibility(View.VISIBLE);
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
                    if (!storyList.isEmpty()) {
                        for (int i = 0; i < storyList.size(); i++) {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(storyList.get(i).getDownloadUrl()));
                            String filename;
                            if (!storyList.get(i).isVideo()) {
                                filename = "Insta Story" + System.currentTimeMillis();
                                request.setTitle(filename);

                                request.setVisibleInDownloadsUi(false);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), filename + ".jpg");
                            } else {
                                filename = "Insta Story" + System.currentTimeMillis();
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

        } else {
            download.setVisibility(View.GONE);
            if (!ZoomstaUtil.getBooleanPreference(context, "isLogin")) {
                loginLayout.setVisibility(View.VISIBLE);
            } else {
                loginLayout.setVisibility(View.GONE);
                storyTv.setText("");
            }
        }
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

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
            return new InstaDwnAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.insta_story_dw_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final InstaDwnAdapter.Holder holder, int position) {
            if (!storyList.get(holder.getAdapterPosition()).isVideo()) {
                Glide.with(context)
                        .load(storyList.get(holder.getAdapterPosition()).getImageUrl())
                        .placeholder(R.drawable.icon)
                        .transition(DrawableTransitionOptions.withCrossFade(450))
                        .into(holder.image);
                holder.type.setImageResource(R.drawable.ic_image_black_24dp);
            } else {
                Glide.with(context)
                        .load(storyList.get(holder.getAdapterPosition()).getVideoThumb())
                        .placeholder(R.drawable.icon)
                        .transition(DrawableTransitionOptions.withCrossFade(450))
                        .into(holder.image);
                holder.type.setImageResource(R.drawable.ic_video_black_24dp);
            }


            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSingleImageClick(storyList.get(holder.getAdapterPosition()).getDownloadUrl());

                }
            });


        }

        @Override
        public int getItemCount() {
            return storyList.size();
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

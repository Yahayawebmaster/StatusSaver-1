package com.collabcreation.statussaver.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.statussaver.Adapter.VideoAdapter;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.Status;
import com.collabcreation.statussaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.getOurDirectory;
import static com.collabcreation.statussaver.Modal.Common.getThumb;

public class VideosFragment extends Fragment {

    public ArrayList<Status> videoStatusList;
    public RecyclerView videoList;
    public ProgressBar process;
    public VideoAdapter adapter;
    TextView note;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment, container, false);
        videoList = view.findViewById(R.id.videoList);
        process = view.findViewById(R.id.process);
        note = view.findViewById(R.id.note);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoStatusList = new ArrayList<>();
        process.setVisibility(View.GONE);
        videoList.setHasFixedSize(true);
        videoList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new VideoAdapter(videoStatusList, getActivity(), "WH");
        videoList.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            getStatus();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    public void getStatus() {
        if (Common.getWhatsappDirectory().exists()) {
            process.setVisibility(View.VISIBLE);

            File[] statusFiles = Common.getWhatsappDirectory().listFiles();
            if (statusFiles != null && statusFiles.length > 0) {
                Arrays.sort(statusFiles);
                videoStatusList.clear();
                for (File statusFile : statusFiles) {
                    if (!new File(getOurDirectory() + File.separator + statusFile.getName()).exists()) {
                        Status status = new Status(statusFile, statusFile.getName(), statusFile.getAbsolutePath());
                        status.setThumbnail(getThumb(status));
                        if (status.isVideo() && !status.getFile().getName().endsWith(".nomedia")) {
                            videoStatusList.add(status);
                        }
                    }
                }
                if (videoStatusList.isEmpty()){
                    note.setVisibility(View.VISIBLE);
                }else {
                    note.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                process.setVisibility(View.GONE);

            }


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStatus();
            } else {
                Toasty.warning(getActivity(), "You must provide permission", Toasty.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

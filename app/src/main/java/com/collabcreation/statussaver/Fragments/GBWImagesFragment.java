package com.collabcreation.statussaver.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
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

import com.collabcreation.statussaver.Adapter.ImageAdapter;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.Status;
import com.collabcreation.statussaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

import static com.collabcreation.statussaver.Modal.Common.getOurDirectory;
import static com.collabcreation.statussaver.Modal.Common.getThumb;

public class GBWImagesFragment extends Fragment {

    ArrayList<Status> imageStatusList;
    RecyclerView imageList;
    ProgressBar process;
    ImageAdapter adapter;
    ProgressDialog dialog;
    TextView note;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        imageList = view.findViewById(R.id.imageList);
        process = view.findViewById(R.id.process);
        note = view.findViewById(R.id.note);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageStatusList = new ArrayList<>();
        process.setVisibility(View.GONE);
        dialog = new ProgressDialog(getActivity());
        imageList.setHasFixedSize(true);
        imageList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new ImageAdapter(imageStatusList, getActivity(), "GB");
        imageList.setAdapter(adapter);
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
        if (Common.getGBWhatsappDirectory().exists()) {
            process.setVisibility(View.VISIBLE);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            File[] statusFiles = Common.getGBWhatsappDirectory().listFiles();
            if (statusFiles != null && statusFiles.length > 0) {

                Arrays.sort(statusFiles);
                imageStatusList.clear();
                for (File statusFile : statusFiles) {
                    if (!new File(getOurDirectory() + File.separator + statusFile.getName()).exists()) {
                        Status status = new Status(statusFile, statusFile.getName(), statusFile.getAbsolutePath());
                        status.setThumbnail(getThumb(status));
                        if (!status.isVideo() && !status.getFile().getName().endsWith(".nomedia")) {
                            imageStatusList.add(status);
                        }
                    }

                }
                process.setVisibility(View.GONE);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                adapter.notifyDataSetChanged();
                if (imageStatusList.isEmpty()) {
                    note.setVisibility(View.VISIBLE);
                } else {
                    note.setVisibility(View.GONE);
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
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
}

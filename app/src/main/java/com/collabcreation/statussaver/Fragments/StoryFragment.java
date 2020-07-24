package com.collabcreation.statussaver.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.statussaver.Adapter.FollowersAdapter;
import com.collabcreation.statussaver.Modal.UserObject;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.Service.OverlappService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoryFragment extends Fragment {

    List<UserObject> userObjects;

    public StoryFragment() {
        this.userObjects = OverlappService.usersList(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        return view;
    }



}

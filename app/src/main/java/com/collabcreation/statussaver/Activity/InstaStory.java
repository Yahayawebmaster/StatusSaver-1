package com.collabcreation.statussaver.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.statussaver.Adapter.FollowersAdapter;
import com.collabcreation.statussaver.R;

import java.util.concurrent.ExecutionException;

public class InstaStory extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView accounts;
    FollowersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_story);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram Story");
        setSupportActionBar(toolbar);
        accounts = findViewById(R.id.accounts);
        try {
            adapter = new FollowersAdapter(getApplicationContext());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        accounts.setHasFixedSize(true);
        accounts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        accounts.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

package com.collabcreation.statussaver.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.collabcreation.statussaver.Adapter.GBWAdapter;
import com.collabcreation.statussaver.Fragments.GBWImagesFragment;
import com.collabcreation.statussaver.Fragments.GBWVideosFragment;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import es.dmoral.toasty.Toasty;

public class GBWhatsapp extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    GBWVideosFragment videosFragment;
    Button btnSaved;
    AdView adView;
    GBWImagesFragment imagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gbwhatsapp);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = findViewById(R.id.tabLayout);
        btnSaved = findViewById(R.id.btnSave);
        adView = findViewById(R.id.adView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("GB Whatsapp Status");
        setSupportActionBar(toolbar);

        videosFragment = new GBWVideosFragment();
        imagesFragment = new GBWImagesFragment();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Common.getGBOurDirectory();
        } else {
            Toasty.warning(getApplicationContext(), "You must provide permission", Toasty.LENGTH_SHORT).show();
        }

        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GBSavedActivity.class));

            }
        });


        viewPager.setAdapter(new GBWAdapter(getSupportFragmentManager(), videosFragment, imagesFragment));
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            ProgressDialog dialog = new ProgressDialog(GBWhatsapp.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
//            dialog.show();
            imagesFragment.getStatus();
            videosFragment.getStatus();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        adView.loadAd(new AdRequest.Builder().build());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Common.getGBOurDirectory();
            } else {
                Toasty.warning(getApplicationContext(), "You must provide permission", Toasty.LENGTH_SHORT).show();
            }
        }
    }


}

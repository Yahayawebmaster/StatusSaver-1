package com.collabcreation.statussaver.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.collabcreation.statussaver.Adapter.PagerAdapter;
import com.collabcreation.statussaver.Fragments.ImagesFragment;
import com.collabcreation.statussaver.Fragments.VideosFragment;
import com.collabcreation.statussaver.Modal.Common;
import com.collabcreation.statussaver.Modal.ZoomstaUtil;
import com.collabcreation.statussaver.R;
import com.collabcreation.statussaver.Service.OverlappService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    VideosFragment videosFragment;
    NavigationView navigationView;
    Button btnSaved;
    DrawerLayout drawer;
    ImagesFragment imagesFragment;
    SharedPreferences sharedPreferences, autoSavePref;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = findViewById(R.id.tabLayout);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnSaved = findViewById(R.id.btnSave);
        toolbar = findViewById(R.id.toolbar);
        adView = findViewById(R.id.adView);
        toolbar.setNavigationIcon(R.drawable.actionbar_icon);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences("first", MODE_PRIVATE);
        if (preferences.getBoolean("isFirst", true)) {
            //new features dialog
            showDialog();
        }


        sharedPreferences = getSharedPreferences(Common.BG_SERVICE, MODE_PRIVATE);

        if (!checkServiceRunning(OverlappService.class)) {

            if (sharedPreferences.getBoolean(Common.isBgTrue, false)) {
                startService(new Intent(getApplicationContext(), OverlappService.class));
            }

        }

        autoSavePref = getSharedPreferences(Common.AUTO_STATUS_SAVER, MODE_PRIVATE);
        if (autoSavePref.getString(Common.whichBehaviour, "").isEmpty()) {
            autoSavePref.edit().putString(Common.whichBehaviour, Common.BEHAVIOUR_BOTH).apply();
        }

        if (autoSavePref.getBoolean(Common.isBgTrue, false)) {
            sharedPreferences.edit().putBoolean(Common.isBgTrue, true).apply();
            if (!checkServiceRunning(OverlappService.class)) {
                startService(new Intent(getApplicationContext(), OverlappService.class));
            }
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        videosFragment = new VideosFragment();
        imagesFragment = new ImagesFragment();


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Common.getOurDirectory();
        } else {
            Toasty.warning(getApplicationContext(), "You must provide permission", Toasty.LENGTH_SHORT).show();
        }

        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SavedActivity.class));

            }
        });


        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), videosFragment, imagesFragment));
        tabLayout.setupWithViewPager(viewPager);


    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Common.getOurDirectory();
            } else {
                Toasty.warning(getApplicationContext(), "You must provide permission", Toasty.LENGTH_SHORT).show();
            }
        }
    }


    public void showDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("How to start Background downloader");
        dialog.setContentView(getLayoutInflater().inflate(R.layout.bg_service_dialog, null, false));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enable Background Downloader")
                .setMessage("Using this feature you can download post and videos by copying links from instagram and youtube. \n\nWould you like to enable this feature ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    SharedPreferences sharedPreferences = getSharedPreferences(Common.BG_SERVICE, MODE_PRIVATE);

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!checkServiceRunning(OverlappService.class)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(Common.isBgTrue, true).apply();
                            startService(new Intent(getApplicationContext(), OverlappService.class));
                            Toasty.success(getApplicationContext(), "Background Downloader Started", Toasty.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dialog.show();
                    }
                });

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Feature Update")
                .setMessage(Html.fromHtml("<b>1.</b> Added Status Saver for GBWhatsapp and BWhatsapp. <br><br><b>2.</b> View your saved statuses. <br><br><b>3.</b> You can download videos and post from background.<br><br><b>4.</b> Added Auto Status Saver. "))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        builder2.create().show();
                    }
                });
        builder1.create().show();
        SharedPreferences preferences = getSharedPreferences("first", MODE_PRIVATE);
        preferences.edit().putBoolean("isFirst", false).apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_whatsapp:
                break;
            case R.id.nav_dm:
                startActivity(new Intent(getApplicationContext(), DirectMessage.class));
                break;
            case R.id.nav_bwhatsapp:
                if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/WhatsApp Business/Media").exists()) {
                    startActivity(new Intent(getApplicationContext(), BusinessWhatsapp.class));
                } else {
                    Toasty.error(getApplicationContext(), "You haven't Business Whatsapp", Toasty.LENGTH_SHORT).show();
                    navigationView.setCheckedItem(R.id.nav_whatsapp);
                }
                break;
            case R.id.GB:
                if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/GBWhatsApp/Media").exists()) {
                    startActivity(new Intent(getApplicationContext(), GBWhatsapp.class));
                } else {
                    Toasty.error(getApplicationContext(), "You haven't GB Whatsapp", Toasty.LENGTH_SHORT).show();
                    navigationView.setCheckedItem(R.id.nav_whatsapp);
                }
                break;
            case R.id.bg_service:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.follow:
                String acname = "instagramaccount";
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("http://instagram.com/_u/" + acname))
                            .setPackage("com.instagram.android");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/" + acname)));
                }
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Loading....");
                dialog.setCancelable(false);
//            dialog.show();
                imagesFragment.getStatus();
                videosFragment.getStatus();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                break;
            case R.id.login:
                if (ZoomstaUtil.getBooleanPreference(getApplicationContext(), "isLogin")) {
                    ZoomstaUtil.setBooleanPreference(getApplicationContext(), "isLogin", false);
                    invalidateOptionsMenu();
                } else {
                    startActivity(new Intent(getApplicationContext(), InstagramOfficalLoginActivity.class));
                }
                break;
        }
        return true;
    }


    public boolean checkServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (ZoomstaUtil.getBooleanPreference(getApplicationContext(), "isLogin")) {
            menu.findItem(R.id.login).setTitle("Logout");
        } else {
            menu.findItem(R.id.login).setTitle("Login");
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.loadAd(new AdRequest.Builder().build());
        invalidateOptionsMenu();
        navigationView.setCheckedItem(R.id.nav_whatsapp);
        Menu nav_Menu = navigationView.getMenu();
        if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/WhatsApp Business/Media").exists()) {
            nav_Menu.findItem(R.id.nav_bwhatsapp).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_bwhatsapp).setVisible(false);
        }

        if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/GBWhatsApp/Media").exists()) {
            nav_Menu.findItem(R.id.GB).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.GB).setVisible(false);
        }


    }
}

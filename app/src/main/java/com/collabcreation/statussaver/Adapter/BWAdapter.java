package com.collabcreation.statussaver.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.collabcreation.statussaver.Fragments.BWImagesFragment;
import com.collabcreation.statussaver.Fragments.BWVideosFragment;
import com.collabcreation.statussaver.Fragments.ImagesFragment;
import com.collabcreation.statussaver.Fragments.VideosFragment;

public class BWAdapter extends FragmentPagerAdapter {

    private String[] PAGE_TITLE = {"Images", "Videos"};
    BWVideosFragment videosFragment;
    BWImagesFragment imagesFragment;


    public BWAdapter(@NonNull FragmentManager fm, BWVideosFragment videosFragment, BWImagesFragment imagesFragment) {
        super(fm);
        this.videosFragment = videosFragment;
        this.imagesFragment = imagesFragment;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = imagesFragment;
                break;
            case 1:
                fragment = videosFragment;
                break;
            default:
                fragment = imagesFragment;
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLE[position];
    }

    @Override
    public int getCount() {
        return PAGE_TITLE.length;
    }
}

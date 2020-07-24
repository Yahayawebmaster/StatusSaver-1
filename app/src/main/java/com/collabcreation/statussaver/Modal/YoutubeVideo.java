package com.collabcreation.statussaver.Modal;

import com.collabcreation.statussaver.Fragments.YoutubeDialog;

public class YoutubeVideo {
    String videoTitle;
    YoutubeDialog.YtFragmentedVideo ytFrVideo;

    public YoutubeVideo(String videoTitle, YoutubeDialog.YtFragmentedVideo ytFrVideo) {
        this.videoTitle = videoTitle;
        this.ytFrVideo = ytFrVideo;
    }

    public String getVideoTitle() {
        return videoTitle;
    }


    public YoutubeDialog.YtFragmentedVideo getYtFrVideo() {
        return ytFrVideo;
    }


}

package com.collabcreation.statussaver.Modal;

import android.graphics.Bitmap;

import java.io.File;

public class Status {

    public static String MP4 = ".mp4";
    private File file;
    private Bitmap thumbnail;
    private String title, path;
    private boolean isVideo;
    private boolean status;

    public Status(File file, String title, String path) {
        this.file = file;
        this.title = title;
        this.path = path;
        this.isVideo = file.getName().endsWith(MP4);
        this.status = false;
    }

    public File getFile() {
        return file;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}

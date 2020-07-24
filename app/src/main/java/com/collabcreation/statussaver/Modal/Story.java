package com.collabcreation.statussaver.Modal;

public class Story {
    private String videoUrl, videoThumb, imageUrl, mediaType;
    public static final String TYPE_IMAGE = "imageType";
    public static final String TYPE_VIDEO = "videoType";

    public Story(String videoUrl, String videoThumb, String imageUrl) {
        this.videoUrl = videoUrl;
        this.videoThumb = videoThumb;
        this.imageUrl = imageUrl;
    }

    private String getMediaType() {
        return mediaType;
    }

    public String getDownloadUrl() {
        if (isVideo()) {
            return getVideoUrl();
        } else {
            return getImageUrl();
        }
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isVideo() {
        return getMediaType().equals(TYPE_VIDEO);
    }

    public Story() {
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

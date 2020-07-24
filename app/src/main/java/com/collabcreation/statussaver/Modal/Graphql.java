package com.collabcreation.statussaver.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Graphql {

    @SerializedName("shortcode_media")
    @Expose
    private ShortcodeMedia shortcodeMedia;

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public ShortcodeMedia getShortcodeMedia() {
        return shortcodeMedia;
    }



}
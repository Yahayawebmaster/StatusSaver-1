package com.collabcreation.statussaver.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {


    @SerializedName("full_name")
    @Expose
    private String fullName;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("profile_pic_url_hd")
    @Expose
    private String profilePicUrlHd;

    @SerializedName("username")
    @Expose
    private String username;

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }


    public String getProfilePicUrlHd() {
        return profilePicUrlHd;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfilePicUrlHd(String profilePicUrlHd) {
        this.profilePicUrlHd = profilePicUrlHd;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


}

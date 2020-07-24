package com.collabcreation.statussaver.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DisplayResource {

    @SerializedName("src")
    @Expose
    private String src;


    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }


}

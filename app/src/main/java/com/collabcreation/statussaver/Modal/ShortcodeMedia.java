package com.collabcreation.statussaver.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShortcodeMedia {

    @SerializedName("display_resources")
    @Expose
    private List<DisplayResource> displayResources = null;

    @SerializedName("edge_sidecar_to_children")
    @Expose
    private EdgeSidecarToChildren edgeSidecarToChildren;

    @SerializedName("owner")
    @Expose
    private Owner owner;

    @SerializedName("video_url")
    @Expose
    private String videoUrl;

    @SerializedName("__typename")
    @Expose
    private String typename;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public List<DisplayResource> getDisplayResources() {
        return displayResources;
    }

    public void setDisplayResources(List<DisplayResource> displayResources) {
        this.displayResources = displayResources;
    }

    public Owner getOwner() {
        return owner;
    }

    public EdgeSidecarToChildren getEdgeSidecarToChildren() {
        return edgeSidecarToChildren;
    }

    public void setEdgeSidecarToChildren(EdgeSidecarToChildren edgeSidecarToChildren) {
        this.edgeSidecarToChildren = edgeSidecarToChildren;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}

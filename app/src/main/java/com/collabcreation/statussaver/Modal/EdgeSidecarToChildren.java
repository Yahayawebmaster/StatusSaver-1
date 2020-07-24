package com.collabcreation.statussaver.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EdgeSidecarToChildren {

    @SerializedName("edges")
    @Expose
    private List<Edge> edges = null;

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

}

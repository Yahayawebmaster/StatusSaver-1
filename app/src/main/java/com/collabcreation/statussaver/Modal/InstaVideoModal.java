package com.collabcreation.statussaver.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InstaVideoModal {
    @SerializedName("graphql")
    @Expose
    private Graphql graphql;

    public Graphql getGraphql() {
        return graphql;
    }

}

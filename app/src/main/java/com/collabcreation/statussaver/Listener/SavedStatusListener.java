package com.collabcreation.statussaver.Listener;

import com.collabcreation.statussaver.Modal.Status;

import java.util.ArrayList;

public interface SavedStatusListener {

    void onThumnailClick(int postition, Status status);
    void onDeleteClick(int position, ArrayList<Status> statuses);
    void onShareClick(Status status);

}

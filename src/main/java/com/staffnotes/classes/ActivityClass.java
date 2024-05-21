package com.staffnotes.classes;

import java.util.List;

public class ActivityClass {
    private String activity;
    private List<Integer> dataKeys;
    public ActivityClass(String activity, List<Integer> dataKeys){
        this.activity = activity;
        this.dataKeys = dataKeys;
    }

    public String getActivity() {
        return activity;
    }
    public List<Integer> getDataKeys(){
        return dataKeys;
    }
}

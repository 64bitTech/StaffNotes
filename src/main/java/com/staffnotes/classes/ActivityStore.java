package com.staffnotes.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityStore {
    private static Map<UUID,ActivityClass> activity = new HashMap<>();
    public static ActivityClass getActivity(UUID uuid){
        return activity.get(uuid);
    }
    public static void setActivity(UUID uuid, ActivityClass newActivity){
        activity.put(uuid,newActivity);
    }
    public static boolean removeActivity(UUID uuid){
        activity.remove(uuid);
        return true;
    }

}

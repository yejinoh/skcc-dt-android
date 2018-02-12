package com.example.yj.itproject_07;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by multi on 2016-12-09.
 */
public class ActivityManager {

    private static ActivityManager activityMananger = null;
    private ArrayList<Activity> activityList = null;

    private ActivityManager() {
        activityList = new ArrayList<Activity>();
    }

    public static ActivityManager getInstance() {

        if (ActivityManager.activityMananger == null) {
            activityMananger = new ActivityManager();
        }
        return activityMananger;
    }

    /**
     * 액티비티 리스트 getter.
     * 26.     * @return activityList
     * 27.
     */
    public ArrayList<Activity> getActivityList() {
        return activityList;
    }

    /**
     * 33.     * 액티비티 리스트에 추가.
     * 34.     * @param activity
     * 35.
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 41.     * 액티비티 리스트에서 삭제.
     * 42.     * @param activity
     * 43.     * @return boolean
     * 44.
     */
    public boolean removeActivity(Activity activity) {
        return activityList.remove(activity);
    }

    /**
     * 50.     * 모든 액티비티 종료.
     * 51.
     */
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}


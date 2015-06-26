package com.example.Radio;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by SC on 28.05.2015.
 */
public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                MainActivity.selectStationTab();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                MainActivity.selectPlayTab();
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}

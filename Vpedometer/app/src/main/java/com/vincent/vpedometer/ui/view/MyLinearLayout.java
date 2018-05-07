package com.vincent.vpedometer.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.vincent.vpedometer.ui.view.SlideMenu.DragState;

/**
 * Created by Administrator on 2018/1/30.
 */

public class MyLinearLayout extends LinearLayout {

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    private static SlideMenu slideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int childCount = getChildCount();
        Log.i("MyLinearLayout", childCount + "");
        if (slideMenu != null && slideMenu.getCurrentState() == DragState.Open) {
            //intercept the slide event
            return true;
        }
        return super.onInterceptTouchEvent(ev);
        //return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu != null && slideMenu.getCurrentState() == DragState.Open) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //close sile menu when the motion event is action up
                slideMenu.close();
            }

            //intercept the slide event
            return true;

        } else if (slideMenu != null && slideMenu.getCurrentState() == DragState.Close) {
            //intercept the event in order not returning to slide menu.
            return true;
        }
        return super.onTouchEvent(event);
    }
}

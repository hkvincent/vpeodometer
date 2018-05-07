package com.vincent.vpedometer.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/2/27 17:51
 */
public class MyListView extends FrameLayout {

    private Scroller scroller;


    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    /**
     * get how many spec to each child view
     * @param widthMeasureSpec the width
     * @param heightMeasureSpec the height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * how to layout the child view
     * @param changed
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //place the child on the view
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.layout(0, i * getHeight(), getWidth(), (i + 1) * getHeight());
        }


    }

    float startX;
    float startY;
    float downX;
    float downY;
    float endX;
    float endY;
    private int currentIndex;


    /**
     * determine the touch event need to go to child view or not
     *
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        boolean flag = false;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("onInterceptTouchEvent ACTION_DOWN result=" + flag);
                //click down evnet
                downY = startY = event.getY();
                downX = startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //determine the event is correct or not
                float endX = event.getX();
                float endY = event.getY();

                float distanceX = Math.abs(endX - downX);
                float distanceY = Math.abs(endY - downY);

                if (distanceY > distanceX && distanceY > 5) {
                    flag = true;
                } else {
                    changePage(currentIndex);
                }

                System.out.println("onInterceptTouchEvent ACTION_MOVE result=" + flag);
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("onInterceptTouchEvent ACTION_UP result=" + flag);
                break;
            default:
                break;
        }
        return flag;

    }


    /**
     * get user touch event
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "SlideLayout-onTouchEvent-ACTION_DOWN");
                //1.click down
                downY = startY = event.getY();
                downX = startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "SlideLayout-onTouchEvent-ACTION_MOVE");
                endY = event.getY();
                float disY = endY - startY;
                float disX = endX - startX;


                MyListView.this.scrollBy(0, (int) -disY);
                startY = endY;
                startX = endX;
                //when myListView is moving, we set his parent can not consume the event
                if (Math.abs(disY) > Math.abs(disX) && Math.abs(disY) > 8) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "SlideLayout-onTouchEvent-ACTION_UP");
                if (endY - downY > getHeight() / 2) {
                    changePage(--currentIndex);
                } else if (downY - endY > getHeight() / 2) {
                    changePage(++currentIndex);
                } else {
                    changePage(currentIndex);
                }
                break;
        }

        return true;
    }


    /**
     * change page by index
     * @param index the page index
     */
    private void changePage(int index) {
        if (index < 0) {
            index = 0;
        } else if (index > getChildCount() - 1) {
            index = getChildCount() - 1;
        }
        currentIndex = index;

        //get the dinstance of change
        int distanceY = currentIndex * getHeight() - getScrollY();
        //use scroller to scrol the page
        scroller.startScroll(0, getScrollY(), 0, distanceY, Math.abs(distanceY));
        invalidate();
    }

    /**
     * when invalidate is call the computeScroll will be invoked
     */
    @Override
    public void computeScroll() {
        //determine the animation is over or not
        if (scroller.computeScrollOffset()) {
            //scroll the page
            float currY = scroller.getCurrY();
            scrollTo(0, (int) currY);
            invalidate();
        }
    }
}

package com.vincent.vpedometer.ui.view;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;
import com.vincent.vpedometer.utils.ColorUtil;

/**
 * Created by Administrator on 2018/1/30.
 */

public class SlideMenu extends FrameLayout {
    private View menuView;//the menu view
    private View mainView;//the main view
    private ViewDragHelper viewDragHelper;
    private int width;
    private float dragRange;//the range you can drag
    private FloatEvaluator floatEvaluator;//float calculator
    private IntEvaluator intEvaluator;//int calculator

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    //enum class has open and close status
    public enum DragState {
        Open, Close;
    }

    private DragState currentState = DragState.Close;//SlideMenu default stauts is close

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    /**
     * the get slidemenu status
     *
     * @return
     */
    public DragState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DragState state) {
        this.currentState = state;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //the custom view can not contain two child view and this 2 child view must be menu view and main view
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlideMenu only have 2 children!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    /**
     * the size  change method will be invoked after the onMeasure method completing,
     * you can resize  the child view highet and width
     *
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.6f;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * determine which child view is being touched
         *
         * child: the touched view
         * return: true:capture the event and consume it false：do not capture the event
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        /**
         *get the horizontal position of the view to return.
         *
         */
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        /**
         * the constrain position of the view horizontal that user can move
         */
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                Log.d("child == mainView", "true");
                if (left < 0) left = 0;//constrain the left of mainView
                if (left > dragRange) left = (int) dragRange;//constrain the right of mainView
            }
            return left;
        }

        /**
         * when the child view position changing the other child view will be moved at the same time
         * left：the current left position of the child, the current right postion of the child
         * the dx  is horizontal position that has moved, dy is the vertical position that has moved
         */
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == menuView) {
                //fixed the menuView
                menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
                //let the mainView moving
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;//constrain the left of the mainView
                if (newLeft > dragRange) newLeft = (int) dragRange;//constrain the right of the mainView
                mainView.layout(newLeft, mainView.getTop() + dy, newLeft + mainView.getMeasuredWidth(), mainView.getBottom() + dy);
            }

            //1. the  fraction of the slipping
            float fraction = mainView.getLeft() / dragRange;
            Log.i("fraction", "fraction:" + fraction + "mainView.getLeft():" + mainView.getLeft() + "dragRange:" + dragRange);
            //2. execute the animation
            executeAnim(fraction);
            //3.when the main view is in 0 position then close the menu
            if (fraction == 0 && currentState != DragState.Close) {
                //close the menus call the close method of the listener
                currentState = DragState.Close;
                if (listener != null) listener.onClose();

            } else if (fraction > 0.99999 && currentState != DragState.Open) {
                //open the menu and call the open method of the listener
                currentState = DragState.Open;
                if (listener != null) listener.onOpen();
            }
            //shaking the icon
            if (listener != null) {
                listener.onDraging(fraction);
            }
        }

        /**
         * when touch is move away from the screen,   xvel, the speed of the x moving, positive value is turn on the right
         * the negative value is move on the left
         */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft() < dragRange / 2) {
                //the main view is on the left so close the menu view automatically
                close();
            } else {
                //the main view is on the right so close the menu view automatically
                open();
            }

            //when the speed if more than 200 the action will be taken
            if (xvel > 200 && currentState != DragState.Open) {
                open();
            } else if (xvel < -200 && currentState != DragState.Close) {
                close();
            }
        }
    };

    /**
     * close the menu slide
     */
    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * open the menu slide
     */
    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * animation will be exectued when silde menus change
     * <p>
     * using viewHelper to do the animation
     *
     * @param fraction
     */
    private void executeAnim(float fraction) {
        //fraction:0-1
        //zoom out mainView
//		float scaleValue = 0.8f+0.2f*(1-fraction);//1-0.8f
        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
        //move the menuView
        ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(fraction, -menuView.getMeasuredWidth() / 2, 0));
        //zoom in menuView
        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
        //change the alpha oif menuView
        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));

        //fill up black color to the background of SlideMenu
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
    }

    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    ;

    private OnDragStateChangeListener listener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnDragStateChangeListener {
        /**
         * the callback method to open the sildemenu
         */
        void onOpen();

        /**
         * the callback method to close the sildemenu
         */
        void onClose();

        /**
         * the callback method on dragging
         */
        void onDraging(float fraction);
    }


    public void setMainView(View v) {
        this.removeView(mainView);
        this.mainView = v;

        System.out.println("menu" + menuView);
        //need to set the current state being close,because when we change the main view
        //the sile menu will be close,if we do not do that,the MyLinearLayout event will not to
        //pass to its children.
        currentState = DragState.Close;

    }

    public void setMenuView(View v) {
        this.removeView(menuView);
        this.menuView = v;
        System.out.println("menu" + menuView);

    }
}

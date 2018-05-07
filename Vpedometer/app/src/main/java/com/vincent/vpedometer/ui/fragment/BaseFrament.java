package com.vincent.vpedometer.ui.fragment;

import android.content.Context;
import android.view.View;

import com.vincent.vpedometer.ui.activity.MainActivity;
import com.vincent.vpedometer.ui.view.MyLinearLayout;
import com.vincent.vpedometer.ui.view.SlideMenu;

/**
 * Created by Administrator on 2018/2/11 21:44
 */
public abstract class BaseFrament  {
    Context mContext;
    SlideMenu mSlideMenu;
    public MyLinearLayout mMyView;
    public MainActivity mMainActivity;
    public View rootView;


    public abstract void init();

    public abstract BaseFrament getMe();

}

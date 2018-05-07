package com.vincent.vpedometer.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.pojo.User;
import com.vincent.vpedometer.ui.activity.MainActivity;
import com.vincent.vpedometer.ui.view.SlideMenu;
import com.vincent.vpedometer.utils.ServerUtils;

/**
 * Created by Administrator on 2018/3/14 14:53
 */
public class InformationFragment extends BaseFrament {
    private TextView name ;
    private TextView totalsteps;
    private TextView email;
    private TextView level;
    private TextView attack;
    private TextView defend;
    private TextView HP;
    private TextView MP;

    public InformationFragment(Context context, SlideMenu slideMenu) {
        this.mContext = context;
        this.mSlideMenu = slideMenu;
        this.mMainActivity = (MainActivity) context;
    }


    @Override
    public void init() {
        initView();
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final User myRple = ServerUtils.getMyRple(mMainActivity);
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myRple != null) {
                            updateText(myRple);
                        }
                    }
                });

            }
        }).start();
    }

    private void initView() {
        rootView = View.inflate(mMainActivity, R.layout.layout_information, null);
        name = (TextView) rootView.findViewById(R.id.name);
        totalsteps = (TextView) rootView.findViewById(R.id.totalsteps);
        email = (TextView) rootView.findViewById(R.id.email);
        level = (TextView) rootView.findViewById(R.id.level);
        attack = (TextView) rootView.findViewById(R.id.attack);
        defend = (TextView) rootView.findViewById(R.id.defend);
        HP = (TextView) rootView.findViewById(R.id.HP);
        MP = (TextView) rootView.findViewById(R.id.MP);
    }

    private void updateText(User user) {
        name.setText(user.getName());
        totalsteps.setText(user.getTotalstep());
        email.setText(user.getEmail());
        level.setText(user.getGameCharecter().getLevel() + "");
        attack.setText(user.getGameCharecter().getAttack() + "");
        defend.setText(user.getGameCharecter().getDefend() + "");
        HP.setText(user.getGameCharecter().getHealthPoint() + "");
        MP.setText(user.getGameCharecter().getMagicPoint() + "");

    }


    @Override
    public BaseFrament getMe() {
        return this;
    }
}

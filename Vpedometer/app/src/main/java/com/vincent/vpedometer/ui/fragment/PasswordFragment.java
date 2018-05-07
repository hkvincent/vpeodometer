package com.vincent.vpedometer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.config.MyConstants;
import com.vincent.vpedometer.pojo.User;
import com.vincent.vpedometer.ui.activity.MainActivity;
import com.vincent.vpedometer.ui.view.SlideMenu;
import com.vincent.vpedometer.utils.ServerUtils;

/**
 * Created by Administrator on 2018/3/13 22:04
 */
public class PasswordFragment extends BaseFrament {
    @Override
    public void init() {
        initView();
        initData();

    }

    public PasswordFragment(Context context, SlideMenu slideMenu) {
        this.mContext = context;
        this.mSlideMenu = slideMenu;
        this.mMainActivity = (MainActivity) context;

    }

    private void initData() {


    }

    private void initView() {
        rootView = View.inflate(mMainActivity, R.layout.layout_password, null);
        final EditText oldPassword = (EditText) rootView.findViewById(R.id.et_oldpassword);
        final EditText newPassword = (EditText) rootView.findViewById(R.id.et_newpassword);
        final EditText comfirmPassword = (EditText) rootView.findViewById(R.id.et_comfirmpassword);
        final EditText userName = (EditText) rootView.findViewById(R.id.et_changeUsername);
        Button comfirmButton = (Button) rootView.findViewById(R.id.btn_comfirm);

        comfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(oldPassword.getText()) || TextUtils.isEmpty(comfirmPassword.getText()) || TextUtils.isEmpty(newPassword.getText())) {
                    Toast.makeText(mMainActivity, "can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!comfirmPassword.getText().toString().equals(newPassword.getText().toString())) {
                    Toast.makeText(mMainActivity, "two password need be same", Toast.LENGTH_SHORT).show();
                    return;
                }
                final User user = new User();
                user.setName(userName.getText().toString().trim());
                user.setPassword(oldPassword.getText().toString().trim());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String password = ServerUtils.ChangePassword(mMainActivity, user, newPassword.getText().toString().trim());
                        Message msg = Message.obtain(null, MyConstants.CHANGE_PASSWORD);
                        Bundle bundle = new Bundle();
                        bundle.putString("password", password);
                        msg.setData(bundle);
                        mMainActivity.delayHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }


    @Override
    public BaseFrament getMe() {
        return this;
    }


}

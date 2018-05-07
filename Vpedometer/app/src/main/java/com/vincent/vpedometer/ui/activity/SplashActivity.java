package com.vincent.vpedometer.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.base.BaseActivity;
import com.vincent.vpedometer.pojo.User;
import com.vincent.vpedometer.utils.ServerUtils;
import com.vincent.vpedometer.utils.SpTools;


/**
 * Created by Administrator on 2018/1/23.
 * pre login page
 */

public class SplashActivity extends BaseActivity {
    private ImageView iv_mainview;
    private AnimationSet as;
    private SharedPreferences sp;
    private static final int GET_SERVER_RESOPENSE = 1;
    private static final int GET_ERROR = 2;
    private String sessionId;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SERVER_RESOPENSE:
                    if (((String) msg.obj).endsWith("success")) {
                        Intent intent = new Intent();
                        intent.setClass(SplashActivity.this, MainActivity.class);
                        intent.putExtra("session", sessionId);
                        startActivity(intent);
                        Toast.makeText(SplashActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        SplashActivity.this.finish();
                        break;
                    }

                case GET_ERROR:
                    Toast.makeText(SplashActivity.this, "login problem", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sp = this.getSharedPreferences("config", this.MODE_PRIVATE);
        performCodeWithPermission("access internet", new PermissionCallback() {
            @Override
            public void hasPermission() {
                initView();
                startAnimation();
                initEvent();
            }

            @Override
            public void noPermission() {

            }
        }, Manifest.permission.INTERNET);

    }

    /**
     * auto login logic
     */
    private void initEvent() {
        //when animation finish we login
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (SpTools.getBoolean(SplashActivity.this, "remember", false, "config")) {
                    if (SpTools.getBoolean(SplashActivity.this, "auto", false, "config")) {
                        login();
                        return;
                    }
                }
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * login logic just like login page
     */
    private void login() {
        final String username = sp.getString("username", "");
        final String password = sp.getString("password", "");
        final User user = new User(username, password);
        new Thread() {
            @Override
            public void run() {
                try {
                    String result = ServerUtils.login(user);
                    if (result.equals("error")) {
                        Message msg = Message.obtain();
                        msg.what = GET_ERROR;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = GET_SERVER_RESOPENSE;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }


                    /*
                    String path = MyConstants.IP + "/pedometerserver/UserServlet?method=login&name=" + username + "&password=" + password;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if (sessionId != null) {
                        conn.setRequestProperty("Cookie", sessionId);
                    }
                    conn.setConnectTimeout(100);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        String cookieval = conn.getHeaderField("Set-Cookie");
                        if (cookieval != null) {
                            sessionId = cookieval.substring(0, cookieval.indexOf(";"));
                            Log.w("SESSION", "session_id=" + sessionId);
                        }
                        InputStream is = conn.getInputStream();
                        String result = StreamUtils.readStream(is);
                        Message msg = Message.obtain();
                        msg.what = GET_SERVER_RESOPENSE;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = GET_ERROR;
                        mHandler.sendMessage(msg);
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = GET_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();


    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        iv_mainview = (ImageView) findViewById(R.id.iv_splash_mainview);

    }

    /**
     * start the splash animation
     */
    private void startAnimation() {
        as = new AnimationSet(false);

        //set 360 degree to rotate
        RotateAnimation ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(1500);
        ra.setFillAfter(true);
        //add to animation set in order to display at the same time with other animation
        as.addAnimation(ra);

        //set zoom in from 0 to 1
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(1500);
        aa.setFillAfter(true);

        as.addAnimation(aa);
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        //all animation in 1.5 seconds to perform
        sa.setDuration(1500);
        sa.setFillAfter(true);
        as.addAnimation(sa);
        iv_mainview.startAnimation(as);
    }
}

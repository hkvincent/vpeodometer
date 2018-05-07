package com.vincent.vpedometer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.pojo.User;
import com.vincent.vpedometer.utils.ServerUtils;

public class LoginActivity extends AppCompatActivity {

    public static final int GET_SERVER_RESOPENSE = 1;
    public static final int GET_ERROR = 2;
    private EditText mEt_username;
    private EditText mEt_passwd;
    private CheckBox mCb_remember;
    private SharedPreferences mSp;
    private Button mBt_login;
    private CheckBox mCb_autologin;
    private String mSessionId;

    //asnyc object to handle login logic
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SERVER_RESOPENSE:
                    if (((String) msg.obj).endsWith("success")) {
                        Intent intent = new Intent();
                        intent.putExtra("auto", mCb_autologin.isChecked() ? true : false);
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    Toast.makeText(LoginActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mBt_login.setEnabled(true);
                    break;
                case GET_ERROR:
                    Toast.makeText(LoginActivity.this, "login problem", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mBt_login.setEnabled(true);
                    break;

                default:
                    break;
            }
        }
    };
    private Button mBt_register;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSp = this.getSharedPreferences("config", this.MODE_PRIVATE);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEt_username = (EditText) findViewById(R.id.et_username);
        mEt_passwd = (EditText) findViewById(R.id.et_passwd);
        mCb_remember = (CheckBox) findViewById(R.id.cb_remember);
        mCb_autologin = (CheckBox) findViewById(R.id.cb_autologin);
        mBt_login = (Button) findViewById(R.id.bt_login);
        mBt_register = (Button) findViewById(R.id.bt_register);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        restoreInfo();

    }

    /**
     * when user in login page, we redisplay information user has saved
     */
    private void restoreInfo() {
        String username = mSp.getString("username", "");
        String password = mSp.getString("password", "");
        boolean remember = mSp.getBoolean("remember", false);
        if (remember) {
            mCb_remember.setChecked(true);
            mEt_username.setText(username);
            mEt_passwd.setText(password);
        }
    }

    public void login(View view) {
        logincheck();
    }

    /**
     * user go to register page
     *
     * @param view
     */
    public void register(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterAcitivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * login logic need to connect to internet
     */
    private void logincheck() {
        final String userName = mEt_username.getText().toString().trim();
        final String password = mEt_passwd.getText().toString().trim();
        mProgressBar.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "username or password must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (mCb_remember.isChecked()) {
                SharedPreferences.Editor editor = mSp.edit();
                editor.putString("username", userName);
                editor.putString("password", password);
                editor.putBoolean("remember", true);
                editor.commit();
            }

            //get user from password and username
            final User user = new User(userName, password);
            mBt_login.setEnabled(false);
            //open working thread to connedt to server and send the login request
            new Thread() {
                @Override
                public void run() {
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
                }
            }.start();


            /*
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        String path = MyConstants.IP + "/pedometerserver/UserServlet?method=login&name=" + userName + "&password=" + password;
                        System.out.println(path);
                        URL url = new URL(path);
                        //get connection
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //set get method
                        conn.setRequestMethod("GET");
                        //if we have the session and send it to server
                        if (mSessionId != null) {
                            conn.setRequestProperty("Cookie", mSessionId);
                        }
                        conn.setConnectTimeout(1000);
                        //get responding
                        int code = conn.getResponseCode();
                        if (code == 200) {
                            String cookieval = conn.getHeaderField("Set-Cookie");
                            if (cookieval != null) {
                                //save the session ID
                                mSessionId = cookieval.substring(0, cookieval.indexOf(";"));
                                Log.w("SESSION", "session_id=" + mSessionId);
                            }
                            //create the message and update the UI
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message msg = Message.obtain();
                        msg.what = GET_ERROR;
                        mHandler.sendMessage(msg);
                    }
                }
            }.start();*/
        }
    }

    /**
     * when register return the data
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            if (data != null) {
                String name = data.getStringExtra("name");
                String password = data.getStringExtra("pwd");
                mEt_username.setText(name);
                mEt_passwd.setText(password);
            }
        }

    }

}

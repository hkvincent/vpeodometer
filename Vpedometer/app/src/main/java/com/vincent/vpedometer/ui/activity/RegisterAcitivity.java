package com.vincent.vpedometer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.config.MyConstants;
import com.vincent.vpedometer.utils.StreamUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/1/18.
 * register logic
 */

public class RegisterAcitivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private RadioGroup mGenderRadioGroup;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mEmail = (EditText) findViewById(R.id.et_email);
        mUsername = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_passwd);
        mGenderRadioGroup = (RadioGroup) findViewById(R.id.rg_gender);

    }


    /**
     * register logic
     *
     * @param view
     */
    public void onRegister(View view) {
        sp = this.getSharedPreferences("register", this.MODE_PRIVATE);

        final boolean register = sp.getBoolean("register", false);
        if (register != false) {
            Toast.makeText(getApplicationContext(), "you already register", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread() {
            public void run() {
                String path = MyConstants.IP + "/pedometerserver/UserServlet?method=register";
                URL url = null;
                try {
                    url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");//set post method
                    conn.setConnectTimeout(1000);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String email = mEmail.getText().toString().trim();
                    String username = mUsername.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    String gender = null;
                    //get gender information
                    int checkedRadioButtonId = mGenderRadioGroup.getCheckedRadioButtonId();
                    switch (checkedRadioButtonId) {
                        case R.id.rb_female:
                            gender = "female";
                            break;
                        case R.id.rb_male:
                            gender = "male";
                            break;
                    }
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(gender)) {
                        showToastInAnyThread("full fill the form");
                        return;
                    }
                    String data = "name=" + URLEncoder.encode(username, "utf-8") + "&password=" + URLEncoder.encode(password);
                    data += "&email=" + email;
                    data += "&gender=" + gender;
                    conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
                    conn.setDoOutput(true);//prepare output
                    conn.getOutputStream().write(data.getBytes());//send data to server
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamUtils.readStream(is);
                        if (result.endsWith("success")) {
                            Intent userData = new Intent();
                            userData.putExtra("name", username);
                            userData.putExtra("pwd", password);
                            setResult(0, userData);
                            sp.edit().putBoolean("register", true).commit();
                            finish();
                        } else {
                            showToastInAnyThread("register faild");
                        }
                    } else {
                        showToastInAnyThread("register faild");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent();

                intent.setClass(RegisterAcitivity.this, LoginActivity.class);

                startActivity(intent);

                RegisterAcitivity.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showToastInAnyThread(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.vincent.vpedometer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.vincent.vpedometer.config.MyConstants;
import com.vincent.vpedometer.pojo.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Administrator on 2018/2/28 20:54
 * connect to server
 */
public class ServerUtils {

    private static String sessionId;
    private static boolean flag = false;

    /**
     * save and load the cookies to okhttpclient and reload it when connect to same host
     */
    static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                    cookieStore.put(url.host(), cookies);
                    for (Cookie cookie : cookies) {
                        sessionId = cookies.toString();
                        System.out.println("cookies: " + cookie.toString());
                    }

                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    int i = 5 + 5;
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    static String post = "";

    /**
     * using httpok framework to login.
     *
     * @param user contain password and username
     * @return return the login status
     */
    public static String login(User user) {
        if (TextUtils.isEmpty(user.getPassword()) || TextUtils.isEmpty(user.getName())) {
            return null;
        }
        RequestBody body = new FormBody.Builder().add("name", user.getName()).add("password", user.getPassword()).build();
        try {
            String post = post(MyConstants.IP + "/pedometerserver/UserServlet?method=login", body);
            return post;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return "error";
    }

    /**
     * get all other player information
     *
     * @param c the contect
     * @return all player infromation
     */
    public static List<User> getAllUserFromServer(Context c) {
        sp = c.getSharedPreferences("config", c.MODE_PRIVATE);
        final String username = sp.getString("username", "");
        final String password = sp.getString("password", "");
        List<User> userList = new ArrayList<>();
        RequestBody body = new FormBody.Builder().add("name", username).add("password", password).build();
        try {
            String post = post(MyConstants.IP + "/pedometerserver/RoleServlet?method=getRoles", body);
            Gson gson = new Gson();
            //convert json to list
            if (!isJSONValid(post))
                return null;
            userList = gson.fromJson(post, new TypeToken<List<User>>() {
            }.getType());

            Log.i("roledata", post);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userList;
    }

    private static SharedPreferences sp;

    /**
     * to upload the total step count to server the
     *
     * @param steps the total steps
     */
    public static String uploadSteps(String steps, Context c) {
        sp = c.getSharedPreferences("config", c.MODE_PRIVATE);
        final String username = sp.getString("username", "");
        final String password = sp.getString("password", "");
        RequestBody body = new FormBody.Builder().add("name", username).add("password", password).add("steps", steps).build();
        String level = "0";
        try {
            level = post(MyConstants.IP + "/pedometerserver/RoleServlet?method=levelRole", body);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return level;
    }


    /**
     * change the password
     *
     * @param c           context
     * @param user        user name and old password
     * @param newPassword changed password
     * @return succee or not
     */
    public static String ChangePassword(Context c, User user, String newPassword) {
        sp = c.getSharedPreferences("config", c.MODE_PRIVATE);
        final String username = user.getName();
        final String password = user.getPassword();
        RequestBody body = new FormBody.Builder().add("name", username).add("password", password).add("newPassword", newPassword).build();
        String result = "";
        try {
            result = post(MyConstants.IP + "/pedometerserver/UserServlet?method=updataPassword", body);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return result;
    }

    public static User getMyRple(Context c) {
        sp = c.getSharedPreferences("config", c.MODE_PRIVATE);
        final String username = sp.getString("username", "");
        final String password = sp.getString("password", "");
        RequestBody body = new FormBody.Builder().add("name", username).add("password", password).build();
        User user = null;
        try {
            String post = post(MyConstants.IP + "/pedometerserver/RoleServlet?method=getMyRole", body);
            Gson gson = new Gson();
            //convert json to list
            if (!isJSONValid(post))
                return null;
            user = gson.fromJson(post, User.class);
            Log.i("roledata", post);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * the actully method to post the data to server
     *
     * @param url         the http address
     * @param requestBody the post body
     * @return the information server return
     * @throws IOException
     */
    private static String post(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        return string;
    }

    /**
     * see the network is working or not
     *
     * @param context activity
     * @return connected
     */
    public static boolean isOnNetwork(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connMgr.getAllNetworks();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < networks.length; i++) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
            if (networkInfo.isConnected())
                return true;
        }
        return false;
    }

    public static boolean isJSONValid(String jsonInString) {
        try {
            Gson gson = new Gson();
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    public static boolean isFlag() {
        return flag;
    }


}

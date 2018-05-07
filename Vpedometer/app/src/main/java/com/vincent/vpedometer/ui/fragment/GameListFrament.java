package com.vincent.vpedometer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.vincent.vpedometer.R;
import com.vincent.vpedometer.pojo.User;
import com.vincent.vpedometer.ui.activity.MainActivity;
import com.vincent.vpedometer.ui.view.MyLinearLayout;
import com.vincent.vpedometer.ui.view.SlideMenu;
import com.vincent.vpedometer.utils.ServerUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/2/11 21:26
 */
public class GameListFrament extends BaseFrament {

    private View mMainView;
    private ListView menu_listview, main_listview;
    private ImageView mGameListHead;//mian view changeIconView
    List<User> allUserFromServer = new ArrayList<>();
    private MyAdapter mMyAdapter;


    public GameListFrament(Context context, SlideMenu slideMenu) {
        this.mContext = context;
        this.mSlideMenu = slideMenu;
        this.mMainActivity = (MainActivity) context;
    }

    public void init() {
        initView();
        initData();

    }

    @Override
    public GameListFrament getMe() {
        return this;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allUserFromServer = ServerUtils.getAllUserFromServer(mMainActivity);
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mMyAdapter.notifyDataSetChanged();
                        initMain();
                    }
                });


            }
        }).start();


    }

    private void initView() {
        mMainView = View.inflate(this.mContext, R.layout.layout_main, this.mSlideMenu);
        mMyView = (MyLinearLayout) mMainView.findViewById(R.id.main_layout);
        main_listview = (ListView) mMainView.findViewById(R.id.main_listview);
        mGameListHead = (ImageView) mMainView.findViewById(R.id.iv_head);
        if (mMainView.findViewById(R.id.iv_head) != null) {
            mGameListHead = (ImageView) mMainView.findViewById(R.id.iv_head);
            mGameListHead.setImageBitmap(((BitmapDrawable) MainActivity.mMenuHead.getDrawable()).getBitmap());
        }
        this.mSlideMenu.setMainView(mMyView);

        initIcon();
    }

    private void initIcon() {
        if (this.listener != null) {
            listener.changeIcon(mGameListHead);
        }
    }

    public static User selectedUser = null;
    public static User myUser = null;

    /**
     * the main layout initiation method.
     */
    private void initMain() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myUser = ServerUtils.getMyRple(mMainActivity);

            }
        }).start();

        //the changeGameListView view of main layout display the contact
        mMyAdapter = new MyAdapter();
        main_listview.setAdapter(mMyAdapter);
        main_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedUser = allUserFromServer.get(position);
                mMainActivity.playGame();
            }
        });



 /*       main_listview.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, MyConstants.NAMES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                //zoom in
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                //zoom out
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });*/
    }

    private IconListener listener;

    public interface IconListener {
        public void changeIcon(ImageView gameListHead);

    }

    public void setIconListener(IconListener listener) {
        this.listener = listener;
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allUserFromServer.size();
        }

        @Override
        public Object getItem(int position) {
            return allUserFromServer.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;

            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(mMainActivity, R.layout.layout_gamelist, null);
                holder = new ViewHolder();
                holder.username = (TextView) view.findViewById(R.id.gamelist);
                view.setTag(holder);
            }

            holder.username.setText(allUserFromServer.get(position).getName());
            //zoom in
            ViewHelper.setScaleX(view, 0.5f);
            ViewHelper.setScaleY(view, 0.5f);
            //zoom out
            ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
            ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
            return view;
        }
    }

    class ViewHolder {
        TextView username;

    }

}


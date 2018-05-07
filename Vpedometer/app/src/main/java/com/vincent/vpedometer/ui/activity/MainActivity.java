package com.vincent.vpedometer.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.orm.LiteOrm;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.vincent.vpedometer.R;
import com.vincent.vpedometer.base.BaseActivity;
import com.vincent.vpedometer.config.MyConstants;
import com.vincent.vpedometer.dao.ShutDownDao;
import com.vincent.vpedometer.pojo.ChartData;
import com.vincent.vpedometer.pojo.ShutDown;
import com.vincent.vpedometer.pojo.StepData;
import com.vincent.vpedometer.pojo.User;
import com.vincent.vpedometer.services.StepService;
import com.vincent.vpedometer.ui.fragment.BaseFrament;
import com.vincent.vpedometer.ui.fragment.GameListFrament;
import com.vincent.vpedometer.ui.fragment.HomeFragment;
import com.vincent.vpedometer.ui.fragment.IconFragment;
import com.vincent.vpedometer.ui.fragment.InformationFragment;
import com.vincent.vpedometer.ui.fragment.PasswordFragment;
import com.vincent.vpedometer.ui.view.MyLinearLayout;
import com.vincent.vpedometer.ui.view.MyListView;
import com.vincent.vpedometer.ui.view.SlideMenu;
import com.vincent.vpedometer.ui.view.SlideMenu.OnDragStateChangeListener;
import com.vincent.vpedometer.utils.DbUtils;
import com.vincent.vpedometer.utils.ServerUtils;
import com.vincent.vpedometer.utils.SpTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.vincent.vpedometer.utils.DbUtils.DB_NAME;
import static com.vincent.vpedometer.utils.DbUtils.liteOrm;

/**
 * Created by Administrator on 2018/1/23.
 */

public class MainActivity extends BaseActivity implements Handler.Callback {
    private SharedPreferences sp; // dao
    private ListView menu_listview, main_listview;//menu and main changeGameListView view
    private SlideMenu slideMenu;
    private ImageView iv_head;//mian view changeIconView
    public static ImageView mMenuHead;//sile menu changeIconView
    private MyLinearLayout my_layout;//the view group
    private final String TAG = MainActivity.class.getSimpleName();//the debug log name
    private String currentPage = MyConstants.sCheeseStrings[1];//set current page in default
    public Handler delayHandler;//the main activity handler to handle message
    private TextView lastSelect = null;//record last item of slidemenu when user have selected
    private BaseFrament mFragment;//the father class of other page
    private long TIME_INTERVAL = 500;//get current step counting every 500 milliseconds
    private Messenger messenger;//the sevices will return the messenger object to let me to contact it
    private ImageView mImageView;
    private TextView mTv;
    public static String currentStep = "0";//the display step
    public static String level = "1";//the role level to display

    private ArrayList<BaseFrament> mBaseFramentsList = new ArrayList<>();

    /**
     * the messenger object is to get the data from step count service when code is MSG_FROM_SERVER
     * if the code is REQUEST_SERVER, the messenger object will send the message to service.
     */
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));

    //when service start, onServiceConnected of this object will be invoked
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //get the messenger which can contact to server
            messenger = new Messenger(service);
            //create a letter
            Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            //give my messenger phone number to server when it have message to send me can use this messenger
            msg.replyTo = mGetReplyMessenger;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * the messenger object is to get the data from step count service when code is MSG_FROM_SERVER
     * and send the empty message to this handle again for looping
     * if the code is REQUEST_SERVER, the messenger object will send the message to service.
     *
     * @param msg
     * @return
     */
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MyConstants.MSG_FROM_SERVER:
                /*if (mHomeFragment != null && mHomeFragment.textStep != null)
                    mHomeFragment.textStep.setText(msg.getData().getInt("step") + "");*/
                currentStep = msg.getData().getInt("step") + "";
                level = msg.getData().getString("level");
                delayHandler.sendEmptyMessageDelayed(MyConstants.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case MyConstants.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case MyConstants.CHANGE_PASSWORD:
                String password = msg.getData().getString("password");
                Toast.makeText(this, password, Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    /**
     * this oncreate method will be invoked when this activity being create.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        //get the permission for reading photo from media library
        performCodeWithPermission("photo permission", new PermissionCallback() {
            @Override
            public void hasPermission() {

            }

            @Override
            public void noPermission() {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        init();
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    /**
     * when back to this activity
     */
    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * when resume to this activity
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * when user clicks the back button this method will be invoked
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    /**
     * the activity life finish
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }


    /**
     * when the pedometer service has been started, we do not need to start it again
     */
    private void startServiceForStrategy() {
        if (!isServiceWork(this, StepService.class.getName())) {
            setupService(true);
        } else {
            setupService(false);
        }
    }

    /**
     * @param flag determine service need to start or not
     */
    private void setupService(boolean flag) {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        if (flag) {
            startService(intent);
        }
    }

    /**
     * initiation method to initiate view, data and event.
     */
    private void init() {
        sp = this.getSharedPreferences("config", this.MODE_PRIVATE);
        boolean auto = getIntent().getBooleanExtra("auto", false);
        if (!SpTools.getBoolean(MainActivity.this, "auto", false, "config")) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("auto", auto);
            editor.commit();
        }
        //create a handler, when data come back the handleMessage method will be called
        delayHandler = new Handler(this);

        startServiceForStrategy();//strategy to start service
        initView();

        initData();

        initEvent();

    }

    /**
     * initiate view
     */
    private void initView() {
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        mMenuHead = (ImageView) findViewById(R.id.iv_head2);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        main_listview = (ListView) findViewById(R.id.main_listview);
        //need to load customer image,not default image
        String myUri = null;
        if ((myUri = SpTools.getString(this, "icon", null, MyConstants.CONFIGFILE)) != null) {
            Uri imageUri = Uri.parse(myUri);
            ContentResolver resolver = getContentResolver();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, imageUri);
                mMenuHead.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 100, 100));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mBaseFramentsList.add(new HomeFragment(this, slideMenu, this));
        mBaseFramentsList.add(new GameListFrament(this, slideMenu));
        mBaseFramentsList.add(new IconFragment(this, slideMenu, this));


        //changeGameListView();
        //iv_head.setImageBitmap(((BitmapDrawable) mMenuHead.getDrawable()).getBitmap());
        //mBaseFramentsList.get(1).init();
        my_layout = (MyLinearLayout) findViewById(R.id.main_layout);

        changeHomeView();

    }


    /**
     * initiate event
     */
    private void initEvent() {
        mMenuHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage.equals("changeIconView"))
                    return;
                currentPage = "changeIconView";
                if (lastSelect != null)
                    lastSelect.setTextColor(Color.WHITE);
                changeIconView();
            }
        });

        slideMenu.setOnDragStateChangeListener(new OnDragStateChangeListener() {
            @Override
            public void onOpen() {
//				Log.e("tag", "onOpen");
                //when open the menu the item of this menu will be display on radom posistion
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }


            @Override
            public void onDraging(float fraction) {
//				Log.e("tag", "onDraging fraction:"+fraction);
                //when sildemenu is being dragged then the icon will zoom in or zoom out
                if (iv_head != null)
                    ViewHelper.setAlpha(iv_head, 1 - fraction);
            }

            @Override
            public void onClose() {
//				Log.e("tag", "onClose");
                //when the silde menu is close, the icon will be sharking
                ViewPropertyAnimator.animate(iv_head).translationXBy(15)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(500)
                        .start();
            }
        });

    }

    /**
     * intiate data
     */
    private void initData() {
        if (menu_listview.getAdapter() == null)
            initMenu();
        my_layout.setSlideMenu(slideMenu);
    }


    /**
     * the menu layout initiation method.
     */
    private void initMenu() {
        //the changeGameListView view of menu layout display the data
        menu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MyConstants.sCheeseStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //get text view from android system
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                if (textView.getText().equals(currentPage)) {
                    textView.setTextColor(Color.BLUE);
                    lastSelect = textView;
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView eventView = (TextView) v;
                        CharSequence text = eventView.getText();
                        System.out.println(text);
                        if (MyConstants.sCheeseStrings[3].equals(text)) {
                            logout();
                            changeSelectItem(eventView);
                            lastSelect = eventView;
                        } else if (MyConstants.sCheeseStrings[0].equals(text)) {
                            if (currentPage.equals(text))
                                return;
                            currentPage = MyConstants.sCheeseStrings[0];
                            changeSelectItem(eventView);
                            lastSelect = eventView;
                            changeGameListView();
                        } else if (MyConstants.sCheeseStrings[1].equals(text)) {
                            if (currentPage.equals(text))
                                return;
                            currentPage = MyConstants.sCheeseStrings[1];
                            changeSelectItem(eventView);
                            lastSelect = eventView;
                            changeHomeView();
                        } else if (MyConstants.sCheeseStrings[2].equals(text)) {
                            if (currentPage.equals(text))
                                return;
                            currentPage = MyConstants.sCheeseStrings[2];
                            changeSelectItem(eventView);
                            lastSelect = eventView;
                            changeSettingView();
                        }

                    }
                });
                return textView;
            }
        });
    }

    private int[] ids = {R.layout.layout_information, R.layout.layout_password};


    /**
     * change the color select item on slien menu
     *
     * @param textView
     */
    private void changeSelectItem(TextView textView) {
        if (lastSelect != null)
            lastSelect.setTextColor(Color.WHITE);
        textView.setTextColor(Color.BLUE);
    }


    /**
     * change to setting view
     */
    private void changeSettingView() {
        View inflate = View.inflate(this, R.layout.layout_more, slideMenu);
        MyLinearLayout myLinearLayout = (MyLinearLayout) inflate.findViewById(R.id.home_layout);
        MyListView myListView = (MyListView) inflate.findViewById(R.id.mylistview);

        PasswordFragment passwordFragment = new PasswordFragment(this, slideMenu);
        passwordFragment.init();
        InformationFragment informationFragment = new InformationFragment(this, slideMenu);
        informationFragment.init();


        myListView.addView(passwordFragment.rootView);
        myListView.addView(informationFragment.rootView);


        my_layout = myLinearLayout;
        slideMenu.setMainView(myLinearLayout);
        initData();
    }


    /**
     * change view to home page
     */
    private void changeHomeView() {
        mFragment = mBaseFramentsList.get(0);
        mFragment.init();
        my_layout = mFragment.mMyView;
        initData();
    }

    /**
     * change view to game list page
     */
    private void changeGameListView() {
        //changeView(R.layout.layout_main, slideMenu, R.id.main_layout);
        mFragment = mBaseFramentsList.get(1);
        ((GameListFrament) mFragment).setIconListener(new GameListFrament.IconListener() {
            @Override
            public void changeIcon(ImageView gameListHead) {
                iv_head = gameListHead;
            }
        });
        mFragment.init();
        my_layout = mFragment.mMyView;
        initData();
    }

    /**
     * change view to icon page
     */
    private void changeIconView() {
        mFragment = mBaseFramentsList.get(2);
        mFragment.init();
        my_layout = mFragment.mMyView;

        initData();
    }


    /**
     * get the data chart to display to user
     *
     * @param chartData
     */
    public void ChangeChartView(@NonNull ChartData chartData) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("chartData", chartData);
        startActivity(intent);

    }

    /**
     * the game mode
     */
    public void playGame() {
        Intent intent = new Intent();
        //explicit to open activity
        intent.setClass(this, GameActivity.class);
        startActivityForResult(intent, 1);
    }


    private final int IMAGE_CODE = 2;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    /**
     * choose image from media library,the IconFragment will call it when be clicked
     */
    public void setImage() {
        //create a intent to open the media
        Intent getAlbum = new Intent(Intent.ACTION_PICK);
        //set the we want to get the type
        getAlbum.setType(IMAGE_UNSPECIFIED);
        //open it and get the result
        startActivityForResult(getAlbum, IMAGE_CODE);
    }


    /**
     * logout method, we need to empty the record in xml file
     */
    private void logout() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", null);
        editor.putString("password", null);
        editor.putBoolean("remember", false);
        editor.putBoolean("auto", false);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * determine service we specify is running or not
     *
     * @param mContext
     * @param serviceName package+class name of service（e.g：net.loonggg.testbackstage.TestService）
     * @return true is running ，false is not running of my service
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            Log.i("service", myList.get(i).service.getClassName().toString());
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    /**
     * if other activity  return result, this method will be called
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult");
        if (resultCode == 1) {

            if (data != null) {
                boolean win = data.getBooleanExtra("win", false);
                if (win) {
                    Toast.makeText(this, "you win", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "you lose", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == IMAGE_CODE) {
            //the bitmap object to store photo
            Bitmap bm = null;
            ContentResolver resolver = getContentResolver();
            try {
                //the photo uri
                Uri originalUri = data.getData();
                //get the bitmap using photo uri and resolver
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //set the photo to icon the size is 300*300
                ((IconFragment) mFragment.getMe()).mImageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 300, 300));

                //get all media type
                String[] proj = {MediaStore.Images.Media.DATA};

                //convert to photo type and get the cursor pointing to the photo user select
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);

                //get the photo index of the media store position
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //reset the cursor
                cursor.moveToFirst();
                //we can get the actully path of photo
                String path = cursor.getString(column_index);
                ((IconFragment) mFragment.getMe()).mTextView.setText(path);
                //set small icon
                iv_head.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 100, 100));
                //set gamelist icon
                mMenuHead.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 100, 100));
                //and save the photo path to xml next time we start app the icon can be reuse.
                SpTools.setString(this, "icon", originalUri.toString());


            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());

            } finally {
                return;
            }
        }

    }






    /*--------------------------unuse------------------------------------------*/

    public void data(View view) {
        DB_NAME = "basepedo.db";
        if (liteOrm == null) {
            liteOrm = LiteOrm.newCascadeInstance(this, DB_NAME);
            liteOrm.setDebugged(true);
        }
        DbUtils.getQueryAll(StepData.class);
        List<StepData> queryAll = DbUtils.getQueryAll(StepData.class);
        int totalstep = 0;
        for (StepData steps : queryAll) {
            totalstep += steps.getStep();
            Log.i("step", steps.toString());
        }
        Log.i("totalsteps", totalstep + "");
        ShutDownDao shutDownDao = new ShutDownDao(this);
        List<ShutDown> query = shutDownDao.query();
        for (ShutDown shutDown : query) {
            Log.i("step", shutDown.toString());
        }

    }


    public void session(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> allUserFromServer = ServerUtils.getAllUserFromServer(MainActivity.this);

            }
        }).start();

    }


    public void map(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);

    }


    /**
     * action bar, but obselte
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.activity_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * action bar call back method
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
        } else if (item.getItemId() == R.id.setting) {

        } else if (item.getItemId() == R.id.test) {
            playGame();

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * the change view core method, when one of the sile menu item be clicked,this method will load the xml file
     * and render it to display
     *
     * @param layout_main
     * @param slideMenu
     * @param main_layout
     * @return
     */
    private View changeView(int layout_main, SlideMenu slideMenu, int main_layout) {
        //main_listview.setAdapter(null);
        View mainView = View.inflate(MainActivity.this, layout_main, slideMenu);
        MyLinearLayout newMainView = (MyLinearLayout) mainView.findViewById(main_layout);

        main_listview = (ListView) mainView.findViewById(R.id.main_listview);
        if (mainView.findViewById(R.id.iv_head) != null) {
            iv_head = (ImageView) mainView.findViewById(R.id.iv_head);
            iv_head.setImageBitmap(((BitmapDrawable) mMenuHead.getDrawable()).getBitmap());
        }

        System.out.println("new view:" + newMainView);
        //we need to set the main view to sile menu
        slideMenu.setMainView(newMainView);
        //slideMenu.setCurrentState(SlideMenu.DragState.Open);
        my_layout = (MyLinearLayout) newMainView;

       /* View menuView = View.inflate(MainActivity.this, layout_main, slideMenu);
        MyLinearLayout newMenuView = (MyLinearLayout) menuView.findViewById(main_layout);
        slideMenu.setMenuView(newMenuView);*/
        initData();
        return mainView;
    }


}

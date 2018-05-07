package com.vincent.vpedometer.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.litesuits.orm.LiteOrm;
import com.vincent.vpedometer.R;
import com.vincent.vpedometer.pojo.ChartData;
import com.vincent.vpedometer.pojo.StepData;
import com.vincent.vpedometer.ui.activity.MainActivity;
import com.vincent.vpedometer.ui.view.BeforeOrAfterCalendarView;
import com.vincent.vpedometer.ui.view.MyLinearLayout;
import com.vincent.vpedometer.ui.view.SlideMenu;
import com.vincent.vpedometer.utils.DbUtils;
import com.vincent.vpedometer.utils.SensorCheckUtils;
import com.vincent.vpedometer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.vincent.vpedometer.R.id.main_layout;
import static com.vincent.vpedometer.utils.DbUtils.DB_NAME;
import static com.vincent.vpedometer.utils.DbUtils.liteOrm;

/**
 * Created by Administrator on 2018/2/11 20:49
 */
public class HomeFragment extends BaseFrament {


    public LinearLayout movementCalenderLl;
    public TextView kmTimeTv;
    public TextView totalKmTv;
    public TextView stepsTimeTv;
    public TextView totalStepsTv;
    public TextView supportTv;
    public TextView textStep;
    public static int screenWidth, screenHeight;
    private View mMainView;
    private BeforeOrAfterCalendarView calenderView;
    private List<StepData> stepEntityList = new ArrayList<StepData>();
    private String curSelDate;
    private Button mChartButton;
    private List<Integer> dayList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private List<String> hyphenDateList = new ArrayList<>();
    private Button mHistoryButton;

    public HomeFragment(Context context, SlideMenu slideMenu, MainActivity main) {
        this.mContext = context;
        this.mSlideMenu = slideMenu;
        this.mMainActivity = main;
    }

    public void init() {
        initView();
        initData();


    }

    @Override
    public HomeFragment getMe() {
        return this;
    }

    /**
     * get the data
     */
    private void initData() {
        WindowManager windowManager = mMainActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        curSelDate = TimeUtils.changeFormatDate(TimeUtils.getCurrentDate());
        //create calender view
        calenderView = new BeforeOrAfterCalendarView(mMainActivity);
        movementCalenderLl.addView(calenderView);
        /**
         * determine the phone support google sensor or not
         */
        if (SensorCheckUtils.isSupportStepCountSensor(mMainActivity)) {
            getRecordList();
            supportTv.setVisibility(View.GONE);
            setDatas();

        } else {
            totalStepsTv.setText("0");
            supportTv.setVisibility(View.VISIBLE);
        }

        /**
         * get step data by user selecting date
         */
        calenderView.setOnBoaCalenderClickListener(new BeforeOrAfterCalendarView.BoaCalenderClickListener() {
            @Override
            public void onClickToRefresh(int position, String curDate) {
                String date = TimeUtils.changeFormatDate(curDate);
                setDatas(date);
            }
        });
    }

    private void initView() {

        mMainView = View.inflate(this.mContext, R.layout.layout_home, this.mSlideMenu);
        mMyView = (MyLinearLayout) mMainView.findViewById(R.id.home_item_layout);
        MyLinearLayout newMainView = (MyLinearLayout) mMainView.findViewById(main_layout);
        movementCalenderLl = (LinearLayout) mMainView.findViewById(R.id.movement_records_calender_ll);
        kmTimeTv = (TextView) mMainView.findViewById(R.id.movement_total_km_time_tv);
        totalKmTv = (TextView) mMainView.findViewById(R.id.movement_total_km_tv);
        stepsTimeTv = (TextView) mMainView.findViewById(R.id.movement_total_steps_time_tv);
        totalStepsTv = (TextView) mMainView.findViewById(R.id.movement_total_steps_tv);
        supportTv = (TextView) mMainView.findViewById(R.id.is_support_tv);
        mChartButton = (Button) mMainView.findViewById(R.id.chart);
        mHistoryButton = (Button) mMainView.findViewById(R.id.history);
        Button session = (Button) mMyView.findViewById(R.id.session);


        //textStep = (TextView) mMainView.findViewById(R.id.text_step);
        this.mSlideMenu.setMainView(mMyView);
        //textStep.setText(MainActivity.currentStep);

        mChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stepDates = new ArrayList<String>();
                dateList.clear();
                dayList.clear();
                hyphenDateList.clear();
                ChartData chartData = new ChartData();
                //get full date e.g :2017年02月21日
                dateList.addAll(TimeUtils.getBeforeDateListByNow(30));
                //change date format
                for (String date : dateList) {
                    hyphenDateList.add(TimeUtils.changeFormatDate(date));
                }
                //format today
                for (StepData s : stepEntityList) {
                    stepDates.add(s.getToday());
                }

                //get partial date e.g : 21 , 22 ,23
                dayList.addAll(TimeUtils.dateListToDayList(dateList));


                for (int i = 0; i < hyphenDateList.size(); i++) {
                    int day = dayList.get(i);
                    List<StepData> today = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{hyphenDateList.get(i)});
                    if (today != null && today.size() > 0)
                        chartData.getStepData().add(today.get(0).getStep() + "");
                    else
                        chartData.getStepData().add(0 + "");

                    //String curItemDate = dateList.get(i);

                    if (day == TimeUtils.getCurrentDay()) {
                        chartData.getTimeData().add("today");
                    } else {
                        chartData.getTimeData().add(day + "");
                    }

                }

                mMainActivity.ChangeChartView(chartData);
            }
        });


        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    /**
     * history data for user select
     */
    private void showDatePickerDialog() {
        // get the Calendar instance
        Calendar calendar = Calendar.getInstance();
        // create a DatePickerDialog dialog and show it out
        new DatePickerDialog(mMainActivity,
                // the listener to listen the data return(How the parent is notified that the date is set.)
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // get the user selected date
                        System.out.println("your choose：" + year + "year" + monthOfYear + 1
                                + "month" + dayOfMonth + "day");

                        String condition = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + dayOfMonth;
                        Log.i("step", condition);
                        List<StepData> today = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{condition});
                        String message = "no record in that day";
                        if (today.size() > 0) {
                            message = today.get(0).getStep() + "step(s) &" + TimeUtils.countTotalKM(today.get(0).getStep()) + "KM";
                        }
                        final AlertDialog dialog = new AlertDialog.Builder(mMainActivity).create();
                        dialog.setCancelable(true);
                        dialog.setTitle(condition + "'s steps");
                        dialog.setMessage(message);

                        dialog.show();
                    }
                },
                // the default date
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    /**
     * access the database to get the step data
     */
    private void getRecordList() {
        DB_NAME = "basepedo.db";
        if (liteOrm == null) {
            liteOrm = LiteOrm.newCascadeInstance(mMainActivity, DB_NAME);
            liteOrm.setDebugged(true);
        }
        stepEntityList.addAll(DbUtils.getQueryAll(StepData.class));
        if (stepEntityList.size() >= 7) {
            // TODO: 2017/3/27 在这里获取历史记录条数，当条数达到7条或以上时，就开始删除第七天之前的数据,暂未实现

        }

    }


    /**
     * @param date
     */
    private void setDatas(String date) {
        DB_NAME = "basepedo.db";
        if (liteOrm == null) {
            liteOrm = LiteOrm.newCascadeInstance(mMainActivity, DB_NAME);
            liteOrm.setDebugged(true);
        }
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{date});
        if (list != null && list.size() > 0) {
            int steps = list.get(0).getStep();
            //get the steps count
            totalStepsTv.setText(String.valueOf(steps));
            //the steps will convent to  KM
            totalKmTv.setText(TimeUtils.countTotalKM(steps));
        } else {
            //get the steps count
            totalStepsTv.setText("0");
            //the steps will be walk how many KM
            totalKmTv.setText("0");
        }
        //setting time
        String time = TimeUtils.getWeekStr(TimeUtils.changeFormatDate2(date));
        kmTimeTv.setText(time);
        stepsTimeTv.setText(time);
    }


    /**
     * set today data
     */
    private void setDatas() {
        //setDatas(TimeUtils.changeFormatDate(TimeUtils.getCurrentDate()));

        //get the steps count
        totalStepsTv.setText(String.valueOf(mMainActivity.currentStep));
        //the steps will be walk how many KM
        totalKmTv.setText(TimeUtils.countTotalKM(Integer.parseInt(mMainActivity.currentStep)));
        String time = TimeUtils.getWeekStr(TimeUtils.changeFormatDate2(curSelDate));
        kmTimeTv.setText(time);
        stepsTimeTv.setText(time);

    }


}

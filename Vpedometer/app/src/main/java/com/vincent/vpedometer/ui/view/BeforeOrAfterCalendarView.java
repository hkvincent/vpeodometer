package com.vincent.vpedometer.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/11 19:22
 * the past six date of today calender
 */

public class BeforeOrAfterCalendarView extends RelativeLayout {

    private List<Integer> dayList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();

    protected List<RecordsCalenderItemView> itemViewList = new ArrayList<>();
    protected Context mContext;
    protected LinearLayout calenderViewLl;
    protected int curPosition;

    public BeforeOrAfterCalendarView(Context context) {
        super(context);
        this.mContext = context;

        init();
    }

    private void init() {
        //inflate this xml file to a view and this view's parent is this.
        View view = LayoutInflater.from(mContext).inflate(R.layout.before_or_after_calendar_layout, this);

        calenderViewLl = (LinearLayout) view.findViewById(R.id.boa_calender_view_ll);

        setBeforeDateViews();

        initItemViews();
    }

    /**
     * set the before day of today
     */
    private void setBeforeDateViews() {
        //get full date e.g :2017年02月21日
        dateList.addAll(TimeUtils.getBeforeDateListByNow());
        //get partial date e.g : 21
        dayList.addAll(TimeUtils.dateListToDayList(dateList));
    }

    private void initItemViews() {
        for (int i = 0; i < dateList.size(); i++) {
            int day = dayList.get(i);
            String curItemDate = dateList.get(i);
            final RecordsCalenderItemView itemView;
            if (day == TimeUtils.getCurrentDay()) {
                itemView = new RecordsCalenderItemView(mContext, "今天", String.valueOf(day), i, curItemDate);
            } else {
                itemView = new RecordsCalenderItemView(mContext, TimeUtils.getCurWeekDay(curItemDate), String.valueOf(day), i, curItemDate);
            }

            itemViewList.add(itemView);
            calenderViewLl.addView(itemView);

            itemView.setOnCalenderItemClick(new RecordsCalenderItemView.OnCalenderItemClick() {
                @Override
                public void onCalenderItemClick() {
                    curPosition = itemView.getPosition();
                    switchPositionView(curPosition);

                    //callback onclick event to refresh user selecting date
                    if (calenderClickListener != null) {
                        calenderClickListener.onClickToRefresh(curPosition, dateList.get(curPosition));
                    }
                }
            });
        }

        switchPositionView(6);

    }

    private void switchPositionView(int position) {
        for (int i = 0; i < itemViewList.size(); i++) {
            if (position == i) {
                itemViewList.get(i).setChecked(true);
            } else {
                itemViewList.get(i).setChecked(false);
            }
        }
    }

    private BoaCalenderClickListener calenderClickListener;

    public interface BoaCalenderClickListener {
        void onClickToRefresh(int position, String curDate);
    }

    public void setOnBoaCalenderClickListener(BoaCalenderClickListener calenderClickListener) {
        this.calenderClickListener = calenderClickListener;
    }
}

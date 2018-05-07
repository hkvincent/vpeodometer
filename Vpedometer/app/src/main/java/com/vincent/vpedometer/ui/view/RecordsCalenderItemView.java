package com.vincent.vpedometer.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.ui.fragment.HomeFragment;


/**
 * Created by Administrator on 2018/2/11 19:21
 * the calender item layout of each day in a week
 */

public class RecordsCalenderItemView extends RelativeLayout {
    private static final String TAG = "RecordsCalenderItemView";

    private Context mContext;

    private LinearLayout itemLl;
    private View lineView;
    private TextView weekTv;
    private RelativeLayout dateRl;
    private TextView dateTv;
    //the date
    private String weekStr, dateStr;
    private int position;


    //the current selected item date
    protected String curItemDate;


    OnCalenderItemClick itemClick = null;

    public interface OnCalenderItemClick {
        public void onCalenderItemClick();
    }

    /**
     * set calender click listener
     *
     * @param itemClick
     */
    public void setOnCalenderItemClick(OnCalenderItemClick itemClick) {
        this.itemClick = itemClick;
    }


    public RecordsCalenderItemView(Context context, String week, String date, int position, String curItemDate) {
        super(context);
        this.mContext = context;
        this.weekStr = week;
        this.dateStr = date;
        this.position = position;
        this.curItemDate = curItemDate;

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View itemView = inflater.inflate(R.layout.records_calender_item_view, this);
        itemLl = (LinearLayout) itemView.findViewById(R.id.records_calender_item_ll);
        weekTv = (TextView) itemView.findViewById(R.id.records_calender_item_week_tv);
        lineView = itemView.findViewById(R.id.calendar_item_line_view);
        dateRl = (RelativeLayout) itemView.findViewById(R.id.records_calender_item_date_rl);
        dateTv = (TextView) itemView.findViewById(R.id.records_calender_item_date_tv);


//        if(curItemDate.equals(TimeUtil.getCurrentDate())){
//            dateTv.setBackgroundResource(R.drawable.ic_blue_round_border_bg);
//            dateTv.getBackground().setAlpha(255);
//        }else{
//            if(dateTv.getBackground() != null){
//                dateTv.getBackground().setAlpha(0);
//            }
//        }

        dateTv.setTextSize(12);
        weekTv.setTextSize(12);
        lineView.setVisibility(GONE);

        weekTv.setText(weekStr);
        dateTv.setText(dateStr);

        //set the calender item size and postion
        itemView.setLayoutParams(new LayoutParams((HomeFragment.screenWidth) / 7,
                ViewGroup.LayoutParams.MATCH_PARENT));

        //set item onclick listener, when the item being clicked, the calender listener being invoked
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onCalenderItemClick();
            }
        });

    }

    /**
     * the select item's ID to return
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    public void setChecked(boolean checkedFlag) {

        if (checkedFlag) {
            //when item has been selected we change it style
            weekTv.setTextColor(getResources().getColor(R.color.main_text_color));
            dateTv.setTextColor(getResources().getColor(R.color.white));
            dateRl.setBackgroundResource(R.mipmap.ic_blue_round_bg);
        } else {
            //when item has not been selected the style will be difference
            weekTv.setTextColor(getResources().getColor(R.color.gray_default_dark));
            dateTv.setTextColor(getResources().getColor(R.color.gray_default_dark));
            //setting the background is transparent
            dateRl.setBackgroundColor(Color.TRANSPARENT);
        }

    }
}

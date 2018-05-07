package com.vincent.vpedometer.utils;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/2/12 1:09
 */
public class TimeUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private static Calendar mCalendar = Calendar.getInstance();
    private static String[] weekStrings = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private static String[] rWeekStrings = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};


    /**
     * change data format
     *
     * @param date 2017年02月09日
     * @return 2017-02-09
     */
    public static String changeFormatDate(String date) {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = null;
        try {
            Date dt = dateFormat.parse(date);
            curDate = dFormat.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return curDate;
    }

    /**
     * change data format
     *
     * @param date 2017-02-09
     * @return 2017年02月09日
     */
    public static String changeFormatDate2(String date) {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = null;
        try {
            Date dt = dFormat.parse(date);
            curDate = dateFormat.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return curDate;
    }

    /**
     * 返回当前的时间
     *
     * @return 今天 09:48
     */
    public static String getCurTime() {
        SimpleDateFormat dFormat = new SimpleDateFormat("HH:mm");
        String time = "今天 " + dFormat.format(System.currentTimeMillis());
        return time;
    }

    /**
     * 获取运动记录是周几，今天则返回具体时间，其他则返回具体周几
     *
     * @param dateStr
     * @return
     */
    public static String getWeekStr(String dateStr) {

        String todayStr = dateFormat.format(mCalendar.getTime());

        if (todayStr.equals(dateStr)) {
            return getCurTime();
        }

        Calendar preCalendar = Calendar.getInstance();
        preCalendar.add(Calendar.DATE, -1);
        String yesterdayStr = dateFormat.format(preCalendar.getTime());
        if (yesterdayStr.equals(dateStr)) {
            return "昨天";
        }

        int w = 0;
        try {
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) {
                w = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rWeekStrings[w];
    }


    /**
     * 获取是几号
     *
     * @return dd
     */
    public static int getCurrentDay() {
        return mCalendar.get(Calendar.DATE);
    }

    /**
     * 获取当前的日期
     *
     * @return yyyy年MM月dd日
     */
    public static String getCurrentDate() {
        String currentDateStr = dateFormat.format(mCalendar.getTime());
        return currentDateStr;
    }


    /**
     * 根据date列表获取day列表
     *
     * @param dateList
     * @return 1~31號
     */
    public static List<Integer> dateListToDayList(List<String> dateList) {
        Calendar calendar = Calendar.getInstance();
        List<Integer> dayList = new ArrayList<>();
        for (String date : dateList) {
            try {
                calendar.setTime(dateFormat.parse(date));
                int day = calendar.get(Calendar.DATE);
                dayList.add(day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dayList;
    }


    /**
     * 根据当前日期获取以含当天的前一周日期
     *
     * @return [2017年02月21日, 2017年02月22日, 2017年02月23日, 2017年02月24日, 2017年02月25日, 2017年02月26日, 2017年02月27日]
     */
    public static List<String> getBeforeDateListByNow() {
        List<String> weekList = new ArrayList<>();

        for (int i = -6; i <= 0; i++) {
            //以周日为一周的第一天
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            String date = dateFormat.format(calendar.getTime());
            weekList.add(date);
        }
        return weekList;
    }

    public static List<String> getBeforeDateListByNow(int howMuchDay) {
        List<String> weekList = new ArrayList<>();

        for (int i = -howMuchDay; i <= 0; i++) {
            //以周日为一周的第一天
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            String date = dateFormat.format(calendar.getTime());
            weekList.add(date);
        }
        return weekList;
    }


    /**
     * 判断当前日期是周几
     *
     * @param curDate
     * @return
     */
    public static String getCurWeekDay(String curDate) {
        int w = 0;
        try {
            Date date = dateFormat.parse(curDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) {
                w = 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weekStrings[w];
    }

    private static DecimalFormat df = new DecimalFormat("#.##");

    public static String countTotalKM(int steps) {
        if (steps < 1) {
            return "0";
        }
        double totalMeters = steps * 0.7;
        //保留两位有效数字
        return df.format(totalMeters / 1000);
    }


}

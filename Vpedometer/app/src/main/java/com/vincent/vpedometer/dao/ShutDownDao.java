package com.vincent.vpedometer.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vincent.vpedometer.pojo.ShutDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/11 19:21
 * when user shut down their phone ,the shutDOWNDAO tools will be invoked to record the steps
 */
public class ShutDownDao {
    private final MyDBOpenHelper helper;
    private final String DBNAME = "shutdown";

    public ShutDownDao(Context context) {
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(context, DBNAME, 1);
        this.helper = myDBOpenHelper;
    }

    public void add(String date, int totalStep) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into " + DBNAME + " (date, steps) values (?, ?);", new Object[]{date, totalStep});
        db.close();
    }

    public void delete(String date) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + DBNAME + " where date=? ", new Object[]{date});
        db.close();
    }

    public void update(int id, String date, int step) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update " + DBNAME + " set date=? , steps = ? where id = ? ", new Object[]{date, step, id});
        db.close();
    }

    public List<ShutDown> query() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBNAME, null);
        List<ShutDown> Lists = new ArrayList<ShutDown>();
        while (cursor.moveToNext()) {
            ShutDown shutDown = new ShutDown();
            shutDown.setId(cursor.getInt(0));
            shutDown.setShutDownDate(cursor.getString(1));
            shutDown.setTotalSteps(cursor.getInt(2));
            Lists.add(shutDown);
        }
        cursor.close();
        db.close();
        return Lists;

    }

}


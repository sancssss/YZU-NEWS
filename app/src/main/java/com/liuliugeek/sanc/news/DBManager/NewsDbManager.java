package com.liuliugeek.sanc.news.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.liuliugeek.sanc.news.Model.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by 73732 on 2016/8/28.
 */
public class NewsDbManager {
    private NewsDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public NewsDbManager(Context context){
        dbHelper = new NewsDatabaseHelper(context, "news.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    public void add(ArrayList<Data> datas){
        db.beginTransaction();
        try{
            for(Data data : datas){
                db.execSQL("INSERT INTO news VALUES(null, ?, ?, ?, ?, ?, ?, null)", new Object[]{data.getNewTypeid(),data.getNewArcid(),data.getNewUrl(),data.getNewDate(),data.getNewTitle(),data.getNewContent()});
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public ArrayList<Data> query(int typeid){
        ArrayList<Data> datas = new ArrayList<>();
        Cursor cursor = queryCursor(typeid);
        while(cursor.moveToNext()){
            Data data = new Data();
            data.setNewArcid(cursor.getInt(cursor.getColumnIndex("news_arcid")));
            data.setNewTypeid(cursor.getInt(cursor.getColumnIndex("news_typeid")));
            data.setNewUrl(cursor.getString(cursor.getColumnIndex("news_url")));
            data.setNewDate(cursor.getString(cursor.getColumnIndex("news_date")));
            data.setNewTitle(cursor.getString(cursor.getColumnIndex("news_title")));
            data.setNewContent(cursor.getString(cursor.getColumnIndex("news_content")));
            data.setIsFavorite(cursor.getInt(cursor.getColumnIndex("news_isfavorite")));
            datas.add(data);
        }
        cursor.close();
        return datas;
    }


    public HashMap<String, Integer> getSetting(){
        Cursor cursor = db.rawQuery("select * from setting", null);
        cursor.moveToFirst();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("setting_diaplay_pic", cursor.getInt(cursor.getColumnIndex("setting_diaplay_pic")));
        map.put("setting_theme_id", cursor.getInt(cursor.getColumnIndex("setting_theme_id")));
        return map;
    }

    public ArrayList<Data> queryFavorites(){
        ArrayList<Data> datas = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from favorite", null);
        while(cursor.moveToNext()){
            Data data = new Data();
            data.setNewArcid(cursor.getInt(cursor.getColumnIndex("news_arcid")));
            data.setNewTypeid(cursor.getInt(cursor.getColumnIndex("news_typeid")));
            data.setNewUrl(cursor.getString(cursor.getColumnIndex("news_url")));
            data.setNewDate(cursor.getString(cursor.getColumnIndex("news_date")));
            data.setNewTitle(cursor.getString(cursor.getColumnIndex("news_title")));
            data.setNewContent(cursor.getString(cursor.getColumnIndex("news_content")));
            datas.add(data);
        }
        cursor.close();
        return datas;
    }

    /**
     * 加入一条data数据到favorite表,更新news对应arcid相同的数据的isfavorite为1
     * @param data
     */
    public void addToFavorites(Data data){
        db.beginTransaction();
        try{
            db.execSQL("INSERT INTO favorite VALUES(null, ?, ?, ?, ?, ?, ?)", new Object[]{data.getNewTypeid(),data.getNewArcid(),data.getNewUrl(),data.getNewDate(),data.getNewTitle(),data.getNewContent()});
            ContentValues val = new ContentValues();
            //要修改的内容
            val.put("news_isfavorite", 1);
            //数组内是条件
            db.update("news", val, "news_arcid = ?", new String[]{String.valueOf(data.getNewArcid())});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void removeFavorite(int arcid){
        Log.v("remove F arcid", String.valueOf(arcid));
            db.execSQL("delete from favorite where news_arcid = " + arcid);
            ContentValues val = new ContentValues();
        //要修改的内容
            val.put("news_isfavorite", 0);
            //数组内是条件
            db.update("news", val, "news_arcid = ?", new String[]{String.valueOf(arcid)});
    }

    //利用arcid更新
    public void updateContent(int arcid,String text){
        ContentValues val = new ContentValues();
        //要修改的内容
        val.put("news_content", text);
        //数组内是条件
        db.update("news", val, "news_arcid = ?", new String[]{String.valueOf(arcid)});
    }

    public void deleteContent(int typeid){
        Log.v("typeid", String.valueOf(typeid));
        db.execSQL("delete from news where news_typeid = " + typeid);
    }

    public String getContent(int arcid){
        Cursor cursor = db.rawQuery("select news_content from news where news_arcid = ?", new String[]{String.valueOf(arcid)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("news_content"));
    }

    public String getTitle(int arcid){
        Cursor cursor = db.rawQuery("select news_title from news where news_arcid = ?", new String[]{String.valueOf(arcid)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("news_title"));
    }

    public Cursor queryCursor(int typeid){
        Cursor cursor = db.rawQuery("select * from news where news_typeid = ?", new String[]{String.valueOf(typeid)});
        return cursor;
    }

    public boolean isListEmpty(int typeid){
        Cursor cursor = queryCursor(typeid);
        int count = cursor.getCount();
        Log.v("is_list_empty", String.valueOf(count));
        if(count <= 0)
            return true;
        else
            return false;
    }

    public boolean isContentEmpty(int arcid){
        Cursor cursor = db.rawQuery("select * from news where news_arcid = ?", new String[]{String.valueOf(arcid)});
        int count = cursor.getCount();
        //文本长度大于30为非url
        if(count <= 0 || getContent(arcid).substring(0,4).equals("http"))
            return true;
        else
            return false;
    }

    public boolean isRefresh(int typeid){
        Cursor cursor = db.rawQuery("select * from board where board_id = ?", new String[]{String.valueOf(typeid)});
        cursor.moveToFirst();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dateStr = format.format(date);
        Log.v("date_now", dateStr);
        if(cursor.getString(cursor.getColumnIndex("date")) == null || !cursor.getString(cursor.getColumnIndex("date")).equals(dateStr)){
            return false;
        }else{
            return true;
        }
    }

    public String getBoardRefreshDate(int typeid){
        Cursor cursor = db.rawQuery("select * from board where board_id = ?", new String[]{String.valueOf(typeid)});
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("date"));
    }


    public void setBoardRefreshDate(int typeid, String date){
        ContentValues val = new ContentValues();
        //要修改的内容
        val.put("date",date);
        //数组内是条件
        db.update("board", val, "board_id = ?", new String[]{String.valueOf(typeid)});
    }


    public void closeDB(){
        db.close();
    }
}

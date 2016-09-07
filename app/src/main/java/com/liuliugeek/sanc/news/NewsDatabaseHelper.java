package com.liuliugeek.sanc.news;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 73732 on 2016/8/28.
 */
public class NewsDatabaseHelper  extends SQLiteOpenHelper {

    public static final String CREATE_NEWS = "create table news("
            + "news_id integer primary key autoincrement, "
            + "news_typeid integer, "
            + "news_arcid integer REFERENCES board(board_id), "
            + "news_url text, "
            + "news_date text, "
            + "news_title text, "
            + "news_content text)";
    public static final String CREATE_BOARD = "create table board("
            + "board_id integer primary key, "
            + "board_name text) ";


    public NewsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOARD);
        db.execSQL(CREATE_NEWS);
        db.execSQL("insert into board (board_id, board_name) values(?, ?)",
                new String[] { "3","扬大要闻" });
        db.execSQL("insert into board (board_id, board_name) values(?, ?)",
                new String[] { "4","媒体扬大" });
        db.execSQL("insert into board (board_id, board_name) values(?, ?)",
                new String[] { "5","综合报道" });
        db.execSQL("insert into board (board_id, board_name) values(?, ?)",
                new String[] { "8","校报传真" });
        db.execSQL("insert into board (board_id, board_name) values(?, ?)",
                new String[] { "9","暖情校园" });
        db.execSQL("insert into board (board_id, board_name) values(?, ?)",
                new String[] { "10","缤纷扬大" });
        Log.v("sql_create", "sql create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
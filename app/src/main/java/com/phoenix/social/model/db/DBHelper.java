package com.phoenix.social.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phoenix.social.model.dao.ContactTable;

import static android.R.attr.version;

/**
 * Created by flashing on 2017/5/24.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建联系人的表
        db.execSQL(ContactTable.CREATE_TAB);

        //创建邀请信息的表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

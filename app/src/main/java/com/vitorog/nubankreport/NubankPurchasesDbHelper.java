package com.vitorog.nubankreport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vitor.gomes on 27/04/2016.
 */
public class NubankPurchasesDbHelper extends SQLiteOpenHelper{

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NubankPurchasesContract.PurchaseEntry.TABLE_NAME + " (" +
                    NubankPurchasesContract.PurchaseEntry._ID + " INTEGER PRIMARY KEY," +
                    NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_VALUE + TEXT_TYPE + COMMA_SEP +
                    NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_PLACE + TEXT_TYPE + COMMA_SEP +
                    NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE +
            " )";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NubankPurchases.db";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NubankPurchasesContract.PurchaseEntry.TABLE_NAME;

    public NubankPurchasesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

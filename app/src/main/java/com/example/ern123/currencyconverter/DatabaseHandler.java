package com.example.ern123.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ernsu on 2017-11-29.
 */

// Not used.

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "currency_converter";
    private static final String TABLE_CURRENCY = "currency";
    private static final String KEY_1 = "base";
    private static final String KEY_2 = "to";
    private static final String EXCHANGE_RATE = "exchange_rate";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_CURRENCY + "("
                        + KEY_1 + " TEXT,"
                        + KEY_2 + " TEXT,"
                        + EXCHANGE_RATE + " REAL,"
                        + "PRIMARY KEY (" + KEY_1 + "," + KEY_2 + ")" + ")";
        */

        String CREATE_CONTACTS_TABLE = "CREATE TABLE currency(base TEXT, to TEXT, exchange_rate REAL, PRIMARY KEY (base, to))";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);

        // Create tables again
        onCreate(db);
    }

    public boolean insertCurrency (String base, String to, Double exchangeRate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_1, base);
        contentValues.put(KEY_2, to);
        contentValues.put(EXCHANGE_RATE, exchangeRate);
        db.insert(TABLE_CURRENCY, null, contentValues); // Vill man istället uppdatera blir det db.update
        return true;
    }

    public Double getExchangeRate(String base, String to) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from currency where base=" + base + "AND to=" + to + "", null );
        Double exchangeRate = cursor.getDouble(cursor.getColumnIndex("exchange_rate"));
        cursor.close();
        return exchangeRate;
    }
}

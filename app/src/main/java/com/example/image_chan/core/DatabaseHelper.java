package com.example.image_chan.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String dTAG = "DATABASE";

    private static final String TABLE_NAME = "favorites";

    private static final String url = "URL"; // COL1
    private static final String tags = "TAGS"; // COL2
    private static final String ratingLong = "RATING_L"; // COL2
    private static final String ratingShort = "RATING_S"; // COL3
    private static final String score = "SCORE"; // COL4
    private static final String directory = "DIR"; // COL5
    private static final String name = "NAME"; // COL6

    //private static final String width = "WIDTH"; // COL7
    //private static final String height = "HEIGHT"; // COL8

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableCreation = "CREATE TABLE "
                + TABLE_NAME + " (URL TEXT PRIMARY KEY DEFAULT \"REPLACEABLE\", "
                + tags + " TEXT  DEFAULT \"[no tags]\", "
                + ratingLong + " TEXT DEFAULT \"safe\", "
                + ratingShort + " TEXT DEFAULT \"s\", "
                + score + " INTEGER DEFAULT 0, "
                + directory + " TEXT DEFAULT \"NULL\", "
                + name + " TEXT DEFAULT \"0\")";

        sqLiteDatabase.execSQL(tableCreation);
    }

    public boolean ifExists(String url) {
        Cursor data = findDataByURL(url);

        return data.getCount() != 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String exec = "DROP TABLE IF EXISTS " + TABLE_NAME;

        sqLiteDatabase.execSQL(exec);
        onCreate(sqLiteDatabase);
    }

    public Cursor findDataByURL(String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE URL = \"" + url + "\"";

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public boolean removeFavorite(String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_NAME + " WHERE URL = \"" + url + "\"";

        db.delete(TABLE_NAME, "URL = \"" + url + "\"", null);

        Cursor data = findDataByURL(url);

        return data.getCount() == 0;
    }

    public boolean addFavorite(String[] entry) {
        /*
            @param String[] entry
            * - required

            0 - URL*
            1 - TAGS*
            2 - RATING LONG*
            3 - RATING SHORT*
            4 - SCORE
            5 - DIRECTORY
            6 - NAME
            7 - WIDTH*
            8 - HEIGHT*
         */

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(url, entry[0]);
        values.put(tags, entry[1]);
        values.put(ratingLong, entry[2]);
        values.put(ratingShort, entry[3]);
        values.put(score, entry[4]);
        values.put(directory, entry[5]);
        values.put(name, entry[6]);

        System.out.println("FAVORITES DB: ADDING ENTRY " + entry[0] + " TO DATABASE");

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }
}

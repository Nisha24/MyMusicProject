package com.nisha.apps.musicproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class CurrentPlaylistOperations {
    public static final String TAG = "Current Playlist Database";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            FavoritesDBHandler.COLUMN_ID,
            FavoritesDBHandler.COLUMN_TITLE,
            FavoritesDBHandler.COLUMN_SUBTITLE,
            FavoritesDBHandler.COLUMN_PATH
    };

    public CurrentPlaylistOperations(Context context) {
        dbHandler = new CurrentDBHandler(context);
    }

    @SuppressLint("LongLogTag")
    public void open() {
        Log.i(TAG, " Database Opened");
        database = dbHandler.getWritableDatabase();
    }

    @SuppressLint("LongLogTag")
    public void close() {
        Log.i(TAG, "Database Closed");
        dbHandler.close();
    }

    public void addSongFav(SongsList songsList) {
        open();
        ContentValues values = new ContentValues();
        values.put(CurrentDBHandler.COLUMN_TITLE, songsList.getTitle());
        values.put(CurrentDBHandler.COLUMN_SUBTITLE, songsList.getSubTitle());
        values.put(CurrentDBHandler.COLUMN_PATH, songsList.getPath());

        database.insertWithOnConflict(CurrentDBHandler.TABLE_SONGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        close();
    }

    public ArrayList<SongsList> getAllCurrent() {
        open();
        Cursor cursor = database.query(CurrentDBHandler.TABLE_SONGS, allColumns,
                null, null, null, null, null);
        ArrayList<SongsList> curSongs = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                SongsList songsList = new SongsList(cursor.getString(cursor.getColumnIndex(CurrentDBHandler.COLUMN_TITLE))
                        , cursor.getString(cursor.getColumnIndex(CurrentDBHandler.COLUMN_SUBTITLE))
                        , cursor.getString(cursor.getColumnIndex(CurrentDBHandler.COLUMN_PATH)));
                curSongs.add(songsList);
            }
        }
        close();
        return curSongs;
    }

    public void removeSong(String songPath) {
        open();
        String whereClause =
                CurrentDBHandler.COLUMN_PATH + "=?";
        String[] whereArgs = new String[]{songPath};

        database.delete(CurrentDBHandler.TABLE_SONGS, whereClause, whereArgs);
        close();
    }

}

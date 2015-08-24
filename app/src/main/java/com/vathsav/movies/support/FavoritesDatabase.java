package com.vathsav.movies.support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Favorites";
    private static final String TABLE_MOVIES = "Movies";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RATING = "rating";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_SYNOPSIS= "synopsis";
    private static final String KEY_BACKDROP = "backdrop";
    private static final String KEY_IMAGE = "image";

    public FavoritesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_MOVIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_RATING + " TEXT," + KEY_RELEASE_DATE + " TEXT," + KEY_SYNOPSIS + " TEXT," + KEY_BACKDROP+ " TEXT," + KEY_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);

        // Create tables again
        onCreate(db);
    }

    // Insert New Item
    public void addItem(GridItem gridItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, gridItem.getMovieID());
        values.put(KEY_TITLE, gridItem.getTitle());
        values.put(KEY_RATING, gridItem.getUserRating());
        values.put(KEY_RELEASE_DATE, gridItem.getReleaseDate());
        values.put(KEY_SYNOPSIS, gridItem.getSynopsis());
        values.put(KEY_BACKDROP, gridItem.getBackdrop());
        values.put(KEY_IMAGE, gridItem.getImage());

        Log.d("DB: ", String.valueOf(values));
        // Inserting Row
        db.insert(TABLE_MOVIES, null, values);
        db.close(); // Closing database connection
    }

    // Getting all Items
    public List<GridItem> getAllItems() {
        List<GridItem> itemsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GridItem movie = new GridItem();
                movie.setMovieID(cursor.getString(0));
                movie.setMovieTitle(cursor.getString(1));
                movie.setRating(cursor.getString(2));
                movie.setReleaseDate(cursor.getString(3));
                movie.setSynopsis(cursor.getString(4));
                movie.setBackdrop(cursor.getString(5));
                movie.setImage(cursor.getString(6));
                Log.d("DB Fetch", cursor.getString(0) + " "  + cursor.getString(1) + " "  + cursor.getString(2) + " "  + cursor.getString(3)
                        + " "  + cursor.getString(4) + " "  + cursor.getString(5) + " "  + cursor.getString(6));

                // Adding contact to list
                itemsList.add(movie);
            } while (cursor.moveToNext());
        }

        // return contact list
        return itemsList;
    }

    // Deleting Item
    public int deleteItem(GridItem movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABLE_MOVIES, KEY_TITLE + " = ?",
                new String[]{String.valueOf(movie.getTitle())});
        db.close();
        return delete;
    }


    // Getting Item Count
    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MOVIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

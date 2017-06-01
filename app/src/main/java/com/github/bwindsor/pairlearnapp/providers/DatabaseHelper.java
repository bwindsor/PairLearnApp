package com.github.bwindsor.pairlearnapp.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.UserDictionary;

/**
 * Created by Ben on 30/05/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 3;

    private static final String PAIRS_TABLE_NAME = WordsContentProvider.PAIRS_TABLE_NAME;
    private static final String CATEGORIES_TABLE_NAME = WordsContentProvider.CATEGORIES_TABLE_NAME;
    private static final String PROGRESS_TABLE_NAME = WordsContentProvider.PROGRESS_TABLE_NAME;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PAIRS_TABLE_NAME + " ("
                + WordsContract.Pairs.WORD_PAIR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WordsContract.Pairs.WORD1        + " VARCHAR(255) NOT NULL,"
                + WordsContract.Pairs.WORD2        + " VARCHAR(255) NOT NULL,"
                + WordsContract.Pairs.CATEGORY_ID  + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + WordsContract.Pairs.CATEGORY_ID + ") REFERENCES " + CATEGORIES_TABLE_NAME + "(" + WordsContract.Categories.CATEGORY_ID + "),"
                + "UNIQUE(" + WordsContract.Pairs.WORD1 + "," + WordsContract.Pairs.WORD2 + "," + WordsContract.Pairs.CATEGORY_ID + ")"
                + ");");
        db.execSQL("CREATE TABLE " + CATEGORIES_TABLE_NAME + " ("
                + WordsContract.Categories.CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WordsContract.Categories.NAME + " VARCHAR(255) NOT NULL UNIQUE,"
                + WordsContract.Categories.IS_IN_TEST + " INT NOT NULL"
                + ");");
        db.execSQL("CREATE TABLE " + PROGRESS_TABLE_NAME + " ("
                + WordsContract.Progress.PROGRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WordsContract.Progress.PAIR_ID     + " INTEGER NOT NULL UNIQUE,"
                + WordsContract.Progress.NUM_CORRECT + " INTEGER NOT NULL,"
                + WordsContract.Progress.NUM_WRONG   + " INTEGER NOT NULL,"
                + WordsContract.Progress.UNIX_TIME_LAST_CORRECT + " INTEGER NOT NULL,"  // This is stored as a unix time
                + "FOREIGN KEY(" + WordsContract.Progress.PAIR_ID + ") REFERENCES " + PAIRS_TABLE_NAME + "(" + WordsContract.Pairs.WORD_PAIR_ID + ")"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PAIRS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROGRESS_TABLE_NAME);
        onCreate(db);
    }
}
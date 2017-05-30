package com.github.bwindsor.pairlearnapp.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * @author Ben Windsor
 *
 */
public class WordsContentProvider extends ContentProvider {

    private static final String TAG = "WordsContentProvider";

    public static final String AUTHORITY = "com.github.bwindsor.pairlearnapp.providers.WordsContentProvider";

    private static final UriMatcher sUriMatcher;

    public static final String PAIRS_TABLE_NAME = "pair";
    public static final String CATEGORIES_TABLE_NAME = "category";
    public static final String PROGRESS_TABLE_NAME = "progress";

    private static final int PAIRS = 1;
    private static final int PAIRS_ID = 2;
    private static final int PAIRS_BY_CATEGORY = 7;
    private static final int CATEGORIES = 3;
    private static final int CATEGORIES_ID = 4;
    private static final int PROGRESS = 5;
    private static final int PROGRESS_ID = 6;
    private static final int PROGRESS_BY_PAIR_ID = 8;
    private static final int PAIR_PROGRESS = 9;

    private DatabaseHelper dbHelper;

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        /*
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                break;
            case PAIRS_ID:
                where = where + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(PAIRS_TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
        */
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PAIRS:
            case PAIRS_ID:
            case PAIRS_BY_CATEGORY:
                return WordsContract.Pairs.CONTENT_TYPE;
            case CATEGORIES:
            case CATEGORIES_ID:
                return WordsContract.Categories.CONTENT_TYPE;
            case PROGRESS:
            case PROGRESS_ID:
            case PROGRESS_BY_PAIR_ID:
                return WordsContract.Progress.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Uri noteUri;
        long rowId;

        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                // This returns -1 on failure and should throw an exception too
                rowId = db.insertOrThrow(PAIRS_TABLE_NAME, null, values);
                noteUri = ContentUris.withAppendedId(WordsContract.Pairs.CONTENT_URI, rowId);
                break;
            case CATEGORIES:
                rowId = db.insertOrThrow(PAIRS_TABLE_NAME, null, values);
                noteUri = ContentUris.withAppendedId(WordsContract.Categories.CONTENT_URI, rowId);
                break;
            case PROGRESS:
                rowId = db.insertOrThrow(PAIRS_TABLE_NAME, null, values);
                noteUri = ContentUris.withAppendedId(WordsContract.Progress.CONTENT_URI, rowId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(noteUri, null);
        }
        return noteUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                qb.setTables(PAIRS_TABLE_NAME);
                break;
            case PAIRS_ID:
                qb.setTables(PAIRS_TABLE_NAME);
                selection = addWhereCondition(selection, WordsContract.Progress.PAIR_ID + " = " + uri.getLastPathSegment());
                break;
            case PAIR_PROGRESS:
                qb.setTables(PAIRS_TABLE_NAME + " INNER JOIN " + PROGRESS_TABLE_NAME + " ON " +
                        PAIRS_TABLE_NAME + "." + WordsContract.Pairs.WORD_PAIR_ID + " = " +
                        PROGRESS_TABLE_NAME + "." + WordsContract.Progress.PAIR_ID);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case PAIRS_ID:
                where = addWhereCondition(where, WordsContract.Pairs.WORD_PAIR_ID + " = " + uri.getLastPathSegment());
                count = db.update(PAIRS_TABLE_NAME, values, where, whereArgs);
                break;
            case PROGRESS_BY_PAIR_ID:
                where = addWhereCondition(where, WordsContract.Progress.PAIR_ID + " = " + uri.getLastPathSegment());
                count = db.update(PAIRS_TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    private String addWhereCondition(String where, String condition) {
        if (where != null && where.length() > 0) {
            where += " AND ";
        } else {
            where = "";
        }
        where += condition;
        return where;
    }
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PAIRS_TABLE_NAME, PAIRS);
        sUriMatcher.addURI(AUTHORITY, PAIRS_TABLE_NAME + "/#", PAIRS_ID);
        sUriMatcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME, CATEGORIES);
        sUriMatcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME + "/#", CATEGORIES_ID);
        sUriMatcher.addURI(AUTHORITY, PROGRESS_TABLE_NAME, PROGRESS);
        sUriMatcher.addURI(AUTHORITY, PROGRESS_TABLE_NAME + "/#", PROGRESS_ID);
        sUriMatcher.addURI(AUTHORITY, PROGRESS_TABLE_NAME + "/" + PAIRS_TABLE_NAME + "/#", PROGRESS_BY_PAIR_ID);
        sUriMatcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME + "/#/" + PAIRS_TABLE_NAME, PAIRS_BY_CATEGORY);
        sUriMatcher.addURI(AUTHORITY, PAIRS_TABLE_NAME + "_" + PROGRESS_TABLE_NAME, PAIR_PROGRESS);
    }
}
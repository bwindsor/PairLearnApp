package com.github.bwindsor.pairlearnapp.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

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
    private static final int CATEGORIES = 4;
    private static final int PROGRESS = 6;
    private static final int PAIR_CATEGORY = 10;

    private DatabaseHelper dbHelper;

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;
        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                tableName = PAIRS_TABLE_NAME;
                break;
            case CATEGORIES:
                tableName = CATEGORIES_TABLE_NAME;
                break;
            case PROGRESS:
                tableName = PROGRESS_TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(tableName, where, whereArgs);

        Context context = getContext();
        if (context != null && count > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                return WordsContract.Pairs.CONTENT_TYPE;
            case CATEGORIES:
                return WordsContract.Categories.CONTENT_TYPE;
            case PROGRESS:
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

        Uri newUri;
        long rowId;

        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                // This returns -1 on failure
                rowId = db.insert(PAIRS_TABLE_NAME, null, values);
                newUri = ContentUris.withAppendedId(WordsContract.Pairs.CONTENT_URI, rowId);
                break;
            case CATEGORIES:
                rowId = db.insert(CATEGORIES_TABLE_NAME, null, values);
                newUri = ContentUris.withAppendedId(WordsContract.Categories.CONTENT_URI, rowId);
                break;
            case PROGRESS:
                rowId = db.insert(PROGRESS_TABLE_NAME, null, values);
                newUri = ContentUris.withAppendedId(WordsContract.Progress.CONTENT_URI, rowId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Context context = getContext();
        if (context != null && rowId >= 0) {
            context.getContentResolver().notifyChange(newUri, null);
        }
        return newUri;
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
            case PAIR_CATEGORY:
                qb.setTables(PAIRS_TABLE_NAME + " INNER JOIN " + CATEGORIES_TABLE_NAME + " ON " +
                        PAIRS_TABLE_NAME + "." + WordsContract.Pairs.CATEGORY_ID + " = " +
                        CATEGORIES_TABLE_NAME + "." + WordsContract.Categories.CATEGORY_ID);
                break;
            case CATEGORIES:
                qb.setTables(CATEGORIES_TABLE_NAME);
                break;
            case PROGRESS:
                qb.setTables(PROGRESS_TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        Context context = getContext();
        if (context != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case PAIRS:
                count = db.update(PAIRS_TABLE_NAME, values, where, whereArgs);
                break;
            case CATEGORIES:
                count = db.update(CATEGORIES_TABLE_NAME, values, where, whereArgs);
                break;
            case PROGRESS:
                count = db.update(PROGRESS_TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Context context = getContext();
        if (context != null && count > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
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
        sUriMatcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME, CATEGORIES);
        sUriMatcher.addURI(AUTHORITY, PROGRESS_TABLE_NAME, PROGRESS);
        sUriMatcher.addURI(AUTHORITY, PAIRS_TABLE_NAME + CATEGORIES_TABLE_NAME, PAIR_CATEGORY);
    }
}
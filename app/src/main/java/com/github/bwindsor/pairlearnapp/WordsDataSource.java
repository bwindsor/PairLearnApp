package com.github.bwindsor.pairlearnapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.annotation.Nullable;
import android.widget.CursorAdapter;

import com.github.bwindsor.pairlearnapp.providers.WordsContentProvider;
import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.sql.Date;
import java.util.List;

/**
 * Created by Ben on 23/05/2017.
 * This provides a layer between the data store on disk and the rest of the program.
 * Data is stored in an SQLLite database.
 */

public class WordsDataSource {

    private WordsDataSource(){}

    public static Cursor getPairs(Context context) {
        return getPairs(context, null);
    }
    public static Cursor getPairsProgress(Context context) {
        return getPairsProgress(context, null);
    }
    public static Cursor getPairs(Context context, int categoryId) {
        return getPairs(context, new int[] {categoryId});
    }
    public static Cursor getPairsProgress(Context context, int categoryId) {
        return getPairsProgress(context, new int[] {categoryId});
    }
    public static Cursor getPairs(Context context, @Nullable int[] categoryIds) {
        return getPairs(context, categoryIds, null);
    }
    public static Cursor getPairsProgress(Context context, @Nullable int[] categoryIds) {
        return getPairsProgress(context, categoryIds, null);
    }
    public static Cursor getPairs(Context context, @Nullable int[] categoryIds, @Nullable Boolean isRandomOrder) {
        return _getPairsProgress(context, categoryIds, isRandomOrder, null, null);
    }
    public static Cursor getPairsProgress(Context context, @Nullable int[] categoryIds, @Nullable Boolean isRandomOrder) {
        return _getPairsProgress(context, categoryIds, isRandomOrder, true, null);
    }
    public static Cursor getPairsProgress(Context context, @Nullable int[] categoryIds, @Nullable Boolean isRandomOrder, @Nullable Integer maxTimesCorrect) {
        return _getPairsProgress(context, categoryIds, isRandomOrder, true, maxTimesCorrect);
    }
    private static Cursor _getPairsProgress(Context context, @Nullable int[] categoryIds, @Nullable Boolean isRandomOrder, @Nullable Boolean alsoGetProgress, @Nullable Integer maxTimesCorrect) {
        String whereClause = null;
        String[] whereArgs = null;
        if (categoryIds != null) {
            whereClause = WordsContract.Pairs.CATEGORY_ID + makeInClause(categoryIds.length);
            whereArgs = makeWhereArgs(categoryIds);
        }
        String orderClause = WordsContract.Pairs.WORD_PAIR_ID + " ASC";
        if (isRandomOrder != null && isRandomOrder) {
            orderClause = "RANDOM()";
        }
        Uri contentUri = WordsContract.Pairs.CONTENT_URI;
        if (alsoGetProgress != null && alsoGetProgress) {
            contentUri = WordsContract.PairProgress.CONTENT_URI;
            if (maxTimesCorrect != null) {
                if (whereClause == null) {
                    whereClause = "";
                } else {
                    whereClause += " AND ";
                }
                whereClause += "(" + WordsContract.Progress.NUM_CORRECT + "<=" + String.valueOf(maxTimesCorrect)
                                + " OR " + WordsContract.Progress.NUM_CORRECT + " IS NULL" + ")";
            }
        }

        return context.getContentResolver().query(
                contentUri,
                null,
                whereClause,
                whereArgs,
                orderClause);
    }

    public static void addPair(Context context, String leftWord, String rightWord, int categoryId) {
        ContentValues values = new ContentValues();
        values.put(WordsContract.Pairs.WORD1, leftWord);
        values.put(WordsContract.Pairs.WORD2, rightWord);
        values.put(WordsContract.Pairs.CATEGORY_ID, categoryId);
        context.getContentResolver().insert(WordsContract.Pairs.CONTENT_URI, values);
    }
    public static void removePairs(Context context, List<Integer> pairIds) {
        if (pairIds.size() == 0) { return; }
        context.getContentResolver().delete(WordsContract.Pairs.CONTENT_URI,
                WordsContract.Pairs.WORD_PAIR_ID + makeInClause(pairIds.size()),
                makeWhereArgs(pairIds)
                );
    }
    public static void updatePair(Context context, int pairId, String leftWord, String rightWord) {
        ContentValues values = new ContentValues();
        values.put(WordsContract.Pairs.WORD1, leftWord);
        values.put(WordsContract.Pairs.WORD2, rightWord);
        context.getContentResolver().update(WordsContract.Pairs.CONTENT_URI, values,
                WordsContract.Pairs.WORD_PAIR_ID + "=?",
                new String[] {String.valueOf(pairId)});
    }
    public static Cursor getCategory(Context context, int categoryId) {
        return context.getContentResolver().query(WordsContract.Categories.CONTENT_URI, null,
                WordsContract.Categories.CATEGORY_ID + "=?",
                new String[] {String.valueOf(categoryId)},
                null);
    }
    public static Cursor getCategory(Context context, String categoryName) {
        return context.getContentResolver().query(WordsContract.Categories.CONTENT_URI, null,
                WordsContract.Categories.NAME + "=?",
                new String[] {categoryName},
                null);
    }
    public static Cursor getCategories(Context context) {
        return context.getContentResolver().query(WordsContract.Categories.CONTENT_URI, null,
                null,
                null,
                null);
    }
    public static int getNumCategoriesInTest(Context context) {
        Cursor c = context.getContentResolver().query(WordsContract.Categories.CONTENT_URI,
                new String[] {"count(*) as count"},
                WordsContract.Categories.IS_IN_TEST + "=1",
                null,
                null);
        int count = 0;
        if (c.moveToNext()) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }
    public static void addCategory(Context context, String categoryName) {
        ContentValues values = new ContentValues();
        values.put(WordsContract.Categories.NAME, categoryName);
        values.put(WordsContract.Categories.IS_IN_TEST, 0);
        context.getContentResolver().insert(WordsContract.Categories.CONTENT_URI, values);
    }
    public static int setCategoryIsInTest(Context context, int categoryId, boolean isInTest) {
        ContentValues values = new ContentValues();
        values.put(WordsContract.Categories.IS_IN_TEST, isInTest ? 1 : 0);
        int numModified = context.getContentResolver().update(WordsContract.Categories.CONTENT_URI,
                values, WordsContract.Categories.CATEGORY_ID + "=?",
                new String[] {String.valueOf(categoryId)});
        return numModified;
    }

    public static Cursor getProgress(Context context, int pairId) {
        return getProgress(context, new int[] {pairId});
    }
    public static Cursor getProgress(Context context, @Nullable int[] pairIds) {
        String[] projection = {WordsContract.Progress.PROGRESS_ID, WordsContract.Progress.PAIR_ID, WordsContract.Progress.NUM_WRONG, WordsContract.Progress.NUM_CORRECT, WordsContract.Progress.UNIX_TIME_LAST_CORRECT};
        String whereClause = null;
        String[] whereArgs = null;
        if (pairIds != null) {
            whereClause = WordsContract.Progress.PAIR_ID + makeInClause(pairIds.length);
            whereArgs = makeWhereArgs(pairIds);
        }
        return context.getContentResolver().query(WordsContract.Progress.CONTENT_URI, projection,
                whereClause, whereArgs, null);
    }
    public static Uri addProgress(Context context, int pairId) {
        ContentValues values = new ContentValues();
        values.put(WordsContract.Progress.PAIR_ID, pairId);
        values.put(WordsContract.Progress.NUM_CORRECT, 0);
        values.put(WordsContract.Progress.NUM_WRONG, 0);
        values.put(WordsContract.Progress.UNIX_TIME_LAST_CORRECT, 0);
        return context.getContentResolver().insert(WordsContract.Progress.CONTENT_URI, values);
    }
    public static void updateProgress(Context context, int progressId,
                                      @Nullable Integer numCorrect, @Nullable Integer numWrong,
                                      @Nullable Boolean updateCorrectTime) {
        ContentValues values = new ContentValues();
        if (numCorrect != null) {
            values.put(WordsContract.Progress.NUM_CORRECT, numCorrect);
        }
        if (numWrong != null) {
            values.put(WordsContract.Progress.NUM_WRONG, numWrong);
        }
        if (updateCorrectTime != null) {
            values.put(WordsContract.Progress.UNIX_TIME_LAST_CORRECT, (int)(System.currentTimeMillis()/1000));
        }
        context.getContentResolver().update(WordsContract.Progress.CONTENT_URI,
                values,
                WordsContract.Progress.PROGRESS_ID + "=?",
                new String[] {String.valueOf(progressId)}
        );
    }

    private static String makeInClause(int length) {
        String s = " IN (";
        for (int i = 0; i < length; i++) {
            s += (i == length-1) ? "?" : "?,";
        }
        s += ")";
        return s;
    }
    private static String[] makeWhereArgs(int[] integers) {
        String[] s = new String[integers.length];
        for (int i = 0; i < integers.length; i++) {
            s[i] = String.valueOf(integers[i]);
        }
        return s;
    }
    private static String[] makeWhereArgs(List<Integer> integers) {
        String[] s = new String[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            s[i] = String.valueOf(integers.get(i));
        }
        return s;
    }
}

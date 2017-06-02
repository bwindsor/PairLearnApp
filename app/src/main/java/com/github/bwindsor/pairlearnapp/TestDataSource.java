package com.github.bwindsor.pairlearnapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.util.Log;

import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ben on 22/05/2017.
 * This provides a data access abstraction for accessing the data required to test the user on
 * their vocabulary.
 */

public class TestDataSource {
    private Cursor mCursor;
    private boolean mIsReversed;
    private Context mContext;

    public void TestDataSource(){}

    /**
     * Initialises the data source
     * @param testConfig - The test configuration
     * @return void
     */
    public void init(TestConfig testConfig, Context context) {
        Cursor c = WordsDataSource.getCategories(context);
        List<Integer> catIds = new ArrayList<>();
        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex(WordsContract.Categories.IS_IN_TEST)) != 0) {
                catIds.add(c.getInt(c.getColumnIndex(WordsContract.Categories.CATEGORY_ID)));
            }
        }
        int[] catIdsInt = new int[catIds.size()];
        for (int i = 0; i < catIds.size(); i++) {
            catIdsInt[i] = catIds.get(i);
        }
        mContext = context;
        mCursor = WordsDataSource.getPairsProgress(context, catIdsInt, true);
        mIsReversed = testConfig.getIsRightToLeft();
    }

    public Pair<String, String> getNextPair() {
        if (mCursor.isAfterLast()) {
            return null;
        } else if (mCursor.moveToNext()) {
            return getCurrentPair();
        } else {
            return null;
        }
    }
    public Pair<String, String> getCurrentPair() {
        if (isCursorOutOfRange()) {
            return null;
        } else if (mIsReversed) {
            return new Pair<>(mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD2)),
                              mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD1)));
        } else {
            return new Pair<>(mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD1)),
                    mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD2)));
        }
    }
    public HashMap<String, Integer> getCurrentProgress() {
        if (isCursorOutOfRange()) {
            return null;
        } else {
            HashMap<String, Integer> h = new HashMap<>();
            h.put(WordsContract.Progress.NUM_CORRECT, mCursor.getInt(mCursor.getColumnIndex(WordsContract.Progress.NUM_CORRECT)));
            h.put(WordsContract.Progress.NUM_WRONG, mCursor.getInt(mCursor.getColumnIndex(WordsContract.Progress.NUM_WRONG)));
            return h;
        }
    }

    public void reset() {

    }

    public void markCorrect() {
        if (isCursorOutOfRange()) {
            return;
        } else {
            int progressId = (int)createProgressIfRequired();
            int numCorrect = mCursor.getInt(mCursor.getColumnIndex(WordsContract.Progress.NUM_CORRECT)) + 1;
            WordsDataSource.updateProgress(mContext, progressId, numCorrect, null, true);
        }
    }

    public void markWrong() {
        if (isCursorOutOfRange()) {
            return;
        } else {
            int progressId = (int)createProgressIfRequired();
            int numWrong = mCursor.getInt(mCursor.getColumnIndex(WordsContract.Progress.NUM_WRONG)) + 1;
            WordsDataSource.updateProgress(mContext, progressId, null, numWrong, false);
        }
    }

    private long createProgressIfRequired() {
        if (mCursor.getString(mCursor.getColumnIndex(WordsContract.PairProgress.ID_PROGRESS)) == null) {
            Uri uri = WordsDataSource.addProgress(mContext,
                    mCursor.getInt(mCursor.getColumnIndex(WordsContract.PairProgress.ID_PAIR)));
            return ContentUris.parseId(uri);
        }
        return mCursor.getLong(mCursor.getColumnIndex(WordsContract.PairProgress.ID_PROGRESS));
    }
    private boolean isCursorOutOfRange() {
        return mCursor==null || mCursor.isAfterLast() || mCursor.isBeforeFirst();
    }
}
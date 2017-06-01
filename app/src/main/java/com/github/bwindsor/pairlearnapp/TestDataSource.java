package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.support.v4.util.Pair;
import android.util.Log;

import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 22/05/2017.
 * This provides a data access abstraction for accessing the data required to test the user on
 * their vocabulary.
 */

public class TestDataSource {
    private Cursor mCursor;
    private boolean mIsReversed;

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
        mCursor = WordsDataSource.getPairs(context, catIdsInt);
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
        if (mCursor.isAfterLast() || mCursor.isBeforeFirst()) {
            return null;
        } else if (mIsReversed) {
            return new Pair<>(mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD2)),
                              mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD1)));
        } else {
            return new Pair<>(mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD1)),
                    mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD2)));
        }
    }

    public void reset() {

    }

    public void markCorrect() {

    }

    public void markWrong() {

    }

    public boolean trySaveProgress() {
        return false;
    }

}
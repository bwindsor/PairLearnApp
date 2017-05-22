package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.util.Pair;
import android.util.Log;

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

    private List<Pair<String, String>> mPairs = new ArrayList<>();
    private int mCurrentIdx = -1;

    public void TestDataSource(){}

    /**
     *
     * @param testConfig - The test configuration
     * @return void
     */
    public void init(TestConfig testConfig, Context context) {
        /*
        for(int i = 0; i < 2; i++ ){
            mPairs.add(new Pair<String, String>("a" + i,"b" + i));
        }
*/

        AssetManager assetManager = context.getAssets();
        try {
            InputStream csvStream = assetManager.open("words.csv");
            String[] lines = getAllLinesFromInputStream(csvStream);
            csvStream.close();

            for (int i = 0; i < lines.length; i++) {
                String[] parts = lines[i].split(",");
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                if (testConfig.hasCategory(parts[0])) {
                    Pair<String, String> p = new Pair<>(parts[1], parts[2]);
                    mPairs.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Put pairs in random order
        java.util.Collections.shuffle(mPairs);

        mCurrentIdx = -1;
    }

    public Pair<String, String> getNextPair() {
        mCurrentIdx++;
        if (mCurrentIdx >= mPairs.size()) {
            return null;
        }
        return mPairs.get(mCurrentIdx);
    }
    public Pair<String, String> getCurrentPair() {
        if (mCurrentIdx < 0 || mCurrentIdx >= mPairs.size()) {
            return null;
        }
        return mPairs.get(mCurrentIdx);
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

    private static String[] getAllLinesFromInputStream(InputStream inputStream) throws IOException
    {
        String s = "";
        while (inputStream.available() > 0) {
            s += (char) inputStream.read();
        }
        return s.split("\r?\n");
    }
}
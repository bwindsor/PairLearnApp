package com.github.bwindsor.pairlearnapp;

import android.support.v4.util.Pair;

/**
 * Created by Ben on 22/05/2017.
 * This provides a data access abstraction for accessing the data required to test the user on
 * their vocabulary.
 */

public class TestDataSource {

    public void TestDataSource(){}

    /**
     *
     * @param testConfig - The test configuration
     * @return void
     */
    public void init(TestConfig testConfig) {

    }

    public Pair<String, String> getNextPair() {
        return new Pair<String, String>("Hello", "World");
    }
    public Pair<String, String> getCurrentPair() {
        return new Pair<String, String>("Hello", "World");
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
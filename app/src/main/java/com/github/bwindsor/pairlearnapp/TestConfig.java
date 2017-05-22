package com.github.bwindsor.pairlearnapp;

/**
 * Created by Ben on 22/05/2017.
 */

public class TestConfig {
    private int mMaxCorrect;

    public TestConfig(int maxCorrect) {
        this.mMaxCorrect = maxCorrect;
    }

    public int getMaxCorrect()
    {
        return this.mMaxCorrect;
    }
}

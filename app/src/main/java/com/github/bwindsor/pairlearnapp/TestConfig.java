package com.github.bwindsor.pairlearnapp;

/**
 * Created by Ben on 22/05/2017.
 * TestConfig contains the elements required by TestDataSource to decide what data it returns.
 */
public class TestConfig {
    private int mMaxCorrect;
    private boolean mTestRightToLeft;

    public TestConfig(int maxCorrect, boolean testRightToLeft) {
        this.mMaxCorrect = maxCorrect;
        this.mTestRightToLeft = testRightToLeft;
    }

    /**
     * Gets if a test direction is reversed
     * @return whether test direction is reversed
     */
    public boolean getIsRightToLeft() { return mTestRightToLeft; }

    public int getMaxCorrect()
    {
        return this.mMaxCorrect;
    }

}

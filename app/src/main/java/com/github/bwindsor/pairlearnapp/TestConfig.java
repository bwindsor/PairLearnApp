package com.github.bwindsor.pairlearnapp;

/**
 * Created by Ben on 22/05/2017.
 */

public class TestConfig {
    private int mMaxCorrect;
    private String[] mCategories;
    private boolean mTestRightToLeft;

    public TestConfig(String[] categories, int maxCorrect, boolean testRightToLeft) {
        this.mMaxCorrect = maxCorrect;
        this.mCategories = categories;
        this.mTestRightToLeft = testRightToLeft;
    }

    public boolean getIsRightToLeft() { return mTestRightToLeft; }

    public int getMaxCorrect()
    {
        return this.mMaxCorrect;
    }

    public boolean hasCategory(String category) {
        for (int i = 0; i < mCategories.length; i++) {
            if (category.compareToIgnoreCase(mCategories[i]) == 0) {
                return true;
            }
        }
        return false;
    }
}

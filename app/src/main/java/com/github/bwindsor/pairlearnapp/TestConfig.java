package com.github.bwindsor.pairlearnapp;

/**
 * Created by Ben on 22/05/2017.
 * TestConfig contains the elements required by TestDataSource to decide what data it returns.
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

    /**
     * Gets if a test direction is reversed
     * @return whether test direction is reversed
     */
    public boolean getIsRightToLeft() { return mTestRightToLeft; }


    public int getMaxCorrect()
    {
        return this.mMaxCorrect;
    }

    /**
     * Tests if a category is present for this test
     * @param category the category to check
     * @return whether the category is present for this test
     */
    public boolean hasCategory(String category) {
        for (int i = 0; i < mCategories.length; i++) {
            if (category.compareToIgnoreCase(mCategories[i]) == 0) {
                return true;
            }
        }
        return false;
    }
}

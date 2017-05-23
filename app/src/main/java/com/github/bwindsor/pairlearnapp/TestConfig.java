package com.github.bwindsor.pairlearnapp;

/**
 * Created by Ben on 22/05/2017.
 */

public class TestConfig {
    private int mMaxCorrect;
    private String[] mCategories;

    public TestConfig(String[] categories, int maxCorrect) {
        this.mMaxCorrect = maxCorrect;
        this.mCategories = categories;
    }

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

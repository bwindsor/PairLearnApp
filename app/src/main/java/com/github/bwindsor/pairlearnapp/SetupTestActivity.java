package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetupTestActivity extends AppCompatActivity {

    static final int SELECT_CATEGORY_REQUEST = 1;

    static final float DEFAULT_TIME_LIMIT_SECONDS = 2;
    static final boolean DEFAULT_IS_REVERSE = false;

    private String[] mSelectedCategories = {};

    private static class ViewCache {
        TextView timeInput;
        CheckBox reverseDirection;
        Button selectCatButton;
    }
    private ViewCache mViewCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_test);

        cacheViewItems();

        // Load preferences
        loadPreferences();
    }
    private void cacheViewItems() {
        mViewCache = new ViewCache();
        mViewCache.timeInput = (TextView) findViewById(R.id.setup_test_time_input);
        mViewCache.reverseDirection = (CheckBox) findViewById(R.id.setup_test_reverse_direction);
        mViewCache.selectCatButton = (Button) findViewById(R.id.setup_test_select_cat_button);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                setSelectedCategories(data.getStringArrayExtra(CategoryPickerActivity.EXTRA_SELECTED_CATEGORIES));
            }
        }
    }

    /** Called when the user taps the go button */
    public void StartTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);

        float maxCorrect;
        try {
            maxCorrect = getTimeLimit();
        } catch (NumberFormatException e) {
            DialogHelper.ShowOKDialog(this, R.string.dialog_invalid_time_limit_message,
                    R.string.dialog_invalid_time_limit_title);
            return;
        }

        // TODO - implement UI for setting these things
        intent.putExtra(TestActivity.EXTRA_MAX_CORRECT, 2);

        intent.putExtra(TestActivity.EXTRA_LEFT_TO_RIGHT, getIsReverse());
        intent.putExtra(TestActivity.EXTRA_QUESTION_TIMEOUT, maxCorrect);
        intent.putExtra(TestActivity.EXTRA_CATEGORIES, mSelectedCategories);

        startActivity(intent);
    }

    /** Called when the user taps select categories button */
    public void SelectCategories(View view) {
        Intent intent = new Intent(this, CategoryPickerActivity.class);
        intent.putExtra(CategoryPickerActivity.EXTRA_SELECTED_CATEGORIES, mSelectedCategories);

        startActivityForResult(intent, SELECT_CATEGORY_REQUEST);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store preferences for next time
        storePreferences();
    }

    protected float getTimeLimit() throws NumberFormatException {
        float maxCorrect = Float.parseFloat(mViewCache.timeInput.getText().toString());
        if (maxCorrect <= 0) { throw new NumberFormatException(); }
        return maxCorrect;
    }
    protected void setTimeLimit(float timeLimit) {
        mViewCache.timeInput.setText(String.valueOf(timeLimit));
    }
    protected Set<String> getCategorySet() {
        return new HashSet<String>(Arrays.asList(mSelectedCategories));
    }
    protected void setSelectedCategories(Set<String> selectedCategories) {
        setSelectedCategories(selectedCategories.toArray(new String[selectedCategories.size()]));
    }
    protected void setSelectedCategories(String[] selectedCategories) {
        mSelectedCategories = selectedCategories;
        mViewCache.selectCatButton.setText(getString(R.string.select_categories_button_text) + " (" + String.valueOf(selectedCategories.length) + ")");

    }
    protected boolean getIsReverse() {
        return mViewCache.reverseDirection.isChecked();
    }
    protected void setIsReverse(boolean isReverse) {
        mViewCache.reverseDirection.setChecked(isReverse);
    }

    protected void loadPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        setTimeLimit(sharedPref.getFloat(getString(R.string.saved_time_limit), DEFAULT_TIME_LIMIT_SECONDS));
        setSelectedCategories(sharedPref.getStringSet(getString(R.string.saved_selected_categories),new HashSet<String>()));
        setIsReverse(sharedPref.getBoolean(getString(R.string.saved_reverse_direction), DEFAULT_IS_REVERSE));
    }

    protected void storePreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        float timeLimit;
        try {
            timeLimit = getTimeLimit();
        } catch (NumberFormatException e) {
            timeLimit = sharedPref.getFloat(getString(R.string.saved_time_limit), DEFAULT_TIME_LIMIT_SECONDS);
            // Change text back to something sensible
            setTimeLimit(timeLimit);
        }

        editor.putFloat(getString(R.string.saved_time_limit), timeLimit);
        editor.putStringSet(getString(R.string.saved_selected_categories), getCategorySet());
        editor.putBoolean(getString(R.string.saved_reverse_direction), getIsReverse());
        editor.apply();
    }
}

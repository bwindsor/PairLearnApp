package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * This activity is for the user to set up their test configuration. It stores the configuration in
 * the android key/value store so that preferences are maintained for next time.
 */
public class SetupTestActivity extends AppCompatActivity {

    static final float DEFAULT_TIME_LIMIT_SECONDS = 2;
    static final boolean DEFAULT_IS_REVERSE = false;
    static final int DEFAULT_MAX_TIMES_CORRECT = 10;

    private static class ViewCache {
        EditText timeInput;
        EditText correctCountInput;
        CheckBox reverseDirection;
        Button selectCatButton;
    }
    private ViewCache mViewCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_test);

        cacheViewItems();
        updateCategorySelectionButton();
        // Load preferences
        loadPreferences();
    }
    private void updateCategorySelectionButton() {
        int selCount = WordsDataSource.getNumCategoriesInTest(this);
        mViewCache.selectCatButton.setText(getString(R.string.select_categories_button_text) +
                " (" + String.valueOf(selCount) + ")");
    }

    private void cacheViewItems() {
        mViewCache = new ViewCache();
        mViewCache.timeInput = (EditText) findViewById(R.id.setup_test_time_input);
        mViewCache.correctCountInput = (EditText) findViewById(R.id.setup_text_correct_count_input);
        mViewCache.reverseDirection = (CheckBox) findViewById(R.id.setup_test_reverse_direction);
        mViewCache.selectCatButton = (Button) findViewById(R.id.setup_test_select_cat_button);
    }

    /** Called when the user taps the go button */
    public void StartTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);

        float timeLimit;
        try {
            timeLimit = getTimeLimit();
        } catch (NumberFormatException e) {
            DialogHelper.ShowOKDialog(this, R.string.dialog_invalid_time_limit_message,
                    R.string.dialog_invalid_time_limit_title);
            return;
        }
        int maxCorrect;
        try {
            maxCorrect = getTimesCorrect();
        } catch (NumberFormatException e) {
            DialogHelper.ShowOKDialog(this, R.string.dialog_invalid_times_correct_message,
                    R.string.dialog_invalid_times_correct_title);
            return;
        }

        intent.putExtra(TestActivity.EXTRA_MAX_CORRECT, maxCorrect);
        intent.putExtra(TestActivity.EXTRA_LEFT_TO_RIGHT, getIsReverse());
        intent.putExtra(TestActivity.EXTRA_QUESTION_TIMEOUT, timeLimit);

        startActivity(intent);
    }

    /** Called when the user taps select categories button */
    public void SelectCategories(View view) {
        Intent intent = new Intent(this, CategoryPickerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCategorySelectionButton();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Store preferences for next time
        storePreferences();
    }

    // Gets the time limit from the UI
    protected float getTimeLimit() throws NumberFormatException {
        float timeLimit = Float.parseFloat(mViewCache.timeInput.getText().toString());
        if (timeLimit <= 0) { throw new NumberFormatException(); }
        return timeLimit;
    }
    // Sets the time limit display in the UI
    protected void setTimeLimit(float timeLimit) {
        mViewCache.timeInput.setText(String.valueOf(timeLimit));
    }
    // Gets whether the reverse direction checkbox is ticked
    protected boolean getIsReverse() {
        return mViewCache.reverseDirection.isChecked();
    }
    // Sets whether the reverse direction checkbox is ticked
    protected void setIsReverse(boolean isReverse) {
        mViewCache.reverseDirection.setChecked(isReverse);
    }
    protected int getTimesCorrect() throws NumberFormatException {
        int maxCorrect = Integer.parseInt(mViewCache.correctCountInput.getText().toString());
        if (maxCorrect < 0) { throw new NumberFormatException(); }
        return maxCorrect;
    }
    protected void setTimesCorrect(int maxCorrect) {
        mViewCache.correctCountInput.setText(String.valueOf(maxCorrect));
    }

    // Loads the users preferences from android's Key/Value store
    protected void loadPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        setTimeLimit(sharedPref.getFloat(getString(R.string.saved_time_limit), DEFAULT_TIME_LIMIT_SECONDS));
        setIsReverse(sharedPref.getBoolean(getString(R.string.saved_reverse_direction), DEFAULT_IS_REVERSE));
        setTimesCorrect(sharedPref.getInt(getString(R.string.saved_max_times_correct), DEFAULT_MAX_TIMES_CORRECT));
    }

    // Stores the users preferences from android's Key/Value store
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
        int maxCorrect;
        try {
            maxCorrect = getTimesCorrect();
        } catch (NumberFormatException e) {
            maxCorrect = sharedPref.getInt(getString(R.string.saved_max_times_correct), DEFAULT_MAX_TIMES_CORRECT);
            setTimesCorrect(maxCorrect);
        }

        editor.putFloat(getString(R.string.saved_time_limit), timeLimit);
        editor.putBoolean(getString(R.string.saved_reverse_direction), getIsReverse());
        editor.putInt(getString(R.string.saved_max_times_correct), maxCorrect);
        editor.apply();
    }
}

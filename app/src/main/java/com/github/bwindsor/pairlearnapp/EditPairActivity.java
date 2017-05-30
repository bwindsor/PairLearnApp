package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * This activity is for editing a pair of words. The user can enter/modify each of the words.
 */
public class EditPairActivity extends AppCompatActivity {
    public static final String EXTRA_LEFT_WORD = "leftWord";
    public static final String EXTRA_RIGHT_WORD = "rightWord";
    public static final String EXTRA_WORD_INDEX = "wordIndex";
    public static final String EXTRA_ACTIVITY_TITLE = "activityTitle";

    private TextView mLeftTextView;
    private TextView mRightTextView;
    private int mWordIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pair);

        Intent intent = getIntent();
        String leftWord = intent.getStringExtra(EXTRA_LEFT_WORD);
        String rightWord = intent.getStringExtra(EXTRA_RIGHT_WORD);
        mWordIndex = intent.getIntExtra(EXTRA_WORD_INDEX, 0);

        // Cache handles to the view items
        mLeftTextView = (TextView) findViewById(R.id.edit_pair_left_input);
        mRightTextView = (TextView) findViewById(R.id.edit_pair_right_input);

        // Set initial words as those passed into the activity
        mLeftTextView.setText(leftWord);
        mRightTextView.setText(rightWord);

        // Set title as that passed in
        setTitle(intent.getStringExtra(EXTRA_ACTIVITY_TITLE));
    }

    // Override for the hardware back button being pressed
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    // Override for the menu bar back button being pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Callback for OK button being clicked
    public void onOKClick(View view) {
        Intent data = new Intent();

        String leftText = mLeftTextView.getText().toString();
        String rightText = mRightTextView.getText().toString();

        /* Validate text inputs - this will display a message to the user if anything is invalid,
        but this function must then return afterwards
        */
        if (!ValidateTextInput(leftText)) { return; }
        if (!ValidateTextInput(rightText)) { return; }

        // Validation was successful, so put extras in and return the result
        data.putExtra(EXTRA_LEFT_WORD, leftText);
        data.putExtra(EXTRA_RIGHT_WORD, rightText);
        data.putExtra(EXTRA_WORD_INDEX, mWordIndex);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean ValidateTextInput(String s) {
        // Cannot be empty
        if (s.length() == 0) {
            DialogHelper.ShowOKDialog(this,
                    R.string.dialog_string_empty_message,
                    R.string.dialog_string_empty_title
            );
            return false;
        }
        // Cannot contain a comma
        if (s.contains(",")) {
            DialogHelper.ShowOKDialog(this,
                    R.string.dialog_string_contains_comma_message,
                    R.string.dialog_string_contains_comma_title
            );
            return false;
        }
        return true;
    }
}

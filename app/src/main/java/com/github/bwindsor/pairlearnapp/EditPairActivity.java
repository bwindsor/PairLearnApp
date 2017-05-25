package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

        mLeftTextView = (TextView) findViewById(R.id.edit_pair_left_input);
        mRightTextView = (TextView) findViewById(R.id.edit_pair_right_input);

        mLeftTextView.setText(leftWord);
        mRightTextView.setText(rightWord);

        setTitle(intent.getStringExtra(EXTRA_ACTIVITY_TITLE));
    }

    public void onOKClick(View view) {
        Intent data = new Intent();

        String leftText = mLeftTextView.getText().toString();
        String rightText = mRightTextView.getText().toString();

        ValidateTextInput(leftText);
        ValidateTextInput(rightText);

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

    private void ValidateTextInput(String s) {
        // Cannot be empty
        if (s.length() == 0) {
            DialogHelper.ShowOKDialog(this,
                    R.string.dialog_string_empty_message,
                    R.string.dialog_string_empty_title
            );
        }
        // Cannot contain a comma
        if (s.contains(",")) {
            DialogHelper.ShowOKDialog(this,
                    R.string.dialog_string_contains_comma_message,
                    R.string.dialog_string_contains_comma_title
            );
        }
    }
}

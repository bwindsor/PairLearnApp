package com.github.bwindsor.pairlearnapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

public class TestActivity extends AppCompatActivity implements QuestionFragment.OnQuestionFinishedListener, AnswerFragment.OnAnswerButtonPressedListener {

    public static final String EXTRA_QUESTION_TIMEOUT = "questionTimeout";
    public static final String EXTRA_MAX_CORRECT = "maxCorrect";
    public static final String EXTRA_LEFT_TO_RIGHT = "leftToRight";

    private TestDataSource mTestDataSource;
    private float mTimeLimitSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        TestConfig config = new TestConfig(
                intent.getIntExtra(EXTRA_MAX_CORRECT, 10),
                intent.getBooleanExtra(EXTRA_LEFT_TO_RIGHT, false)
        );

        // Create the data source
        mTimeLimitSeconds = intent.getFloatExtra(EXTRA_QUESTION_TIMEOUT, 3);

        mTestDataSource = new TestDataSource();
        mTestDataSource.init(config, getApplicationContext());
        Pair<String, String> p = mTestDataSource.getNextPair();
        if (p == null)
        {
            showNoWordsDialogThenFinish();
            return;
        }

        // Create a new Fragment to be placed in the activity layout
        QuestionFragment firstFragment = QuestionFragment.newInstance(p.first,this.mTimeLimitSeconds);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.test_fragment_container, firstFragment).commit();
    }

    private void switchToAnswer() {
        Pair<String, String> p = mTestDataSource.getCurrentPair();
        AnswerFragment newFragment = AnswerFragment.newInstance(p.second);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.test_fragment_container, newFragment);
        transaction.commit();
    }

    private void switchToNextQuestion() {
        Pair<String, String> p = mTestDataSource.getNextPair();
        if (p == null) {
            this.onTestFinished();
            return;
        }
        QuestionFragment newFragment = QuestionFragment.newInstance(p.first, this.mTimeLimitSeconds);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.test_fragment_container, newFragment);
        transaction.commit();
    }

    public void onTestFinished() {
        // End this activity (goes back to previous in the back stack)
        finish();
    }

    public void onQuestionFinished() {
        this.switchToAnswer();
    }
    public void onContinueClicked(View view) { this.switchToAnswer(); }

    public void onCorrectButtonPressed(View view) {
        this.switchToNextQuestion();
    }
    public void onWrongButtonPressed(View view) {
        this.switchToNextQuestion();
    }

    private void showNoWordsDialogThenFinish() {
        DialogHelper.ShowOKDialog(this,
                R.string.dialog_no_words_selected_message,
                R.string.dialog_no_words_selected_title,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

}

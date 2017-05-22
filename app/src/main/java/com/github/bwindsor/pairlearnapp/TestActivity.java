package com.github.bwindsor.pairlearnapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

public class TestActivity extends AppCompatActivity implements QuestionFragment.OnQuestionFinishedListener, AnswerFragment.OnAnswerButtonPressedListener {

    private TestDataSource mTestDataSource;
    private long mTimeLimitSeconds = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Create the data source
        mTestDataSource = new TestDataSource();
        TestConfig config = null;
        mTestDataSource.init(config);
        Pair<String, String> p = mTestDataSource.getNextPair();

        // Create a new Fragment to be placed in the activity layout
        QuestionFragment firstFragment = QuestionFragment.newInstance(p.first,this.mTimeLimitSeconds);

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.test_fragment_container, firstFragment).commit();
    }

    public void switchToAnswer() {
        Pair<String, String> p = mTestDataSource.getCurrentPair();
        AnswerFragment newFragment = AnswerFragment.newInstance(p.second);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.test_fragment_container, newFragment);
        transaction.commit();
    }

    public void switchToNextQuestion() {
        Pair<String, String> p = mTestDataSource.getNextPair();
        QuestionFragment newFragment = QuestionFragment.newInstance(p.first, this.mTimeLimitSeconds);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.test_fragment_container, newFragment);
        transaction.commit();
    }

    public void onQuestionFinished() {
        this.switchToAnswer();
    }

    public void onCorrectButtonPressed(View view) {
        this.switchToNextQuestion();
    }
    public void onWrongButtonPressed(View view) {
        this.switchToNextQuestion();
    }
/*
    public void onAnswerButtonPressed(AnswerFragment.AnswerButton answerButton) {
        switch (answerButton) {
            case ANSWER_CORRECT:
                // Do stuff
                break;
            case ANSWER_WRONG:
                // Do stuff
                break;
            default:
                throw new UnsupportedOperationException();
        }
        this.switchToNextQuestion();
    }
    */
}

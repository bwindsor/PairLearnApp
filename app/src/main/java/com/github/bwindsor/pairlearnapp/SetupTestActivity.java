package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SetupTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_test);
    }

    /** Called when the user taps the go button */
    public void StartTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }
}

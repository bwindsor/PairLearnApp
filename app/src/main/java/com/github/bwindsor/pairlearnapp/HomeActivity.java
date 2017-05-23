package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        WordsDataSource.init(getApplicationContext());
    }

    /** Called when the user taps the test me button */
    public void StartTestSetup(View view) {
        Intent intent = new Intent(this, SetupTestActivity.class);
        startActivity(intent);
    }
}

package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SetupTestActivity extends AppCompatActivity {

    static final int SELECT_CATEGORY_REQUEST = 1;

    private String[] mSelectedCategories = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_test);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                mSelectedCategories = data.getStringArrayExtra(CategoryPickerActivity.EXTRA_SELECTED_CATEGORIES);
            }
        }
    }

    /** Called when the user taps the go button */
    public void StartTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);

        // TODO - implement UI for setting these things
        intent.putExtra(TestActivity.EXTRA_QUESTION_TIMEOUT, 2);
        intent.putExtra(TestActivity.EXTRA_MAX_CORRECT, 10);
        intent.putExtra(TestActivity.EXTRA_LEFT_TO_RIGHT, true);
        intent.putExtra(TestActivity.EXTRA_CATEGORIES, mSelectedCategories);

        startActivity(intent);
    }

    /** Called when the user taps select categories button */
    public void SelectCategories(View view) {
        Intent intent = new Intent(this, CategoryPickerActivity.class);

        startActivityForResult(intent, SELECT_CATEGORY_REQUEST);
    }
}

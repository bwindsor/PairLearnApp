package com.github.bwindsor.pairlearnapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "categoryName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
    }
}

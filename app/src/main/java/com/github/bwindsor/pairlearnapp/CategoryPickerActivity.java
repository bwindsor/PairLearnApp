package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryPickerActivity extends AppCompatActivity {

    private CategoryPickerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_picker);

        Intent intent = getIntent();

        ListView lv = (ListView) findViewById(R.id.cat_select_list);
        Cursor c = WordsDataSource.getCategories(getApplicationContext());
        mAdapter = new CategoryPickerAdapter(this, c, 0);
        lv.setAdapter(mAdapter);

        lv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }



    public void onDoneClick(View view) {
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }
}

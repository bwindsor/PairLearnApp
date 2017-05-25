package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryPickerActivity extends AppCompatActivity {
    public final static String EXTRA_SELECTED_CATEGORIES = "selectedCategories";

    private List<String> mCategoryStrings;
    private CategoryPickerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_picker);

        Intent intent = getIntent();
        List<String> selectedCategories = Arrays.asList(intent.getStringArrayExtra(EXTRA_SELECTED_CATEGORIES));

        ListView lv = (ListView) findViewById(R.id.cat_select_list);
        mCategoryStrings = WordsDataSource.getDataSource().getUniqueCategories();
        mAdapter = new CategoryPickerAdapter(mCategoryStrings, selectedCategories, this);
        lv.setAdapter(mAdapter);

        lv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }



    public void onDoneClick(View view) {
        List<String> categories = new ArrayList<String>();
        for (int i = 0; i < mCategoryStrings.size(); i++) {
            if (mAdapter.getCheckedStatus(i)) {
                categories.add(mCategoryStrings.get(i));
            }
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_SELECTED_CATEGORIES, categories.toArray(new String[categories.size()]));
        setResult(RESULT_OK, data);
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }
}

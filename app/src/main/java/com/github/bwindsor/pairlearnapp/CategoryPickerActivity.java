package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryPickerActivity extends AppCompatActivity {
    public final static String EXTRA_SELECTED_CATEGORIES = "selectedCategories";

    private List<String> mCategoryStrings;
    private boolean[] mSelectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_picker);

        ListView lv = (ListView) findViewById(R.id.cat_select_list);
        mCategoryStrings = WordsDataSource.getDataSource().getUniqueCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.tick_list_item, R.id.tick_list_check, mCategoryStrings);
        lv.setAdapter(adapter);

        mSelectionStatus = new boolean[100];
        Arrays.fill(mSelectionStatus, Boolean.FALSE);

        lv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Do something in response to the click
                CheckBox chk = (CheckBox) v.findViewById(R.id.tick_list_check);
                chk.setChecked(!chk.isChecked());
                mSelectionStatus[position] = chk.isChecked();
            }
        };

        lv.setOnItemClickListener(listClickedHandler);

        // TODO - set initial states, but probably need custom adapter for this?
        /*
        // Set initial states
        Intent intent = getIntent();
        String[] selectedCategories = intent.getStringArrayExtra(EXTRA_SELECTED_CATEGORIES);
        for (int i = 0; i < mCategoryStrings.size(); i++) {
            for (int j = 0; j < selectedCategories.length; j++) {
                if (mCategoryStrings.get(i).compareToIgnoreCase(selectedCategories[j]) == 0) {
                    mSelectionStatus[i] = true;

                    break;
                }
            }
        }
        */
    }



    public void onDoneClick(View view) {
        List<String> categories = new ArrayList<String>();
        for (int i = 0; i < mCategoryStrings.size(); i++) {
            if (mSelectionStatus[i]) {
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

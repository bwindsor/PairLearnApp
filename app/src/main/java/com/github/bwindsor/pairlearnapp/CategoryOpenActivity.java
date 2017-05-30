package com.github.bwindsor.pairlearnapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This activity allows the user to view existing categories and add new ones
 */
public class CategoryOpenActivity extends AppCompatActivity {
    private List<String> mCategoryNames;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_open);

        // Set up a simple list view with a list of categories
        ListView lv = (ListView) findViewById(R.id.cat_open_list);
        mCategoryNames = WordsDataSource.getDataSource().getUniqueCategories();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCategoryNames);
        lv.setAdapter(mAdapter);

        final CategoryOpenActivity this_ = this;
        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // In response to the click, start the edit category activity, passing the category
                // name to be edited.
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                Intent intent = new Intent(this_, EditCategoryActivity.class);
                intent.putExtra(EditCategoryActivity.EXTRA_CATEGORY_NAME, textView.getText());
                startActivity(intent);
            }
        };

        lv.setOnItemClickListener(listClickedHandler);
    }

    /**
     * This is the callback for when the add category button is clicked
     * @param view
     */
    public void onAddCategoryClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        // Show a dialog asking the user for the name of the new category
        builder.setTitle(R.string.dialog_add_category_title)
                .setView(input)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update the data we are working on
                        String text = input.getText().toString();
                        if (text.length() > 0) {
                            mCategoryNames.add(text);
                            WordsDataSource w = WordsDataSource.getDataSource();

                            w.setInterleavedWordListForCategory(text, new ArrayList<String>(Arrays.asList(
                                    getString(R.string.default_left_word),
                                    getString(R.string.default_right_word)
                            )));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.show();
    }
}

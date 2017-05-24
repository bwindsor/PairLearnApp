package com.github.bwindsor.pairlearnapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "categoryName";
    private String mCategoryName;
    private List<String> mInterleavedWords;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Intent intent = getIntent();
        mCategoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);

        // Display the category name in the activity title
        this.setTitle(getResources().getString(R.string.edit_category_title) + ": " + mCategoryName);

        GridView lv = (GridView) findViewById(R.id.cat_edit_grid);
        mInterleavedWords = WordsDataSource.getDataSource().getInterleavedWordListInCategory(mCategoryName);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mInterleavedWords);
        lv.setAdapter(mAdapter);

        final EditCategoryActivity this_ = this;
        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // In response to the click, open a dialog asking the user to edit the word
                AlertDialog.Builder builder = new AlertDialog.Builder(this_);
                final EditText input = new EditText(this_);
                final int idx = position;

                input.setText(mInterleavedWords.get(position));

                builder.setTitle(R.string.dialog_edit_word_title)
                        .setView(input)
                       .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Update the data we are working on
                                mInterleavedWords.set(idx, input.getText().toString());
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        };

        lv.setOnItemClickListener(listClickedHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save data to CSV
        WordsDataSource w = WordsDataSource.getDataSource();
        w.setInterleavedWordListForCategory(mCategoryName, mInterleavedWords);

        // Save data asynchronously to avoid blocking this thread
        w.saveAsync();
    }

}

package com.github.bwindsor.pairlearnapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This activity allows the user to view existing categories and add new ones
 */
public class CategoryOpenActivity extends AppCompatActivity {
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_open);

        new LoadCategoriesTask().execute();
    }


    private class LoadCategoriesTask extends AsyncTask<Void, Void, Integer> {
        private ListView mListView;

        protected Integer doInBackground(Void... x) {
            // Set up a simple list view with a list of categories
            mListView = (ListView) findViewById(R.id.cat_open_list);
            Cursor c = WordsDataSource.getCategories(getApplicationContext());
            mAdapter = new CategoryOpenAdapter(CategoryOpenActivity.this, c, 0);
            return 0;
        }

        protected void onPostExecute(Integer result) {
            mListView.setAdapter(mAdapter);
        }
    }

    /**
     * This is the callback for when the add category button is clicked
     * @param view
     */
    public void onAddCategoryClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        // This makes the keyboard appear lower case
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        // Show a dialog asking the user for the name of the new category
        builder.setTitle(R.string.dialog_add_category_title)
                .setView(input)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update the data we are working on
                        String text = input.getText().toString();
                        if (text.length() > 0) {
                            WordsDataSource.addCategory(getApplicationContext(), text);
                            mAdapter.swapCursor(WordsDataSource.getCategories(getApplicationContext()));
                            // mAdapter.notifyDataSetChanged();
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

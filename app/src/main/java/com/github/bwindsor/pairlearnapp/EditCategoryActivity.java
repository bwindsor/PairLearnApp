package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.util.ArrayList;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String EXTRA_CATEGORY_ID = "categoryId";
    public static final int EDIT_PAIR_REQUEST_CODE = 1;
    public static final int ADD_PAIR_REQUEST_CODE = 2;
    private int mCategoryId;
    private Cursor mCursor;
    private CursorAdapter mAdapter;
    private ListView mListView;

    private void refreshCursor() {
        mCursor = WordsDataSource.getPairs(this, mCategoryId);
        if (mAdapter != null) {
            mAdapter.changeCursor(mCursor);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class LoadPairsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer doInBackground(Void... x) {
            // Display the category name in the activity title
            Cursor c0 = WordsDataSource.getCategory(EditCategoryActivity.this, mCategoryId);
            c0.moveToFirst();
            EditCategoryActivity.this.setTitle(getResources().getString(R.string.edit_category_title) + ": " + c0.getString(c0.getColumnIndex(WordsContract.Categories.NAME)));

            refreshCursor();
            mAdapter = new EditCategoryAdapter(EditCategoryActivity.this, mCursor, 0);
            return 0;
        }

        protected void onPostExecute(Integer result) {
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Intent intent = getIntent();
        mCategoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, 0);

        mListView = (ListView) findViewById(R.id.cat_edit_list);

        new LoadPairsTask().execute();

        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private List<Boolean> mIsSelected;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                mIsSelected.set(position, checked);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.edit_words_menu_delete:
                        deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.edit_words_context_menu, menu);
                mIsSelected = new ArrayList<>();
                int count = mCursor.getCount();
                for (int i = 0; i < count; i++) {
                    mIsSelected.add(false);
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }

            private void deleteSelectedItems() {
                List<Integer> pairIdsToRemove = new ArrayList<>();
                for (int i = 0; i < mIsSelected.size(); i++) {
                    if (mIsSelected.get(i)) {
                        mCursor.moveToPosition(i);
                        pairIdsToRemove.add(mCursor.getInt(mCursor.getColumnIndex(WordsContract.Pairs.WORD_PAIR_ID)));
                    }
                }
                WordsDataSource.removePairs(EditCategoryActivity.this, pairIdsToRemove);
                refreshCursor();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        // In response to the click, start edit word activity
        mCursor.moveToPosition(position);
        Intent intent = new Intent(this, EditPairActivity.class);
        intent.putExtra(EditPairActivity.EXTRA_LEFT_WORD, mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD1)));
        intent.putExtra(EditPairActivity.EXTRA_RIGHT_WORD, mCursor.getString(mCursor.getColumnIndex(WordsContract.Pairs.WORD2)));
        intent.putExtra(EditPairActivity.EXTRA_WORD_INDEX, mCursor.getInt(mCursor.getColumnIndex(WordsContract.Pairs.WORD_PAIR_ID)));
        intent.putExtra(EditPairActivity.EXTRA_ACTIVITY_TITLE, getResources().getString(R.string.edit_word_pair_title));
        startActivityForResult(intent, EditCategoryActivity.EDIT_PAIR_REQUEST_CODE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, EditPairActivity.class);
        intent.putExtra(EditPairActivity.EXTRA_LEFT_WORD, getResources().getString(R.string.default_left_word));
        intent.putExtra(EditPairActivity.EXTRA_RIGHT_WORD, getResources().getString(R.string.default_right_word));
        intent.putExtra(EditPairActivity.EXTRA_WORD_INDEX, mCursor.getCount());
        intent.putExtra(EditPairActivity.EXTRA_ACTIVITY_TITLE, getResources().getString(R.string.add_word_pair_title));

        startActivityForResult(intent, ADD_PAIR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_PAIR_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String leftWord = data.getStringExtra(EditPairActivity.EXTRA_LEFT_WORD);
                    String rightWord = data.getStringExtra(EditPairActivity.EXTRA_RIGHT_WORD);
                    int index = data.getIntExtra(EditPairActivity.EXTRA_WORD_INDEX, 0);

                    WordsDataSource.updatePair(this, index, leftWord, rightWord);
                    refreshCursor();
                }
                break;
            case ADD_PAIR_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String leftWord = data.getStringExtra(EditPairActivity.EXTRA_LEFT_WORD);
                    String rightWord = data.getStringExtra(EditPairActivity.EXTRA_RIGHT_WORD);

                    WordsDataSource.addPair(this, leftWord, rightWord, mCategoryId);
                    refreshCursor();
                }
            default:
                break;
        }
    }
}

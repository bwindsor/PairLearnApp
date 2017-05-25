package com.github.bwindsor.pairlearnapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "categoryName";
    public static final int EDIT_PAIR_REQUEST_CODE = 1;
    public static final int ADD_PAIR_REQUEST_CODE = 2;
    private String mCategoryName;
    private List<String> mInterleavedWords;
    private ArrayAdapter<String> mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Intent intent = getIntent();
        mCategoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);

        // Display the category name in the activity title
        this.setTitle(getResources().getString(R.string.edit_category_title) + ": " + mCategoryName);

        mGridView = (GridView) findViewById(R.id.cat_edit_grid);
        mInterleavedWords = WordsDataSource.getDataSource().getInterleavedWordListInCategory(mCategoryName);
        mAdapter = new ArrayAdapter<String>(this, R.layout.grid_list_item, R.id.grid_list_text, mInterleavedWords);
        mGridView.setAdapter(mAdapter);

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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
                for (int i = 0; i < mInterleavedWords.size(); i++) {
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
                for (int i = mIsSelected.size()-1; i >= 0; i-=2) {
                    if (mIsSelected.get(i-1) || mIsSelected.get(i)) {
                        mInterleavedWords.remove(i);
                        mInterleavedWords.remove(i-1);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });


        // Set up what happens when an item is clicked - an edit popup appears where the user can
        // edit the text.
        final EditCategoryActivity this_ = this;
        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // In response to the click, start edit word activity
                Intent intent = new Intent(this_, EditPairActivity.class);
                intent.putExtra(EditPairActivity.EXTRA_LEFT_WORD, mInterleavedWords.get(position - position % 2));
                intent.putExtra(EditPairActivity.EXTRA_RIGHT_WORD, mInterleavedWords.get(position - position % 2 + 1));
                intent.putExtra(EditPairActivity.EXTRA_WORD_INDEX, position - position % 2);
                intent.putExtra(EditPairActivity.EXTRA_ACTIVITY_TITLE, getResources().getString(R.string.edit_word_pair_title));

                startActivityForResult(intent, EDIT_PAIR_REQUEST_CODE);

            }
        };

        mGridView.setOnItemClickListener(listClickedHandler);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save data to CSV
        WordsDataSource w = WordsDataSource.getDataSource();
        w.setInterleavedWordListForCategory(mCategoryName, mInterleavedWords);

        // Save data asynchronously to avoid blocking this thread
        WordsDataSource.saveAsync();
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, EditPairActivity.class);
        intent.putExtra(EditPairActivity.EXTRA_LEFT_WORD, getResources().getString(R.string.default_left_word));
        intent.putExtra(EditPairActivity.EXTRA_RIGHT_WORD, getResources().getString(R.string.default_right_word));
        intent.putExtra(EditPairActivity.EXTRA_WORD_INDEX, mInterleavedWords.size());
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
                    mInterleavedWords.set(index, leftWord);
                    mInterleavedWords.set(index+1, rightWord);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case ADD_PAIR_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mInterleavedWords.add(data.getStringExtra(EditPairActivity.EXTRA_LEFT_WORD));
                    mInterleavedWords.add(data.getStringExtra(EditPairActivity.EXTRA_RIGHT_WORD));
                    mAdapter.notifyDataSetChanged();
                }
            default:
                break;
        }
    }
}

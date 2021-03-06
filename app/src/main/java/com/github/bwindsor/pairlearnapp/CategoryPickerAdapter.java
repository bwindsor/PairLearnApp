package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.util.List;

/**
 * Created by Ben on 25/05/2017.
 */

public class CategoryPickerAdapter extends CursorAdapter implements View.OnClickListener{

    //private List<String> mCatNames;
    private Context mContext;
    private LayoutInflater mCursorInflater;

    public CategoryPickerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
        mCursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onClick(View v) {
        int categoryId = (int)v.getTag();
        WordsDataSource.setCategoryIsInTest(mContext, categoryId, ((CheckBox)v).isChecked());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return mCursorInflater.inflate(R.layout.tick_list_item, parent, false);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        CheckBox chkBox = (CheckBox) v.findViewById(R.id.tick_list_check);
        if (chkBox != null) {
            chkBox.setText(cursor.getString(cursor.getColumnIndex(WordsContract.Categories.NAME)));
            chkBox.setOnClickListener(this);
            chkBox.setChecked(cursor.getInt(cursor.getColumnIndex(WordsContract.Categories.IS_IN_TEST))!=0);
            chkBox.setTag(cursor.getInt(cursor.getColumnIndex(WordsContract.Categories.CATEGORY_ID)));
        }
    }
}

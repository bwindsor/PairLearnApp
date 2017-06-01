package com.github.bwindsor.pairlearnapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.bwindsor.pairlearnapp.providers.WordsContract;

import java.util.List;

/**
 * Created by Ben on 25/05/2017.
 */

public class EditCategoryAdapter extends CursorAdapter {

    //private List<String> mCatNames;
    private Activity mContext;
    private LayoutInflater mCursorInflater;

    public EditCategoryAdapter(Activity context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
        mCursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return mCursorInflater.inflate(R.layout.edit_category_list_item, parent, false);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        TextView textView1 = (TextView) v.findViewById(R.id.edit_cat_left_list);
        if (textView1 != null) {
            textView1.setText(cursor.getString(cursor.getColumnIndex(WordsContract.Pairs.WORD1)));
            textView1.setTag(cursor.getPosition());
        }
        TextView textView2 = (TextView) v.findViewById(R.id.edit_cat_right_list);
        if (textView2 != null) {
            textView2.setText(cursor.getString(cursor.getColumnIndex(WordsContract.Pairs.WORD2)));
            textView2.setTag(cursor.getPosition());
        }
    }
}

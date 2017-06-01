package com.github.bwindsor.pairlearnapp;

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

public class CategoryOpenAdapter extends CursorAdapter implements View.OnClickListener{

    //private List<String> mCatNames;
    private Context mContext;
    private LayoutInflater mCursorInflater;

    public CategoryOpenAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
        mCursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) v.findViewById(android.R.id.text1);
        Intent intent = new Intent(mContext, EditCategoryActivity.class);
        intent.putExtra(EditCategoryActivity.EXTRA_CATEGORY_ID, (int)textView.getTag());
        mContext.startActivity(intent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return mCursorInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        TextView textView = (TextView) v.findViewById(android.R.id.text1);
        if (textView != null) {
            textView.setText(cursor.getString(cursor.getColumnIndex(WordsContract.Categories.NAME)));
            textView.setTag(cursor.getInt(cursor.getColumnIndex(WordsContract.Categories.CATEGORY_ID)));
            textView.setOnClickListener(this);
        }
    }
}

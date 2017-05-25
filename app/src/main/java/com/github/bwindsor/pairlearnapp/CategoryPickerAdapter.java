package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by Ben on 25/05/2017.
 */

public class CategoryPickerAdapter extends ArrayAdapter<String> implements View.OnClickListener{

    private List<String> mCatNames;
    private Boolean[] mIsChecked;
    Context mContext;

    public CategoryPickerAdapter(List<String> data, List<String> initialSelection, Context context) {
        super(context, R.layout.tick_list_item, R.id.tick_list_check, data);
        this.mCatNames = data;
        this.mContext = context;
        this.mIsChecked = new Boolean[mCatNames.size()];
        for (int i = 0; i < mCatNames.size(); i++) {
            mIsChecked[i] = false;
            for (int j = 0; j < initialSelection.size(); j++) {
                if (mCatNames.get(i).compareToIgnoreCase(initialSelection.get(j)) == 0) {
                    mIsChecked[i] = true;
                    break;
                }
            }
        }
    }

    public boolean getCheckedStatus(int position) {
        return mIsChecked[position];
    }

    @Override
    public void onClick(View v) {

        int position = (int)v.getTag();
        mIsChecked[position] = ((CheckBox)v).isChecked();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.tick_list_item, null);
        }

        String catName = getItem(position);

        if (catName != null) {
            CheckBox chkBox = (CheckBox) v.findViewById(R.id.tick_list_check);
            if (chkBox != null) {
                chkBox.setText(catName);
                chkBox.setOnClickListener(this);
                chkBox.setTag(position);
                chkBox.setChecked(mIsChecked[position]);
            }
        }

        return v;
    }
}

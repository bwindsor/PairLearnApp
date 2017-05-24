package com.github.bwindsor.pairlearnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CategoryOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_open);

        ListView lv = (ListView) findViewById(R.id.cat_open_list);
        List<String> categoryStrings = WordsDataSource.getDataSource().getUniqueCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryStrings);
        lv.setAdapter(adapter);

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
}

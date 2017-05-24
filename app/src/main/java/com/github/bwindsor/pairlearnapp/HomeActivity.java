package com.github.bwindsor.pairlearnapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 8;
    private static final String IMPORT_MIME_TYPE = "*/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Initialise singleton data source with the application context -
        // this will last for the lifetime of the application
        try {
            WordsDataSource.init(getApplicationContext());
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_initial_data_read_failed_message)
                    .setTitle(R.string.dialog_initial_data_read_failed_title)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /** Called when the user taps the test me button */
    public void StartTestSetup(View view) {
        Intent intent = new Intent(this, SetupTestActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the edit vocabulary button */
    public void onEditVocabClick(View view) {
        Intent intent = new Intent(this, CategoryOpenActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the import CSV button */
    public void onImportCsvClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(IMPORT_MIME_TYPE);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /** Process the result of the import CSV button */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = resultData.getData();
            if (uri != null) {
                WordsDataSource w = WordsDataSource.getDataSource();
                try {
                    w.importCsvFromUri(uri);
                    WordsDataSource.save();
                } catch (IOException e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.dialog_import_failed_message)
                            .setTitle(R.string.dialog_import_failed_title)
                            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    }
}

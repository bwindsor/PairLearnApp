package com.github.bwindsor.pairlearnapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.UserDictionary;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 8;
    private static final int WRITE_CSV_REQUEST_CODE = 9;
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

    /** Called when the user taps export CSV button */
    public void onExportCsvClick(View view) {
        // Intent intent = new Intent(Intent.)
        final Intent chooserIntent = new Intent(
                this,
                DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(
                DirectoryChooserActivity.EXTRA_CONFIG,
                config);

        startActivityForResult(chooserIntent, WRITE_CSV_REQUEST_CODE);
    }

    /** Process the result of the import CSV button */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        switch (requestCode) {
            case READ_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
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
                break;
            case WRITE_CSV_REQUEST_CODE:
                if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                    String dirName = resultData.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                    File file = new File(dirName, "words_export.csv");
                    try {
                        WordsDataSource.save(file);
                    } catch (IOException e) {
                        DialogHelper.ShowOKDialog(this, R.string.dialog_save_failed_message, R.string.dialog_save_failed_title);
                    }
                }
                break;
            default:
                break;
        }
    }
}

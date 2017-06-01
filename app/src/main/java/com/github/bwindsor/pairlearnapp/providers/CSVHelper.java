package com.github.bwindsor.pairlearnapp.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.github.bwindsor.pairlearnapp.WordsDataSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 31/05/2017.
 */

public class CSVHelper {
    private static final String CSV_ENCODING = "UTF-8";
    private static final String CSV_COL_SEP = ",";
    private static final int CSV_EXPECTED_COLS = 3;

    public static void exportCsvToUri(Uri uri, Context context) throws IOException {
        ContentResolver cr = context.getContentResolver();
        String[] projection = {WordsContract.Pairs.WORD1, WordsContract.Pairs.WORD2, WordsContract.Categories.NAME};
        Cursor c = cr.query(WordsContract.PairCategory.CONTENT_URI, projection, null, null, null);
        c.moveToNext();
        List<List<String>> data = new ArrayList<>();
        while (!c.isAfterLast()) {
            List<String> row = new ArrayList<>();
            row.add(c.getString(0));
            row.add(c.getString(1));
            row.add(c.getString(2));
            data.add(row);
            c.moveToNext();
        }
        c.close();
        writeCsv(uri, data);
    }

    public static void importCsvFromUri(Uri uri, Context context) throws IOException {
        ContentResolver cr = context.getContentResolver();
        List<List<String>> data = CSVHelper.readCsv(context, uri);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).size() != CSV_EXPECTED_COLS) {
                throw new IOException();
            }
        }
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);

            WordsDataSource.addCategory(context, row.get(0));
            Cursor catCursor = WordsDataSource.getCategory(context, row.get(0));
            if (!catCursor.moveToNext()) {throw new IOException(); }
            int catId = catCursor.getInt(catCursor.getColumnIndex(WordsContract.Categories.CATEGORY_ID));
            catCursor.close();

            WordsDataSource.addPair(context, row.get(1), row.get(2), catId);
        }
/*
        INSERT INTO memos(id,text)
        SELECT 5, 'text to insert'
        WHERE NOT EXISTS(SELECT 1 FROM memos WHERE id = 5 AND text = 'text to insert');
        */
    }

    public static List<List<String>> readCsv(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        List<List<String>> contents;
        if (inputStream == null) {
            throw new IOException();
        }
        else {
            String[] lines = getAllLinesFromInputStream(inputStream);
            contents = parseCsvLines(lines);
            inputStream.close();
        }
        return contents;
    }

    public static void writeCsv(Uri uri, List<List<String>> data) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(uri.getPath()));
        writeCsvToOutputStream(fos, data);
    }

    // Parses the lines of a CSV file
    private static List<List<String>> parseCsvLines(String[] lines) {
        List<List<String>> ss = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(CSV_COL_SEP);
            List<String> s = new ArrayList<>();
            for (int j = 0; j < parts.length; j++) {
                s.add(parts[j].trim());
            }
            ss.add(s);
        }
        return ss;
    }

    private static String[] getAllLinesFromInputStream(InputStream inputStream) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, CSV_ENCODING));
        String readLine;
        List<String> lines = new ArrayList<>();
        while ((readLine = in.readLine()) != null) {
            lines.add(readLine);
        }
        return lines.toArray(new String[lines.size()]);
    }

    public static void writeCsvToOutputStream(OutputStream os, List<List<String>> data) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, CSV_ENCODING));

        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {
                out.write(row.get(j));
                if (j+1 < row.size()) {
                    out.write(CSV_COL_SEP);
                }
            }
            out.newLine();
        }

        out.close();
    }
}

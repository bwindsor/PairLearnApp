package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ben on 23/05/2017.
 */

public class WordsDataSource {
    private List<String> mCategories = new ArrayList<String>();
    private List<String> mLeftWords = new ArrayList<String>();
    private List<String> mRightWords = new ArrayList<String>();
    private Context mContext;
    private boolean mIsInitialised = false;
    private static final String WORDS_CSV_FILE = "words.csv";
    private static final int CSV_NUM_COLS = 3;
    private static final String CSV_COL_SEP = ",";
    private static final String CSV_WRITE_NEW_LINE = "\n";
    private static final String WORDS_CSV_ENCODING = "UTF-8";

    private int mNumRows() { return mCategories.size(); }
    private static WordsDataSource instance = null;

    private WordsDataSource(){}

    public static WordsDataSource getDataSource() {
        if (WordsDataSource.instance == null) {
            WordsDataSource.instance = new WordsDataSource();
        }
        return WordsDataSource.instance;
    }

    public static void init(Context context) throws IOException {
        WordsDataSource w = WordsDataSource.getDataSource();
        w._init(context);
    }
    private void _init(Context context) throws IOException {
        if (mIsInitialised) {
            return;
        }

        mContext = context;
        boolean openSuccess = true;
        InputStream csvStream;
        try {
            csvStream = mContext.openFileInput(WORDS_CSV_FILE);
        } catch (FileNotFoundException e) {
            AssetManager assetManager = context.getAssets();
            csvStream = assetManager.open(WORDS_CSV_FILE);
        }
        String[] lines = getAllLinesFromInputStream(csvStream);
        csvStream.close();
        parseCsvLines(lines);

        mIsInitialised = true;
    }

    public List<String> getUniqueCategories() {
        ArrayList<String> c = new ArrayList<String>(new HashSet<>(mCategories));
        Collections.sort(c);
        return c;
    }

    public List<String> getCategories() {
        return mCategories;
    }
    public List<String> getLeftWords() {
        return mLeftWords;
    }
    public List<String> getRightWords() {
        return mRightWords;
    }
    public List<Pair<String,String>> getWordPairs() {
        List<Pair<String,String>> p = new ArrayList<Pair<String,String>>();
        for (int i = 0; i < mNumRows(); i++) {
            p.add(new Pair<String,String>(mLeftWords.get(i), mRightWords.get(i)));
        }
        return p;
    }
    public List<Pair<String,String>> getReversedWordPairs() {
        List<Pair<String,String>> p = new ArrayList<Pair<String,String>>();
        for (int i = 0; i < mNumRows(); i++) {
            p.add(new Pair<String,String>(mRightWords.get(i), mLeftWords.get(i)));
        }
        return p;
    }
    public void addPair(String categoryName, Pair<String,String> pair) {
        mCategories.add(categoryName);
        mLeftWords.add(pair.first);
        mRightWords.add(pair.second);
    }
    public void importCsvFromUri(Uri uri) throws IOException {
        InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException();
        }
        else {
            String[] lines = getAllLinesFromInputStream(inputStream);
            parseCsvLines(lines);
            inputStream.close();
        }
    }

    private void parseCsvLines(String[] lines) throws IOException {
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(CSV_COL_SEP);
            if (parts.length == CSV_NUM_COLS) {
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                // categories will always be read as lower case
                String category = parts[0].toLowerCase();
                // Only add row if it is unique
                if (!hasRow(category, parts[1], parts[2])) {
                    mCategories.add(category);
                    mLeftWords.add(parts[1]);
                    mRightWords.add(parts[2]);
                }
            } else if (parts.length != 0) {
                throw new IOException();
            }
        }
    }

    private boolean hasRow(String category, String leftWord, String rightWord) {
        for (int i = 0; i < mNumRows(); i++) {
            if (mCategories.get(i).compareTo(category) == 0 &&
                    mLeftWords.get(i).compareTo(leftWord) == 0 &&
                    mRightWords.get(i).compareTo(rightWord) == 0) {
                return true;
            }
        }
        return false;
    }

    public static void save() throws IOException {
        WordsDataSource w = WordsDataSource.getDataSource();
        w._save();
    }

    private void _save() throws IOException {
        FileOutputStream fos = mContext.openFileOutput(WORDS_CSV_FILE, Context.MODE_PRIVATE);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, WORDS_CSV_ENCODING));
        for (int i = 0; i < mNumRows(); i++) {
            String s = mCategories.get(i) + CSV_COL_SEP + mLeftWords.get(i) + CSV_COL_SEP + mRightWords.get(i);
            out.write(s);
            out.newLine();
        }
        out.close();
    }

    public static void delete() {
        WordsDataSource w = WordsDataSource.getDataSource();
        w._delete();
    }

    private void _delete() {
        mContext.deleteFile(WORDS_CSV_FILE);
    }

    private static String[] getAllLinesFromInputStream(InputStream inputStream) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, WORDS_CSV_ENCODING));
        String readLine;
        List<String> lines = new ArrayList<>();
        while ((readLine = in.readLine()) != null) {
            lines.add(readLine);
        }
        return lines.toArray(new String[lines.size()]);
    }
}

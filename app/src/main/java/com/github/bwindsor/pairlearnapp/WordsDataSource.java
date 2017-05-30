package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Ben on 23/05/2017.
 * This is a singleton class. It needs initialising with the application context when the
 * application first starts running.
 * This provides a layer between the data store on disk and the rest of the program.
 * Data is currently stored in a CSV file but this should be changed to SQLLite at some point.
 */

public class WordsDataSource {
    private List<String> mCategories = new ArrayList<>();
    private List<String> mLeftWords = new ArrayList<>();
    private List<String> mRightWords = new ArrayList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Context mContext;
    private boolean mIsInitialised = false;
    private static final String WORDS_CSV_FILE = "words.csv";
    private static final int CSV_NUM_COLS = 3;
    private static final String CSV_COL_SEP = ",";
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

    /**
     * Gets a list of unique category names
     * @return list of unique category names
     */
    public List<String> getUniqueCategories() {
        ArrayList<String> c;
        readWriteLock.readLock().lock();
        try {
            c = new ArrayList<>(new HashSet<>(mCategories));
        } finally {
            readWriteLock.readLock().unlock();
        }
        Collections.sort(c);
        return c;
    }

    /**
     * Gets a list of strings with interleave left and right words for a requested category
     * @param categoryName the name of the category to get the words for
     * @return list of interleaved words for this category
     */
    public List<String> getInterleavedWordListInCategory(String categoryName) {
        List<String> words = new ArrayList<>();
        readWriteLock.readLock().lock();
        try {
            for (int i = 0; i < mNumRows(); i++) {
                if (mCategories.get(i).compareToIgnoreCase(categoryName) == 0) {
                    words.add(mLeftWords.get(i));
                    words.add(mRightWords.get(i));
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return words;
    }

    /**
     * Sets the set of word pairs for a category from interleaved words
     * @param categoryName the name of the category to set
     * @param interleavedWords a list of interleaved word pairs for this category
     */

    public void setInterleavedWordListForCategory(String categoryName, List<String> interleavedWords) {
        removeCategory(categoryName);
        for (int i = 0; i < interleavedWords.size(); i+=2) {
            addIfUnique(categoryName, interleavedWords.get(i), interleavedWords.get(i+1));
        }
    }

    /**
     * Gets the complete list of categories for every word pair. Use getUniqueCategories to get the
     * set of available categories.
     * @return list of categories for each word pair in the data set
     */
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
        readWriteLock.readLock().lock();
        try {
            for (int i = 0; i < mNumRows(); i++) {
                p.add(new Pair<String, String>(mLeftWords.get(i), mRightWords.get(i)));
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return p;
    }
    public List<Pair<String,String>> getReversedWordPairs() {
        List<Pair<String, String>> p = new ArrayList<>();
        readWriteLock.readLock().lock();
        try {
            for (int i = 0; i < mNumRows(); i++) {
                p.add(new Pair<String, String>(mRightWords.get(i), mLeftWords.get(i)));
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return p;
    }

    /**
     * Imports a CSV file from a specified location
     * @param uri address of the file
     * @throws IOException if it can't open the file or if the file format isn't valid
     */
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

    // Parses the lines of a CSV file
    private void parseCsvLines(String[] lines) throws IOException {
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(CSV_COL_SEP);
            if (parts.length == CSV_NUM_COLS) {
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                // Only add row if it is unique
                addIfUnique(parts[0], parts[1], parts[2]);
            } else if (parts.length != 0) {
                throw new IOException();
            }
        }
    }
    // Removes all word pairs for a given category
    public void removeCategory(String categoryName) {
        readWriteLock.writeLock().lock();
        try {
            // We need to do the loop backwards since the list gets shorter as we remove stuff
            for (int i = mNumRows() - 1; i >= 0; i--) {
                if (mCategories.get(i).compareToIgnoreCase(categoryName) == 0) {
                    mCategories.remove(i);
                    mLeftWords.remove(i);
                    mRightWords.remove(i);
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    // Adds a word pair if it doesn't already exist
    private void addIfUnique(String categoryName, String leftWord, String rightWord) {
        readWriteLock.writeLock().lock();
        try {
            if (!hasRow(categoryName.toLowerCase(), leftWord, rightWord)) {
                mCategories.add(categoryName.toLowerCase());
                mLeftWords.add(leftWord);
                mRightWords.add(rightWord);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    // Checks if a row of data already exists
    private boolean hasRow(String category, String leftWord, String rightWord) {
        boolean b = false;
        readWriteLock.readLock().lock();
        try {
            for (int i = 0; i < mNumRows(); i++) {
                if (mCategories.get(i).compareTo(category) == 0 &&
                        mLeftWords.get(i).compareTo(leftWord) == 0 &&
                        mRightWords.get(i).compareTo(rightWord) == 0) {
                    b = true;
                    break;
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return b;
    }
    // Saves the data to file synchronously
    public static void save() throws IOException {
        WordsDataSource w = WordsDataSource.getDataSource();
        w._save_internal();
    }
    // Saves the data to file asynchronously
    public static void saveAsync() {
        WordsDataSource w = WordsDataSource.getDataSource();
        w._saveAsync();
    }

    /**
     * Saves the data to a user specified CSV file asynchronously
     * @param file file location to save to
     * @throws IOException if the file cannot be saved
     */
    public static void save(File file) throws IOException {
        WordsDataSource w = WordsDataSource.getDataSource();
        w._save_specified(file);
    }

    private void _saveAsync() {
        new SaveDataTask().execute();
    }

    private void _save_internal() throws IOException {
        FileOutputStream fos = mContext.openFileOutput(WORDS_CSV_FILE, Context.MODE_PRIVATE);
        _saveToOutputStream(fos);
    }
    private void _save_specified(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        _saveToOutputStream(fos);
    }

    private void _saveToOutputStream(OutputStream os) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, WORDS_CSV_ENCODING));

        readWriteLock.readLock().lock();
        try {
            for (int i = 0; i < mNumRows(); i++) {
                String s = mCategories.get(i) + CSV_COL_SEP + mLeftWords.get(i) + CSV_COL_SEP + mRightWords.get(i);
                out.write(s);
                out.newLine();
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        out.close();
    }

    private class SaveDataTask extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... voids) {
            try {
                instance._save_internal();
            } catch (IOException e) {
                // TODO - notify user that save failed
                DialogHelper.ShowOKDialog(mContext, R.string.dialog_save_failed_message, R.string.dialog_save_failed_title);
            }
            return 0;
        }
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

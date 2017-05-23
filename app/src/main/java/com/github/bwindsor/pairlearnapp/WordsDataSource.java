package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.UserDictionary;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ben on 23/05/2017.
 */

public class WordsDataSource {
    private List<String> mCategories = new ArrayList<String>();
    private List<String> mLeftWords = new ArrayList<String>();
    private List<String> mRightWords = new ArrayList<String>();

    private static WordsDataSource instance = null;

    private WordsDataSource(){}

    public static WordsDataSource getDataSource() {
        if (WordsDataSource.instance == null) {
            WordsDataSource.instance = new WordsDataSource();
        }
        return WordsDataSource.instance;
    }

    public static void init(Context context) {
        WordsDataSource w = WordsDataSource.getDataSource();

        AssetManager assetManager = context.getAssets();
        try {
            InputStream csvStream = assetManager.open("words.csv");
            String[] lines = getAllLinesFromInputStream(csvStream);
            csvStream.close();

            for (int i = 0; i < lines.length; i++) {
                String[] parts = lines[i].split(",");
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                w.mCategories.add(parts[0]);
                w.mLeftWords.add(parts[1]);
                w.mRightWords.add(parts[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        for (int i = 0; i < mLeftWords.size(); i++) {
            p.add(new Pair<String,String>(mLeftWords.get(i), mRightWords.get(i)));
        }
        return p;
    }

    private static String[] getAllLinesFromInputStream(InputStream inputStream) throws IOException
    {
        String s = "";
        while (inputStream.available() > 0) {
            s += (char) inputStream.read();
        }
        return s.split("\r?\n");
    }
}

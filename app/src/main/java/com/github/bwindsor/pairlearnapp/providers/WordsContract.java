package com.github.bwindsor.pairlearnapp.providers;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.UserDictionary;

/**
 * Created by Ben on 30/05/2017.
 */

public class WordsContract {

    public WordsContract() {
    }

    public static final class PairCategory {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + WordsContentProvider.AUTHORITY + "/"
                + WordsContentProvider.PAIRS_TABLE_NAME
                + WordsContentProvider.CATEGORIES_TABLE_NAME);
    }

    public static final class PairProgress {
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + WordsContentProvider.AUTHORITY + "/"
                + WordsContentProvider.PAIRS_TABLE_NAME
                + WordsContentProvider.PROGRESS_TABLE_NAME);
        public static final String ID_PAIR = "_id_pair";
        public static final String ID_PROGRESS = "_id_progress";
    }

    public static final class Progress implements BaseColumns {
        private Progress() {}

        public static final Uri CONTENT_URI = Uri.parse("content://"
            + WordsContentProvider.AUTHORITY + "/" + WordsContentProvider.PROGRESS_TABLE_NAME);

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bwindsor.progress";

        public static final String PROGRESS_ID = _ID;
        public static final String PAIR_ID = "pair_id";
        public static final String NUM_CORRECT = "num_correct";
        public static final String NUM_WRONG = "num_wrong";
        public static final String UNIX_TIME_LAST_CORRECT = "date_last_correct";
    }

    public static final class Categories implements BaseColumns {
        private Categories() {}

        public static final Uri CONTENT_URI = Uri.parse("content://"
            + WordsContentProvider.AUTHORITY + "/" + WordsContentProvider.CATEGORIES_TABLE_NAME);

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bwindsor.category";

        public static final String CATEGORY_ID = _ID;
        public static final String NAME = "name";
        public static final String IS_IN_TEST = "is_in_test";
    }

    public static final class Pairs implements BaseColumns {
        private Pairs() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + WordsContentProvider.AUTHORITY + "/" + WordsContentProvider.PAIRS_TABLE_NAME);

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bwindsor.pair";

        public static final String WORD_PAIR_ID = _ID;
        public static final String WORD1 = "word1";
        public static final String WORD2 = "word2";
        public static final String CATEGORY_ID = "cat_id";
    }

}
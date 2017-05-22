package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnQuestionFinishedListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {
    private static final String ARG_QUESTION_TEXT = "questionText";
    private static final String ARG_TIME_LIMIT = "timeLimit";

    private String mQuestionText;
    private long mTimeLimitSeconds;

    private OnQuestionFinishedListener mListener;
    private Timer mTimer;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionText Question text to display to the user
     * @param timeLimit Time limit, in seconds, for the question to display
     * @return A new instance of fragment QuestionFragment.
     */
    public static QuestionFragment newInstance(String questionText, long timeLimit) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, questionText);
        args.putLong(ARG_TIME_LIMIT, timeLimit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionText = getArguments().getString(ARG_QUESTION_TEXT);
            mTimeLimitSeconds = getArguments().getLong(ARG_TIME_LIMIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        // Set the display text
        TextView t = (TextView) v.findViewById(R.id.question_textView);
        t.setText(mQuestionText);

        return v;
    }

    public void onTimeUp() {
        if (mListener != null) {
            mListener.onQuestionFinished();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnQuestionFinishedListener) {
            mListener = (OnQuestionFinishedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnQuestionFinishedListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Start the timer - this does mean that if the user pauses and resumes, their time is reset
        // but that's fine because if they're doing this they aren't using it properly anyway
        this.mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                onTimeUp();
            }
        };
        this.mTimer.schedule(task, mTimeLimitSeconds * 1000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnQuestionFinishedListener {
        void onQuestionFinished();
    }
}

package com.github.bwindsor.pairlearnapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAnswerButtonPressedListener} interface
 * to handle interaction events.
 * Use the {@link AnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerFragment extends Fragment {
    private static final String ARG_ANSWER_TEXT = "answerText";

    private String mAnswerText;

    private OnAnswerButtonPressedListener mListener;

    public AnswerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param answerText Parameter 1.
     * @return A new instance of fragment AnswerFragment.
     */
    public static AnswerFragment newInstance(String answerText) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ANSWER_TEXT, answerText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnswerText = getArguments().getString(ARG_ANSWER_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_answer, container, false);

        TextView t = (TextView) v.findViewById(R.id.answer_text);
        t.setText(mAnswerText);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerButtonPressedListener) {
            mListener = (OnAnswerButtonPressedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnQuestionFinishedListener");
        }
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
     */
    public interface OnAnswerButtonPressedListener {
        void onCorrectButtonPressed(View view);
        void onWrongButtonPressed(View view);
    }

}
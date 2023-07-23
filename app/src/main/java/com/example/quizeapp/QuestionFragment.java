package com.example.quizeapp;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class QuestionFragment extends Fragment {

    private TextView questionTextView;

    // Required empty public constructor
    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        questionTextView = (TextView) view.findViewById(R.id.questionTextView);
        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            questionTextView.setText(bundle.getString("questionText"));
            questionTextView.setBackgroundColor(Color.parseColor(bundle.getString("questionBackground")));
        }
    }

}

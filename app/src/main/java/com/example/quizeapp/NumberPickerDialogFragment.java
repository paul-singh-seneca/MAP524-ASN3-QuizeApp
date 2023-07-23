package com.example.quizeapp;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class NumberPickerDialogFragment extends DialogFragment {

    private static final String ARG_CURRENT_QUESTION_COUNT = "current_question_count";
    private OnNumberSelectedListener listener;

    public static NumberPickerDialogFragment newInstance(int currentQuestionCount) {
        NumberPickerDialogFragment fragment = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_QUESTION_COUNT, currentQuestionCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int currentQuestionCount = getArguments().getInt(ARG_CURRENT_QUESTION_COUNT);

        NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(currentQuestionCount);
        numberPicker.setValue(currentQuestionCount);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.selectNumberTitle)
                .setView(numberPicker)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int selectedValue = numberPicker.getValue();
                    listener.onNumberSelected(selectedValue);
                })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    public interface OnNumberSelectedListener {
        void onNumberSelected(int number);
    }

    public void setOnNumberSelectedListener(OnNumberSelectedListener listener) {
        this.listener = listener;
    }
}


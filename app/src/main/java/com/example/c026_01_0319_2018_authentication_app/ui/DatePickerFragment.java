package com.example.c026_01_0319_2018_authentication_app.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final Calendar calendar;
    private final EditText editText;

    public DatePickerFragment(EditText targetEditText) {
        calendar = Calendar.getInstance();
        editText = targetEditText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use current date as the default date in the picker
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new date picker instance
        return new DatePickerDialog(getActivity(), this, currentYear, currentMonth, currentDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Update the calendar instance to match the users selected values
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // Update the edit text
        updateEditText();
    }

    private void updateEditText() {
        // Update the date of the EditText
        editText.setText(getDate());
        // Move cursor to the end of the EditText
        editText.setSelection(editText.getText().length());
    }

    private String getDate() {
        // Formatted date will be like 09/30/2000
        String datePattern = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

        return dateFormat.format(calendar.getTime());
    }
}

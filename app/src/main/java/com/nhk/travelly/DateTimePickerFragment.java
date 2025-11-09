package com.nhk.travelly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DateTimePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public interface OnFragmentInteractionListener {
        void onDateTimeSet(DateTime dateTime, boolean isDeparture);
    }

    OnFragmentInteractionListener mListener;
    private final TextView mTextView;
    private final Context mContext;
    private final DateTime dateTime;
    private final DateTime minDateTime;
    private boolean isDeparture;

    public DateTimePickerFragment(TextView textView, Context context, boolean isDeparture, DateTime dateTime) {
        mTextView = textView;
        mContext = context;
        this.isDeparture = isDeparture;
        this.dateTime = new DateTime(dateTime);
        minDateTime = new DateTime();
        mListener = (OnFragmentInteractionListener) context;
    }

    public DateTimePickerFragment(TextView textView, Context context, boolean isDeparture, DateTime dateTime, DateTime minDateTime) {
        mTextView = textView;
        mContext = context;
        this.isDeparture = isDeparture;
        this.dateTime = new DateTime(dateTime);
        this.minDateTime = minDateTime;
        mListener = (OnFragmentInteractionListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(minDateTime.year, minDateTime.month, minDateTime.day);
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, this, dateTime.year, dateTime.month, dateTime.day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateTime.year = year;
        dateTime.month = month;
        dateTime.day = dayOfMonth;
        mListener.onDateTimeSet(dateTime, isDeparture);
        mTextView.setText(dateTime.toString());
    }
}

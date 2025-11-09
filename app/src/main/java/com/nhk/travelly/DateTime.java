package com.nhk.travelly;

import android.icu.util.Calendar;

import androidx.annotation.NonNull;

public class DateTime {
    public int year;
    public int month;
    public int day;

    public DateTime() {
        final Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public DateTime(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DateTime(@NonNull DateTime other) {
        this.year = other.year;
        this.month = other.month;
        this.day = other.day;
    }

    public boolean lessThan(@NonNull DateTime other) {
        if (this.year < other.year) return true;
        if (this.year > other.year) return false;
        if (this.month < other.month) return true;
        if (this.month > other.month) return false;
        return this.day < other.day;
    }

    DateTime copy() {
        return new DateTime(year, month, day);
    }

    @NonNull
    public String toString() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month] + " " + (day < 10 ? "0" : "") + day + ", " + year;
    }

    public String toDateMonth() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return (day < 10 ? "0" : "") + day + " " + months[month];
    }
}

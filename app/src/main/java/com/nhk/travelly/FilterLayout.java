package com.nhk.travelly;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class FilterLayout {
    BookingActivity mContext;
    int departureTimeSelected = -1;
    int arrivalTimeSelected = -1;
    int minPrice = 0;
    int maxPrice = 300;
    boolean[] facilities = {false, false, false, false};
    int sortSelected = -1;

    public FilterLayout(BookingActivity context) {
        mContext = context;
    }

    public void reset() {
        departureTimeSelected = -1;
        arrivalTimeSelected = -1;
        minPrice = 0;
        maxPrice = 300;
        Arrays.fill(facilities, false);
        sortSelected = -1;
    }

    public void selectArrivalTime(@NonNull View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        arrivalTimeSelected = parent.indexOfChild(view);
        LinearLayout button = (LinearLayout) view;
        mContext.onFocusedButton(button, "Text");
    }

    public void selectFacility(@NonNull View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        int index = parent.indexOfChild(view);
        facilities[index] = !facilities[index];
        LinearLayout button = (LinearLayout) view;
        onFocusedButton(button, facilities[index], "Image");
    }

    private void onFocusedButton(LinearLayout button, boolean isSelected, String id) {
        if (isSelected) {
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.light_green_box));
            if (id.equals("Image")) {
                ImageView imageView = (ImageView) button.getChildAt(0);
                imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
            } else {
                TextView textView = (TextView) button.getChildAt(0);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }
        } else {
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.box));
            if (id.equals("Image")) {
                ImageView imageView = (ImageView) button.getChildAt(0);
                imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.green_text));
            } else {
                TextView textView = (TextView) button.getChildAt(0);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.green_text));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        if (departureTimeSelected != -1) {
            LinearLayout buttonRow = mContext.findViewById(R.id.departure_time_select);
            LinearLayout button = (LinearLayout) buttonRow.getChildAt(departureTimeSelected);
            mContext.onFocusedButton(button, "Text");
        }
        if (arrivalTimeSelected != -1) {
            LinearLayout buttonRow = mContext.findViewById(R.id.arrival_time_select);
            LinearLayout button = (LinearLayout) buttonRow.getChildAt(arrivalTimeSelected);
            mContext.onFocusedButton(button, "Text");
        }
        SeekBar minPriceSeekBar = mContext.findViewById(R.id.min_seekbar);
        SeekBar maxPriceSeekBar = mContext.findViewById(R.id.max_seekbar);
        EditText minPriceText = mContext.findViewById(R.id.min_seek_text);
        EditText maxPriceText = mContext.findViewById(R.id.max_seek_text);
        minPriceText.setText(String.valueOf(minPrice));
        maxPriceText.setText(String.valueOf(maxPrice));
        minPriceSeekBar.setProgress(minPrice);
        maxPriceSeekBar.setProgress(maxPrice);
        minPriceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= maxPrice) {
                    seekBar.setProgress(maxPrice - 1);
                    minPrice = maxPrice - 1;
                    minPriceText.setText(String.valueOf(minPrice));
                } else {
                    minPrice = progress;
                    seekBar.setProgress(progress);
                    minPriceText.setText(String.valueOf(minPrice));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        maxPriceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= minPrice) {
                    seekBar.setProgress(minPrice + 1);
                    maxPrice = minPrice + 1;
                    maxPriceText.setText(String.valueOf(maxPrice));
                } else {
                    maxPrice = progress;
                    seekBar.setProgress(progress);
                    maxPriceText.setText(String.valueOf(maxPrice));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        minPriceText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                EditText editText = (EditText) v;
                String text = editText.getText().toString();
                if (text.isEmpty()) {
                    minPrice = 0;
                    minPriceText.setText("0");
                    minPriceSeekBar.setProgress(0);
                } else {
                    minPrice = Integer.parseInt(text);
                    if (minPrice >= maxPrice) {
                        minPrice = maxPrice - 1;
                        minPriceText.setText(String.valueOf(minPrice));
                    } else if (minPrice < 0) {
                        minPrice = 0;
                        minPriceText.setText("0");
                    }
                    minPriceSeekBar.setProgress(minPrice);
                }
            }
        });
        maxPriceText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                EditText editText = (EditText) v;
                String text = editText.getText().toString();
                if (text.isEmpty()) {
                    maxPrice = 300;
                    maxPriceText.setText("300");
                    maxPriceSeekBar.setProgress(300);
                } else {
                    maxPrice = Integer.parseInt(text);
                    if (maxPrice <= minPrice) {
                        maxPrice = minPrice + 1;
                        maxPriceText.setText(String.valueOf(maxPrice));
                    } else if (maxPrice > 300) {
                        maxPrice = 300;
                        maxPriceText.setText("300");
                    }
                    maxPriceSeekBar.setProgress(maxPrice);
                }
            }
        });
        LinearLayout facilitiesSelect = mContext.findViewById(R.id.facilities_select);
        for (int i = 0; i < facilities.length; ++i)
            if (facilities[i]) {
                LinearLayout button = (LinearLayout) facilitiesSelect.getChildAt(i);
                onFocusedButton(button, true, "Image");
            }
        RadioGroup sortBySelect = mContext.findViewById(R.id.sort_by_select);
        if (sortSelected != -1)
            sortBySelect.check(sortBySelect.getChildAt(sortSelected).getId());
    }

    public void selectDepartureTime(@NonNull View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        departureTimeSelected = parent.indexOfChild(view);
        LinearLayout button = (LinearLayout) view;
        mContext.onFocusedButton(button, "Text");
    }

    public void selectSortBy(@NonNull View view) {
        RadioGroup radioGroup = (RadioGroup) view.getParent();
        sortSelected = radioGroup.indexOfChild(view);
    }

    @SuppressLint("SetTextI18n")
    public void resetButton() {
        if (departureTimeSelected != -1) {
            LinearLayout buttonRow = mContext.findViewById(R.id.departure_time_select);
            LinearLayout button = (LinearLayout) buttonRow.getChildAt(departureTimeSelected);
            onFocusedButton(button, false, "Text");
        }
        if (arrivalTimeSelected != -1) {
            LinearLayout buttonRow = mContext.findViewById(R.id.arrival_time_select);
            LinearLayout button = (LinearLayout) buttonRow.getChildAt(arrivalTimeSelected);
            onFocusedButton(button, false, "Text");
        }
        SeekBar minPriceSeekBar = mContext.findViewById(R.id.min_seekbar);
        SeekBar maxPriceSeekBar = mContext.findViewById(R.id.max_seekbar);
        minPriceSeekBar.setProgress(0);
        maxPriceSeekBar.setProgress(300);
        TextView minPriceText = mContext.findViewById(R.id.min_seek_text);
        TextView maxPriceText = mContext.findViewById(R.id.max_seek_text);
        minPriceText.setText("0");
        maxPriceText.setText("300");
        for (int i = 0; i < facilities.length; ++i)
            if (facilities[i]) {
                LinearLayout buttonRow = mContext.findViewById(R.id.facilities_select);
                LinearLayout button = (LinearLayout) buttonRow.getChildAt(i);
                onFocusedButton(button, false, "Image");
            }
        if (sortSelected != -1) {
            RadioGroup radioGroup = mContext.findViewById(R.id.sort_by_select);
            radioGroup.clearCheck();
        }
        reset();
    }

    public void filterDone() {
        mContext.backButton(null);
        mContext.findViewById(R.id.flight_layout).requestApplyInsets();
    }
}

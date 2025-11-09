package com.nhk.travelly;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;

public class FlightLayout {
    BookingActivity mContext;
    TransportBookingLayout transportBookingLayout;
    FilterLayout filterLayout;
    ArrayList<Integer> flightListArray = new ArrayList<>();
    int departureID = -1;

    public FlightLayout(BookingActivity context, TransportBookingLayout transportBookingLayout, FilterLayout filterLayout) {
        mContext = context;
        this.transportBookingLayout = transportBookingLayout;
        this.filterLayout = filterLayout;
    }

    @SuppressLint("DefaultLocale")
    public void init() {
        flightListArray.add(0);
        flightListArray.add(1);
        flightListArray.add(2);
        flightListArray.add(3);
        LinearLayout dateList = mContext.findViewById(R.id.date_list);
        Calendar calendar = Calendar.getInstance();
        calendar.set(transportBookingLayout.departureDateTime.year, transportBookingLayout.departureDateTime.month, transportBookingLayout.departureDateTime.day);
        calendar.add(Calendar.DATE, -1);
        String[] dayOfWeekStrings = {"SU", "MO", "TU", "WE", "TH", "FR", "SA"};
        for (int i = 0; i <= 12; i += 2) {
            LinearLayout date = (LinearLayout) dateList.getChildAt(i);
            TextView dayOfWeek = (TextView) date.getChildAt(0);
            TextView dayOfMonth = (TextView) date.getChildAt(1);
            calendar.add(Calendar.DATE, 1);
            dayOfWeek.setText(dayOfWeekStrings[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            dayOfMonth.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        }
        TextView numOfFlightAvailable = mContext.findViewById(R.id.num_of_flights_available);
        String cityFrom = mContext.airportCities[transportBookingLayout.airportFrom];
        String cityTo = mContext.airportCities[transportBookingLayout.airportTo];
        LinearLayout flightList = mContext.findViewById(R.id.flight_list);
        int numOfFlight = flightList.getChildCount();
        for (int i = 0; i < numOfFlight; ++i) {
            LinearLayout flight = (LinearLayout) flightList.getChildAt(i);
            setTextForFlight(flight, i);
        }
        filterFlight();
        numOfFlightAvailable.setText(String.format("%d flights from %s to %s", flightListArray.size(), cityFrom, cityTo));
        for (int i = 0; i < numOfFlight; ++i) {
            LinearLayout flight = (LinearLayout) flightList.getChildAt(i);
            if (flightListArray.contains(i))
                flight.setVisibility(View.VISIBLE);
            else
                flight.setVisibility(View.GONE);
        }
        flightListArray.clear();
    }

    private void filterFlight() {
        if (filterLayout.departureTimeSelected != -1)
            setFlightInvisible(filterLayout.departureTimeSelected);
        if (filterLayout.arrivalTimeSelected != -1)
            setFlightInvisible((filterLayout.arrivalTimeSelected + 3) % 4);
        int price = (transportBookingLayout.classSelected == 0) ? 100 : 200;
        if (filterLayout.minPrice > price || filterLayout.maxPrice < price)
            flightListArray.clear();
    }

    private void setFlightInvisible(int i) {
        flightListArray.removeIf(flight -> flight != i);
    }

    @SuppressLint("SetTextI18n")
    private void setTextForFlight(@NonNull LinearLayout flight, int i) {
        LinearLayout fromTo = (LinearLayout) flight.getChildAt(0);
        LinearLayout from = (LinearLayout) fromTo.getChildAt(0);
        LinearLayout to = (LinearLayout) fromTo.getChildAt(4);
        TextView fromCode = (TextView) from.getChildAt(0);
        TextView fromCity = (TextView) from.getChildAt(1);
        TextView toCode = (TextView) to.getChildAt(0);
        TextView toCity = (TextView) to.getChildAt(1);
        fromCode.setText(mContext.airportCodes[transportBookingLayout.airportFrom]);
        fromCity.setText(mContext.airportCities[transportBookingLayout.airportFrom]);
        toCode.setText(mContext.airportCodes[transportBookingLayout.airportTo]);
        toCity.setText(mContext.airportCities[transportBookingLayout.airportTo]);
        LinearLayout detail = (LinearLayout) flight.getChildAt(4);
        LinearLayout date = (LinearLayout) detail.getChildAt(0);
        LinearLayout departure = (LinearLayout) detail.getChildAt(2);
        LinearLayout price = (LinearLayout) detail.getChildAt(4);
        LinearLayout number = (LinearLayout) detail.getChildAt(6);
        TextView _date = (TextView) date.getChildAt(1);
        TextView _departure = (TextView) departure.getChildAt(1);
        TextView _price = (TextView) price.getChildAt(1);
        TextView _number = (TextView) number.getChildAt(1);
        _date.setText(transportBookingLayout.departureDateTime.toDateMonth());
        switch (i) {
            case 0:
                _departure.setText("3:00 AM");
                break;
            case 1:
                _departure.setText("9:00 AM");
                break;
            case 2:
                _departure.setText("3:00 PM");
                break;
            case 3:
                _departure.setText("9:00 PM");
                break;
            default:
                break;
        }
        if (transportBookingLayout.classSelected == 0)
            _price.setText("$100");
        else if (transportBookingLayout.classSelected == 1)
            _price.setText("$200");
        _number.setText(mContext.airportCodes[transportBookingLayout.airportFrom].charAt(0) + "" + mContext.airportCodes[transportBookingLayout.airportTo].charAt(0) + "-0" + i);
    }

    public void changeDepartureDate(@NonNull View view) {
        LinearLayout dateRow = (LinearLayout) view.getParent();
        int index = dateRow.indexOfChild(view);
        Calendar calendar = Calendar.getInstance();
        calendar.set(transportBookingLayout.departureDateTime.year, transportBookingLayout.departureDateTime.month, transportBookingLayout.departureDateTime.day);
        calendar.add(Calendar.DATE, index / 2);
        DateTime tmp = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (transportBookingLayout.returnDateTime.lessThan(tmp)) {
            Toast.makeText(mContext, "Departure date must be before return date!", Toast.LENGTH_SHORT).show();
            return;
        }
        transportBookingLayout.departureDateTime = tmp;
        init();
    }

    public void selectFlight(@NonNull View view) {
        LinearLayout allFlight = mContext.findViewById(R.id.flight_list);
        departureID = allFlight.indexOfChild(view);
        mContext.selectSeatLayout.reset(mContext.numOfAdults + mContext.numOfChildren, transportBookingLayout.classSelected);
        mContext.setLayout(R.layout.select_seats, R.id.select_seats_layout);
        mContext.findViewById(R.id.select_seats_layout).requestApplyInsets();
    }
}

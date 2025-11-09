package com.nhk.travelly;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class SelectSeatLayout {
    BookingActivity mContext;
    TransportBookingLayout transportBookingLayout;
    FlightLayout flightLayout;
    int numTravelers = 0;
    int classType = -1;
    boolean[][] seatStatus = new boolean[7][4];
    int travelerIndex = 0;
    int price = 0;
    Pair<Integer, Integer>[] seatTraveler;

    public SelectSeatLayout(BookingActivity context, TransportBookingLayout transportBookingLayout, FlightLayout flightLayout) {
        mContext = context;
        this.transportBookingLayout = transportBookingLayout;
        this.flightLayout = flightLayout;
    }

    public void reset(int num, int classSelected) {
        numTravelers = num;
        classType = classSelected;
        String namePreference = transportBookingLayout.departureDateTime.year + "_" + transportBookingLayout.departureDateTime.month + "_" + transportBookingLayout.departureDateTime.day + "_" + flightLayout.departureID + "_" + classType + "_" + mContext.airportCodes[transportBookingLayout.airportFrom] + "_" + mContext.airportCodes[transportBookingLayout.airportTo];
        SharedPreferences seatStatusPref = mContext.getSharedPreferences(namePreference, 0);
        for (int i = 0; i < 7; ++i)
            for (int j = 0; j < 4; ++j)
                seatStatus[i][j] = seatStatusPref.getBoolean(i + "_" + j, false);
        price = classType == 0 ? 100 : 200;
        seatTraveler = new Pair[num];
        for (int i = 0; i < num; ++i)
            seatTraveler[i] = null;
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        LinearLayout travelerRow = mContext.findViewById(R.id.traveler_select);
        for (int i = 1; i < 6; ++i) {
            View separator = travelerRow.getChildAt(2 * i - 1);
            View traveler = travelerRow.getChildAt(2 * i);
            if (i >= numTravelers) {
                separator.setVisibility(View.INVISIBLE);
                traveler.setVisibility(View.INVISIBLE);
            }
        }
        for (int i = 0; i < 7; ++i)
            for (int j = 0; j < 4; ++j) {
                int id = mContext.getResources().getIdentifier("seat_" + i + "_" + j, "id", mContext.getPackageName());
                View seat = mContext.findViewById(id);
                if (seatStatus[i][j])
                    setBackGroundView(seat, R.drawable.light_green_box);
                else
                    setBackGroundView(seat, R.drawable.very_light_green_box);
                if (seatTraveler[travelerIndex] != null && seatTraveler[travelerIndex].first == i && seatTraveler[travelerIndex].second == j)
                    setBackGroundView(seat, R.drawable.peach_box);
            }
        TextView totalPrice = mContext.findViewById(R.id.total_price);
        totalPrice.setText("$" + numOfTravelerSelected() * price);
        TextView seatName = mContext.findViewById(R.id.seat_name);
        if (seatTraveler[travelerIndex] != null) {
            String columnNames = "ABCD";
            seatName.setText("Traveller " + (travelerIndex + 1) + " / Seat " + (seatTraveler[travelerIndex].first + 1) + columnNames.charAt(seatTraveler[travelerIndex].second));
        } else
            seatName.setText("Traveller " + (travelerIndex + 1) + " /");
    }

    public void onFocusedTraveler(@NonNull View view) {
        LinearLayout travelerRow = (LinearLayout) view.getParent();
        for (int i = 0; i <= 10; i += 2) {
            View traveler = travelerRow.getChildAt(i);
            if (traveler == view)
                traveler.setBackground(ContextCompat.getDrawable(mContext, R.drawable.light_peach_box));
            else
                traveler.setBackground(ContextCompat.getDrawable(mContext, R.drawable.box));
        }
    }

    public void selectTraveler(@NonNull View view) {
        LinearLayout travelerRow = (LinearLayout) view.getParent();
        int index = travelerRow.indexOfChild(view) / 2;
        if (index == travelerIndex)
            return;
        if (seatTraveler[travelerIndex] != null)
            seatStatus[seatTraveler[travelerIndex].first][seatTraveler[travelerIndex].second] = true;
        travelerIndex = index;
        onFocusedTraveler(view);
        init();
    }

    @SuppressLint("SetTextI18n")
    public void selectSeat(@NonNull View view) {
        String id = mContext.getResources().getResourceEntryName(view.getId());
        int row = Integer.parseInt(id.split("_")[1]);
        int col = Integer.parseInt(id.split("_")[2]);
        if (seatStatus[row][col]) {
            Toast.makeText(mContext, "This seat is already taken, please select another one", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView seatName = mContext.findViewById(R.id.seat_name);
        TextView totalPrice = mContext.findViewById(R.id.total_price);
        if (seatTraveler[travelerIndex] != null) {
            seatStatus[seatTraveler[travelerIndex].first][seatTraveler[travelerIndex].second] = false;
            int _id = mContext.getResources().getIdentifier("seat_" + seatTraveler[travelerIndex].first + "_" + seatTraveler[travelerIndex].second, "id", mContext.getPackageName());
            View seat = mContext.findViewById(_id);
            setBackGroundView(seat, R.drawable.very_light_green_box);

            if (seatTraveler[travelerIndex].first == row && seatTraveler[travelerIndex].second == col) {
                seatTraveler[travelerIndex] = null;
                seatName.setText("Traveller " + (travelerIndex + 1) + " /");
                totalPrice.setText("$" + numOfTravelerSelected() * price);
                return;
            }
        }
        setBackGroundView(view, R.drawable.peach_box);
        seatTraveler[travelerIndex] = new Pair<>(row, col);
        String columnNames = "ABCD";
        seatName.setText("Traveller " + (travelerIndex + 1) + " / Seat " + (row + 1) + columnNames.charAt(col));
        totalPrice.setText("$" + numOfTravelerSelected() * price);
    }

    private void setBackGroundView(@NonNull View view, int box) {
        view.setBackground(ContextCompat.getDrawable(mContext, box));
    }

    public void realSelectSeat() {
        if (numOfTravelerSelected() < numTravelers) {
            Toast.makeText(mContext, "Please select a seat for all travelers", Toast.LENGTH_SHORT).show();
            return;
        }
        seatStatus[seatTraveler[travelerIndex].first][seatTraveler[travelerIndex].second] = true;
        String namePreference = transportBookingLayout.departureDateTime.year + "_" + transportBookingLayout.departureDateTime.month + "_" + transportBookingLayout.departureDateTime.day + "_" + flightLayout.departureID + "_" + classType + "_" + mContext.airportCodes[transportBookingLayout.airportFrom] + "_" + mContext.airportCodes[transportBookingLayout.airportTo];
        SharedPreferences.Editor seatStatusPref = mContext.getSharedPreferences(namePreference, 0).edit();
        for (int i = 0; i < 7; ++i)
            for (int j = 0; j < 4; ++j)
                seatStatusPref.putBoolean(i + "_" + j, seatStatus[i][j]);
        seatStatusPref.apply();
        mContext.boardingPassLayout.reset(flightLayout.departureID, seatTraveler);
        mContext.setLayout(R.layout.boarding_pass, R.id.boarding_pass_layout);
        mContext.findViewById(R.id.boarding_pass_layout).requestApplyInsets();
    }

    private int numOfTravelerSelected() {
        int count = 0;
        for (int i = 0; i < numTravelers; ++i)
            if (seatTraveler[i] != null)
                ++count;
        return count;
    }
}

package com.nhk.travelly;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Stack;

public class BookingActivity extends AppCompatActivity implements DateTimePickerFragment.OnFragmentInteractionListener {
    public boolean isTransport;
    Stack<Integer> layoutStack = new Stack<>();
    HashMap<Integer, Integer> layoutMap = new HashMap<Integer, Integer>() {{
        put(R.layout.booking, R.id.mainBookingLayout);
        put(R.layout.transport_booking, R.id.transport_booking_layout);
        put(R.layout.flights, R.id.flight_layout);
        put(R.layout.filter_flight, R.id.filter_layout);
        put(R.layout.select_seats, R.id.select_seats_layout);
        put(R.layout.boarding_pass, R.id.boarding_pass_layout);
    }};
    TransportBookingLayout transportBookingLayout = new TransportBookingLayout(this);
    FilterLayout filterLayout = new FilterLayout(this);
    FlightLayout flightLayout = new FlightLayout(this, transportBookingLayout, filterLayout);
    SelectSeatLayout selectSeatLayout = new SelectSeatLayout(this, transportBookingLayout, flightLayout);
    BoardingPassLayout boardingPassLayout = new BoardingPassLayout(this, transportBookingLayout);
    public String[] listOfAirports = {"", "John F. Kennedy International Airport", "Heathrow Airport", "Singapore Changi Airport", "Narita International Airport", "Hong Kong International Airport", "Da Nang International Airport"};
    public String[] airportCodes = {"", "JFK", "LHR", "SIN", "NRT", "HKG", "DAD"};
    public String[] airportCities = {"", "New York", "London", "Singapore", "Tokyo", "Hong Kong", "Da Nang"};
    public int numOfAdults = 0, numOfChildren = 0, numOfPets = 0, numOfLuggage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        isTransport = getIntent().getBooleanExtra("isTransport", false);
        if (isTransport) {
            setLayout(R.layout.transport_booking, R.id.transport_booking_layout);
            isTransport = false;
        } else
            setLayout(R.layout.booking, R.id.mainBookingLayout);
    }

    public void changeTab(@NonNull View view) {
        int id = view.getId();
        Intent intent;
        if (id == R.id.account)
            intent = new Intent(this, AccountActivity.class);
        else if (id == R.id.home)
            intent = new Intent(this, HomeActivity.class);
        else {
            Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void backButton(View view) {
        layoutStack.pop();
        if (!layoutStack.isEmpty()) {
            int layoutID = layoutStack.peek();
            int mainLayoutID = layoutMap.get(layoutID);
            setLayout(layoutID, mainLayoutID);
            layoutStack.pop();
            findViewById(mainLayoutID).requestApplyInsets();
        } else {
            setLayout(R.layout.booking, R.id.mainBookingLayout);
            findViewById(R.id.mainBookingLayout).requestApplyInsets();
        }
    }

    public void backButtonFinal(View view) {
        layoutStack.clear();
        setLayout(R.layout.booking, R.id.mainBookingLayout);
        findViewById(R.id.mainBookingLayout).requestApplyInsets();
    }

    @Override
    public void onBackPressed() {
        if (layoutStack.peek() != R.layout.booking)
            backButton(null);
        else
            super.onBackPressed();
    }

    public void setLayout(int layoutID, int mainLayoutID) {
        setContentView(layoutID);
        layoutStack.push(layoutID);
        if (layoutID == R.layout.booking)
            transportBookingLayout.reset();
        else if (layoutID == R.layout.transport_booking)
            initTransportBooking();
        else if (layoutID == R.layout.flights)
            initFlights();
        else if (layoutID == R.layout.filter_flight)
            filterLayout.init();
        else if (layoutID == R.layout.select_seats)
            selectSeatLayout.init();
        else if (layoutID == R.layout.boarding_pass)
            boardingPassLayout.init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(mainLayoutID), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initTransportBooking() {
        if (isTransport) {
            transportBookingLayout.reset();
            isTransport = false;
        }
        filterLayout.reset();
        transportBookingLayout.init();
    }

    private void initFlights() {
        flightLayout.init();
    }

    public void bookingCard(@NonNull View view) {
        if (view.getId() == R.id.transport_card) {
            setLayout(R.layout.transport_booking, R.id.transport_booking_layout);
            findViewById(R.id.transport_booking_layout).requestApplyInsets();
        } else
            Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateTimeSet(DateTime dateTime, boolean isDeparture) {
        if (isDeparture) {
            transportBookingLayout.departureDateTime = dateTime.copy();
            transportBookingLayout.isPickingDeparture = true;
            transportBookingLayout.isPickingReturn = false;
        } else {
            transportBookingLayout.returnDateTime = dateTime.copy();
            transportBookingLayout.isPickingReturn = true;
        }
    }

    public void showDatePickerDialogDeparture(View view) {
        DateTimePickerFragment fragment = new DateTimePickerFragment(findViewById(R.id.departure_date), this, true, transportBookingLayout.departureDateTime);
        fragment.show(getSupportFragmentManager(), "dateTimePicker");
    }

    public void showDatePickerDialogReturn(View view) {
        transportBookingLayout.showDatePickerDialogReturn();
    }

    public void onFocusedButton(@NonNull LinearLayout button, String id) {
        LinearLayout buttonRow = (LinearLayout) button.getParent();
        int numOfChild = buttonRow.getChildCount();
        for (int i = 0; i < numOfChild; ++i) {
            LinearLayout child = (LinearLayout) buttonRow.getChildAt(i);
            if (child == button)
                child.setBackground(ContextCompat.getDrawable(this, R.drawable.light_green_box));
            else child.setBackground(ContextCompat.getDrawable(this, R.drawable.box));
        }
        if (id.equals("Text"))
            for (int i = 0; i < numOfChild; ++i) {
                LinearLayout child = (LinearLayout) buttonRow.getChildAt(i);
                TextView textView = (TextView) child.getChildAt(0);
                if (child == button)
                    textView.setTextColor(ContextCompat.getColor(this, R.color.white));
                else textView.setTextColor(ContextCompat.getColor(this, R.color.green_text));
            }
        else if (id.equals("Image"))
            for (int i = 0; i < numOfChild; ++i) {
                LinearLayout child = (LinearLayout) buttonRow.getChildAt(i);
                ImageView imageView = (ImageView) child.getChildAt(0);
                if (child == button)
                    imageView.setColorFilter(ContextCompat.getColor(this, R.color.white));
                else imageView.setColorFilter(ContextCompat.getColor(this, R.color.green_text));
            }
    }

    public void selectClass(@NonNull View view) {
        onFocusedButton((LinearLayout) view, "Text");
        transportBookingLayout.selectClass(view);
    }

    public void selectTransport(@NonNull View view) {
        onFocusedButton((LinearLayout) view, "Image");
        transportBookingLayout.selectTransport(view);
    }

    public void swapLocation(View view) {
        transportBookingLayout.swapLocation();
    }

    public void search(View view) {
        transportBookingLayout.search();
    }

    public void changeDepartureDate(@NonNull View view) {
        flightLayout.changeDepartureDate(view);
    }

    public void selectFacility(View view) {
        filterLayout.selectFacility(view);
    }

    public void openFilter(View view) {
        setLayout(R.layout.filter_flight, R.id.filter_layout);
        findViewById(R.id.filter_layout).requestApplyInsets();
    }

    public void selectArrivalTime(View view) {
        filterLayout.selectArrivalTime(view);
    }

    public void selectDepartureTime(View view) {
        filterLayout.selectDepartureTime(view);
    }

    public void selectSortBy(View view) {
        filterLayout.selectSortBy(view);
    }

    public void resetButton(View view) {
        filterLayout.resetButton();
    }

    public void filterDone(View view) {
        filterLayout.filterDone();
    }

    public void selectSeat(View view) {
        selectSeatLayout.selectSeat(view);
    }

    public void selectFlight(View view) {
        flightLayout.selectFlight(view);
    }

    public void selectTraveler(View view) {
        selectSeatLayout.selectTraveler(view);
    }

    public void endSelectSeat(View view) {
        selectSeatLayout.realSelectSeat();
    }

    public void selectTravelerTicket(View view) {
        boardingPassLayout.selectTravelerTicket(view);
    }

    public void downloadTicket(View view) {
        boardingPassLayout.downloadTicket();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                boardingPassLayout.downloadTicket();
            else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
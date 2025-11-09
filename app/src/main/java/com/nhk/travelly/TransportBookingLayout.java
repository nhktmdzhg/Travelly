package com.nhk.travelly;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class TransportBookingLayout {
    BookingActivity mContext;
    DateTime departureDateTime, returnDateTime;
    boolean isPickingDeparture, isPickingReturn;
    boolean isSelectFrom, isSelectTo, isClickSwap;
    int airportFrom, airportTo, classSelected, transportSelected;

    public TransportBookingLayout(BookingActivity context) {
        mContext = context;
        departureDateTime = new DateTime();
        isPickingDeparture = false;
        isPickingReturn = false;
        isSelectFrom = false;
        isSelectTo = false;
        isClickSwap = false;
        classSelected = -1;
        transportSelected = -1;
    }

    public void onFocusPassengerAndLuggage(@NonNull View view, boolean b) {
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        LinearLayout parent = (LinearLayout) linearLayout.getParent();
        ImageView imageView = (ImageView) linearLayout.getChildAt(0);
        View underline = parent.getChildAt(1);
        EditText editText = (EditText) view;
        final int colorFocused = ContextCompat.getColor(mContext, R.color.green_button);
        final int colorUnfocused = ContextCompat.getColor(mContext, R.color.gray_text);
        if (b) {
            imageView.setColorFilter(colorFocused);
            underline.setBackgroundColor(colorFocused);
            editText.setTextColor(colorFocused);
        } else {
            imageView.setColorFilter(colorUnfocused);
            underline.setBackgroundColor(colorUnfocused);
            editText.setTextColor(colorUnfocused);
        }
    }

    public void reset() {
        departureDateTime = new DateTime();
        isPickingDeparture = false;
        isPickingReturn = false;
        isSelectFrom = false;
        isSelectTo = false;
        isClickSwap = false;
        classSelected = -1;
        transportSelected = -1;
    }

    private int getIndex(String location, @NonNull String[] listOfAirports) {
        for (int i = 0; i < listOfAirports.length; ++i)
            if (listOfAirports[i].equals(location))
                return i;
        return -1;
    }

    public void onSpinnerSelected(int i) {
        if (isClickSwap) {
            isClickSwap = false;
            return;
        }
        Spinner toSpinner = mContext.findViewById(R.id.toSpinner);
        String selectedAirport = mContext.listOfAirports[i];
        if (selectedAirport.isEmpty()) {
            isSelectFrom = false;
            return;
        }
        airportFrom = getIndex(selectedAirport, mContext.listOfAirports);
        String[] toSpinnerList = new String[mContext.listOfAirports.length - 1];
        int index = 0;
        for (String airport : mContext.listOfAirports)
            if (!airport.equals(selectedAirport))
                toSpinnerList[index++] = airport;
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, toSpinnerList);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        isSelectFrom = true;
        isSelectTo = false;
    }

    public void onToSpinnerSelected(AdapterView<?> adapterView, int i) {
        if (!isSelectFrom) {
            Toast.makeText(mContext, "Please select departure airport first!", Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedAirport = adapterView.getItemAtPosition(i).toString();
        if (selectedAirport.isEmpty()) {
            isSelectTo = false;
            return;
        }
        isSelectTo = true;
        airportTo = getIndex(selectedAirport, mContext.listOfAirports);
    }

    public void showDatePickerDialogReturn() {
        if (!isPickingDeparture) {
            Toast.makeText(mContext, "Please pick departure date first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (returnDateTime == null)
            returnDateTime = departureDateTime.copy();
        if (returnDateTime.lessThan(departureDateTime))
            returnDateTime = departureDateTime.copy();
        DateTimePickerFragment fragment = new DateTimePickerFragment(mContext.findViewById(R.id.return_date), mContext, false, returnDateTime, departureDateTime);
        fragment.show(mContext.getSupportFragmentManager(), "dateTimePicker");
    }

    public void selectClass(@NonNull View view) {
        if (view.getId() == R.id.economy)
            classSelected = 0;
        else if (view.getId() == R.id.business)
            classSelected = 1;
    }

    public void selectTransport(@NonNull View view) {
        int id = view.getId();
        if (id == R.id.flight)
            transportSelected = 0;
        else if (id == R.id.ship)
            transportSelected = 1;
        else if (id == R.id.train)
            transportSelected = 2;
        else if (id == R.id.coach)
            transportSelected = 3;
    }

    public void swapLocation() {
        if (!isSelectFrom || !isSelectTo) {
            Toast.makeText(mContext, "Please select departure and arrival airport first!", Toast.LENGTH_SHORT).show();
            return;
        }
        isClickSwap = true;
        Spinner fromSpinner = mContext.findViewById(R.id.fromSpinner);
        Spinner toSpinner = mContext.findViewById(R.id.toSpinner);
        String fromLocation = (String) fromSpinner.getSelectedItem();
        String toLocation = (String) toSpinner.getSelectedItem();
        airportFrom = getIndex(toLocation, mContext.listOfAirports);
        airportTo = getIndex(fromLocation, mContext.listOfAirports);
        fromSpinner.setSelection(getIndex(toLocation, mContext.listOfAirports));
        String[] toSpinnerList = new String[mContext.listOfAirports.length - 1];
        int index = 0;
        for (String airport : mContext.listOfAirports)
            if (!airport.equals(toLocation))
                toSpinnerList[index++] = airport;
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, toSpinnerList);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setSelection(getIndex(fromLocation, toSpinnerList));
    }

    public void search() {
        if (!isSelectFrom || !isSelectTo) {
            Toast.makeText(mContext, "Please select departure and arrival airport first!", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText numOfAdult = mContext.findViewById(R.id.num_of_adult);
        EditText numOfChildren = mContext.findViewById(R.id.num_of_children);
        EditText numOfPet = mContext.findViewById(R.id.num_of_pet);
        EditText numOfLuggage = mContext.findViewById(R.id.num_of_luggage);
        String adult = numOfAdult.getText().toString();
        String children = numOfChildren.getText().toString();
        String pet = numOfPet.getText().toString();
        String luggage = numOfLuggage.getText().toString();
        if (adult.isEmpty() || children.isEmpty() || pet.isEmpty() || luggage.isEmpty()) {
            Toast.makeText(mContext, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        int _adult = Integer.parseInt(adult);
        int _children = Integer.parseInt(children);
        int _pet = Integer.parseInt(pet);
        int _luggage = Integer.parseInt(luggage);
        if (_adult + _children > 6) {
            Toast.makeText(mContext, "Maximum ticket can book in one time is 6", Toast.LENGTH_SHORT).show();
            return;
        }
        mContext.numOfAdults = _adult;
        mContext.numOfChildren = _children;
        mContext.numOfPets = _pet;
        mContext.numOfLuggage = _luggage;

        if (classSelected == -1) {
            Toast.makeText(mContext, "Please select class first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (transportSelected == -1) {
            Toast.makeText(mContext, "Please select transport first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (transportSelected != 0) {
            Toast.makeText(mContext, "This feature is not available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        mContext.setLayout(R.layout.flights, R.id.flight_layout);
        mContext.findViewById(R.id.flight_layout).requestApplyInsets();
    }

    public void init() {
        EditText numOfAdult = mContext.findViewById(R.id.num_of_adult);
        EditText numOfChildren = mContext.findViewById(R.id.num_of_children);
        EditText numOfPet = mContext.findViewById(R.id.num_of_pet);
        EditText numOfLuggage = mContext.findViewById(R.id.num_of_luggage);
        numOfAdult.setOnFocusChangeListener(this::onFocusPassengerAndLuggage);
        numOfChildren.setOnFocusChangeListener(this::onFocusPassengerAndLuggage);
        numOfPet.setOnFocusChangeListener(this::onFocusPassengerAndLuggage);
        numOfLuggage.setOnFocusChangeListener(this::onFocusPassengerAndLuggage);

        Spinner fromSpinner = mContext.findViewById(R.id.fromSpinner);
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mContext.listOfAirports);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        if (isSelectFrom)
            fromSpinner.setSelection(airportFrom);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner toSpinner = mContext.findViewById(R.id.toSpinner);
        toSpinner.setAdapter(null);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onToSpinnerSelected(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (isPickingReturn) {
            TextView departureDate = mContext.findViewById(R.id.departure_date);
            TextView returnDate = mContext.findViewById(R.id.return_date);
            departureDate.setText(departureDateTime.toString());
            returnDate.setText(returnDateTime.toString());
        }
        numOfAdult.setText(String.valueOf(mContext.numOfAdults));
        numOfChildren.setText(String.valueOf(mContext.numOfChildren));
        numOfPet.setText(String.valueOf(mContext.numOfPets));
        numOfLuggage.setText(String.valueOf(mContext.numOfLuggage));
        if (classSelected == 0)
            mContext.findViewById(R.id.economy).performClick();
        else if (classSelected == 1)
            mContext.findViewById(R.id.business).performClick();
        if (transportSelected == 0)
            mContext.findViewById(R.id.flight).performClick();
        else if (transportSelected == 1)
            mContext.findViewById(R.id.ship).performClick();
        else if (transportSelected == 2)
            mContext.findViewById(R.id.train).performClick();
        else if (transportSelected == 3)
            mContext.findViewById(R.id.coach).performClick();
    }
}

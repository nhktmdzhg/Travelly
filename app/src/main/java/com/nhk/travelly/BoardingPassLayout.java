package com.nhk.travelly;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BoardingPassLayout {
    BookingActivity mContext;
    TransportBookingLayout transportBookingLayout;
    int numTravelers = 0;
    int travelerIndex = 0;
    Pair<Integer, Integer>[] seatTraveler;
    String departureTime;
    int departureID = -1;

    public BoardingPassLayout(BookingActivity context, TransportBookingLayout transportBookingLayout) {
        mContext = context;
        this.transportBookingLayout = transportBookingLayout;
    }

    public void reset(int departureID, Pair<Integer, Integer>[] seatTraveler) {
        this.seatTraveler = seatTraveler;
        this.departureID = departureID;
        switch (departureID) {
            case 0:
                departureTime = "3:00 AM";
                break;
            case 1:
                departureTime = "9:00 AM";
                break;
            case 2:
                departureTime = "3:00 PM";
                break;
            case 3:
                departureTime = "9:00 PM";
                break;
            default:
                break;
        }
        numTravelers = mContext.numOfAdults + mContext.numOfChildren;
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        LinearLayout travelerRow = mContext.findViewById(R.id.traveler_select_boarding_pass);
        for (int i = 1; i < 6; ++i) {
            View separator = travelerRow.getChildAt(2 * i - 1);
            View traveler = travelerRow.getChildAt(2 * i);
            if (i >= numTravelers) {
                separator.setVisibility(View.INVISIBLE);
                traveler.setVisibility(View.INVISIBLE);
            }
        }
        TextView fromCode = mContext.findViewById(R.id.from_code);
        TextView toCode = mContext.findViewById(R.id.to_code);
        TextView fromCity = mContext.findViewById(R.id.from_city);
        TextView toCity = mContext.findViewById(R.id.to_city);
        fromCode.setText(mContext.airportCodes[transportBookingLayout.airportFrom]);
        toCode.setText(mContext.airportCodes[transportBookingLayout.airportTo]);
        fromCity.setText(mContext.airportCities[transportBookingLayout.airportFrom]);
        toCity.setText(mContext.airportCities[transportBookingLayout.airportTo]);
        TextView date = mContext.findViewById(R.id.date_ticket);
        date.setText(transportBookingLayout.departureDateTime.toDateMonth());
        TextView departure = mContext.findViewById(R.id.departure_ticket);
        departure.setText(departureTime);
        TextView passenger = mContext.findViewById(R.id.passenger_ticket);
        String passengerText = travelerIndex < mContext.numOfAdults ? "Adult" : "Child";
        passenger.setText(passengerText);
        TextView ticketNumber = mContext.findViewById(R.id.ticket_number);
        ticketNumber.setText(mContext.airportCodes[transportBookingLayout.airportFrom].charAt(0) + "" + mContext.airportCodes[transportBookingLayout.airportTo].charAt(0) + "-0" + departureID);
        TextView _class = mContext.findViewById(R.id.class_ticket);
        _class.setText(transportBookingLayout.classSelected != 0 ? "Business" : "Economy");
        TextView seat = mContext.findViewById(R.id.seat_ticket);
        String seatCols = "ABCD";
        seat.setText((seatTraveler[travelerIndex].first + 1) + "" + seatCols.charAt(seatTraveler[travelerIndex].second));
        TextView barcode = mContext.findViewById(R.id.barcode_text);
        String barcodeText = BarCodeGenerator.generateBarCodeString();
        barcode.setText(barcodeText);
        ImageView barcodeImage = mContext.findViewById(R.id.barcode_image);
        Bitmap barcodeBitmap = BarCodeGenerator.generateBarCode(barcodeText);
        barcodeImage.setImageBitmap(barcodeBitmap);
    }

    public void selectTravelerTicket(@NonNull View view) {
        LinearLayout travelerRow = (LinearLayout) view.getParent();
        int index = travelerRow.indexOfChild(view) / 2;
        if (index == travelerIndex)
            return;
        for (int i = 0; i <= 10; i += 2) {
            View traveler = travelerRow.getChildAt(i);
            if (traveler == view)
                traveler.setBackground(ContextCompat.getDrawable(mContext, R.drawable.light_peach_box));
            else
                traveler.setBackground(ContextCompat.getDrawable(mContext, R.drawable.box));
        }
        travelerIndex = index;
        init();
    }

    public void downloadTicket() {
        LinearLayout ticket = mContext.findViewById(R.id.ticket_full);
        Bitmap bitmap = captureLayout(ticket);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            saveToGalleryQ(bitmap);
        else {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                mContext.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            else
                saveToGalleryLegacy(bitmap);
        }
    }

    @NonNull
    private Bitmap captureLayout(@NonNull LinearLayout layout) {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);
        layout.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void saveToGalleryQ(@NonNull Bitmap bitmap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Ticket" + System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
        contentValues.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try {
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(mContext, "Ticket saved to gallery", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveToGalleryLegacy(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveToGalleryLegacy(bitmap);
    }

    private void saveToGalleryLegacy(@NonNull Bitmap bitmap) {
        String fileName = "Ticket" + System.currentTimeMillis() + ".png";
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(directory, fileName);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(mContext, "Ticket saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Failed to save ticket", Toast.LENGTH_SHORT).show();
        }
    }
}

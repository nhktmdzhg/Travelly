package com.nhk.travelly;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.Random;

public class BarCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @NonNull
    public static String generateBarCodeString() {
        StringBuilder barCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 14; ++i)
            barCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        return barCode.toString();
    }

    @Nullable
    public static Bitmap generateBarCode(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.CODE_128, 300, 40, null);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; ++x)
                for (int y = 0; y < height; ++y)
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

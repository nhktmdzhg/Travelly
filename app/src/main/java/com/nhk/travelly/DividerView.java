package com.nhk.travelly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class DividerView extends View {
    private Paint paint;

    public DividerView(Context context) {
        super(context);
        init();
    }

    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int dividerWidth = 20;
        int space = 10;

        int numDividers = (width + space) / (dividerWidth + space);
        int totalWidth = (numDividers * dividerWidth) + ((numDividers - 1) * space);
        int startX = (width - totalWidth) / 2;

        for (int i = 0; i < numDividers; ++i) {
            int x = startX + i * (dividerWidth + space);
            canvas.drawLine(x, (float) height / 2, x + dividerWidth, (float) height / 2, paint);
        }
    }
}

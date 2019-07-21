package com.classicloner.runjs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;


public class LinedEditText extends android.support.v7.widget.AppCompatEditText {

    private static Paint linePaint;
    private static Paint fontPaint;

    {
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.dark_line_color));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    {
        fontPaint = new Paint();
        fontPaint.setColor(getResources().getColor(R.color.dark_line_color));
        fontPaint.setStyle(Paint.Style.STROKE);
        fontPaint.setTextSize(this.spToPx(15 , this.getContext()));

    }

    public LinedEditText(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();
        int firstLineY = getLineBounds(0, bounds);
        int lineHeight = getLineHeight();
        int totalLines = Math.max(getLineCount(), getHeight() / lineHeight);

        for (int i = 0; i < totalLines; i++) {
            int lineY = firstLineY + i * lineHeight;
            canvas.drawLine(bounds.left, lineY, bounds.right, lineY, linePaint);
            canvas.drawText(String.valueOf(i+1)+". ",0,lineY,fontPaint);
        }
        super.onDraw(canvas);
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
package com.classicloner.runjs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;


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
//        DisplayMetrics metrics = getMetrics(this.getContext());
//        setMinWidth(metrics.widthPixels);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();
        int firstLineY = getLineBounds(0, bounds);
        int lineHeight = getLineHeight();
        int totalLines = Math.max(getLineCount(), getHeight() / lineHeight);

//        DisplayMetrics metrics = getMetrics(this.getContext());
//        ViewGroup.LayoutParams lparam = getLayoutParams();
//        Log.d("BOUNDS" , "lw: " + lparam.width + "; m: " + metrics.widthPixels + "; lbr: "+ bounds.right+ "; getMeasuredWidth: "+getMeasuredWidth()+ "; getMeasuredWidthAndState: "+ getMeasuredWidthAndState());
//        if ( lparam!=null && lparam.width < metrics.widthPixels) {
//            lparam.width = metrics.widthPixels;
//            setLayoutParams(lparam);
//        }

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

    public DisplayMetrics getMetrics(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
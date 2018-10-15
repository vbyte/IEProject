package com.jiang.geo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;

public class DBView extends android.support.v7.widget.AppCompatImageView {

    private float scaleWidth, scaleHeight;
    private int newWidth, newHeight;
    private Matrix mMatrix = new Matrix();
    private Bitmap indicatorBitmap;
    private Paint paint = new Paint();
    static final long ANIMATION_INTERVAL = 100;


    public DBView(Context context) {
        super(context);
    }

    public DBView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.noise_index);
        int bitmapWidth = myBitmap.getWidth();
        int bitmapHeight = myBitmap.getHeight();
        newWidth = getWidth();
        newHeight = getHeight();
        scaleWidth = ((float) newWidth) / (float) bitmapWidth;  // GET zoom ratio
        scaleHeight = ((float) newHeight) / (float) bitmapHeight;  //get zoom ratio
        mMatrix.postScale(scaleWidth, scaleHeight);   // set the zoom ration of matrix
        // get equality and background height for the noise index
        indicatorBitmap = Bitmap.createBitmap(myBitmap, 0, 0, bitmapWidth, bitmapHeight, mMatrix, true);
        // get equality and background height for the noise index

        paint = new Paint();
        paint.setTextSize(55);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);  //Anti-Aliasing
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (indicatorBitmap == null) {
            init();
        }
        float currentAngle = getAngle(Calculator.dbstart);
        mMatrix.setRotate(currentAngle, newWidth / 2, newHeight * 215 / 460);   //
        canvas.drawBitmap(indicatorBitmap, mMatrix, paint);
        postInvalidateDelayed(ANIMATION_INTERVAL);
        canvas.drawText((int) Calculator.dbstart + " DB", newWidth / 2, newHeight * 36 / 46, paint); //relative piecture
    }

    private float getAngle(float db) {
        return (db - 85) * 5 / 3;
    }

}

package com.jiang.geo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * bitmap处理
 */

public class BitmapUtil {

    private static LayoutInflater sLayoutInflater;

    /**
     * 获取bitmap同时缩放
     *
     * @param c
     * @param resId
     * @return
     */
    public static Bitmap getBitmapFromRes(Context c, int resId) {
        Resources res = c.getResources();
        // set shrinkage to 2
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        // according id to analyse the detail
        return BitmapFactory.decodeResource(res, resId, options);
    }

}

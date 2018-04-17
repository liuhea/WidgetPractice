package com.liuhe.widget.utils;

import android.util.Log;

/**
 * @author liuhe
 * @date 2018-04-02
 */
public class CircleUtils {

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @param d      直径
     * @return
     */
    public static float getAngle(float xTouch, float yTouch, int d) {
        double x = xTouch - (d / 2f);
        double y = yTouch - (d / 2f);
        // hypot √(x^2 + y^2)
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }


    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @param d 直径
     * @return
     */
    public static int getQuadrant(float x, float y, int d) {
        int tempX = (int) (x - (d / 2));
        int tempY = (int) (y - (d / 2));


        if (tempX >= 0) {
            Log.d("CircleUtils", (tempY >= 0 ? 4 : 1) + "象限");
            return tempY >= 0 ? 4 : 1;

        } else {
            Log.d("CircleUtils", (tempY >= 0 ? 3 : 2) + "象限");
            return tempY > 0 ? 3 : 2;
        }
    }
}

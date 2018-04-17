package com.liuhe.widget.utils;

/**
 * @author liuhe
 * @date 2018-04-01
 */
public class MathUtils {
    public static float getTouchAngle(float x, float y) {
        float touchAngle = 0;
        //2 象限
        if (x < 0 && y < 0) {
            touchAngle += 180;
            //1象限
        } else if (y < 0 && x > 0) {
            touchAngle += 360;
            //3象限
        } else if (y > 0 && x < 0) {
            touchAngle += 180;
        }
        //Math.atan(y/x) 返回正数值表示相对于 x 轴的逆时针转角，返回负数值则表示顺时针转角。
        //返回值乘以 180/π，将弧度转换为角度。
        touchAngle += Math.toDegrees(Math.atan(y / x));
        if (touchAngle < 0) {
            touchAngle = touchAngle + 360;
        }
        return touchAngle;
    }
}

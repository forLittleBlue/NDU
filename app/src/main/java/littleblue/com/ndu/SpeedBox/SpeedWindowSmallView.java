package littleblue.com.ndu.SpeedBox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.reflect.Field;
import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by 10964492 on 2016/11/9.
 */

public class SpeedWindowSmallView extends LinearLayout {
    private String TAG = "SpeedWindowSmallView";

    private int mStatusBarHeight = 0;

    public SpeedWindowSmallView(Context context) {
        super(context);

        mStatusBarHeight = getStatusBarHeight();
        LogNdu.i(TAG, "mStatusBarHeight: " + mStatusBarHeight);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.speed_window_small, this);
//        View view = (LinearLayout) findViewById(R.id.speed_small);
//        view.setAlpha(0.8F);

        TextView speedText = (TextView) view.findViewById(R.id.speed_text);
        speedText.setText("hello");

        int viewWidth = 100;//view.getMeasuredWidth();//view.getLayoutParams().width;
        int viewHeight = 55;//view.getMeasuredHeight();//view.getLayoutParams().height;
        LogNdu.i(TAG, "viewWidth: " + viewWidth + " viewHeight: " + viewHeight);

        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        LogNdu.i(TAG, "screenWidth: " + screenWidth + " screenHeight: " + screenHeight);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // 不阻塞事件传递到后面的窗口
                //| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // 悬浮窗口较小时，后面的应用图标由不可长按变为可长按,弹出的View收不到Back键的事件
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; //不受手机界面限制，比如y值设置为负的时候可以在状态栏上显示


        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        layoutParams.x = screenWidth/3;
        layoutParams.y = -mStatusBarHeight;//screenHeight;

        view.setLayoutParams(layoutParams);
        windowManager.addView(view, layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public static boolean isColorDark(@ColorInt int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    /**
     * Get status bar height
     * @return statusBarHeight
     */
    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}

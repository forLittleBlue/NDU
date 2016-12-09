package littleblue.com.ndu.ViewCustom;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by 10964492 on 2016/12/9.
 */

public class SideSlideView extends RelativeLayout {
    private String TAG = "SideSlideLayout";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mSideSlideView;

    private int mScreenHeight;
    private int mScreenWidth;

    public SideSlideView(Context context) {
        this(context, null);
    }

    public SideSlideView(Context context, AttributeSet attr) {
        super(context, attr);
        initSideSlideView(context);
    }

    private void initSideSlideView(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mSideSlideView = LayoutInflater.from(context).inflate(R.layout.side_slide_vew, this);

        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        mScreenHeight = point.y;
        mScreenWidth = point.x;
        LogNdu.i(TAG, "mScreenWidth: " + mScreenWidth + " mScreenHeight: " + mScreenHeight);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = 100;
        mLayoutParams.height = 100;
        mLayoutParams.x = 0;
        mLayoutParams.y = mScreenHeight/2;
//        mSideSlideView.setLayoutParams(mLayoutParams);
        mWindowManager.addView(mSideSlideView, mLayoutParams);


    }

}

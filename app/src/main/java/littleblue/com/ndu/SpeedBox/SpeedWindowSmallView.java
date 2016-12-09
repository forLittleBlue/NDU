package littleblue.com.ndu.SpeedBox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.DataSaveUtils;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by 10964492 on 2016/11/9.
 */

public class SpeedWindowSmallView extends RelativeLayout {
    private String TAG = "SpeedWindowSmallView";

    private Context mContext;
    private View mSpeedBoxView;
    private TextView mSpeedText;
    private ImageView mSupportView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private int mStatusBarHeight = 0;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    private int mXInBox = 0;
    private int mYInBox = 0;
    private int mBoxInScreenX = 0;
    private int mBoxInScreenY = 0;
    private int mBoxViewWidth = 200;
    private int mBoxViewHeight = 0;

    private final int MSG_DELAY_CHANGE_BOX_VIEW = 1;

    public SpeedWindowSmallView(Context context) {
        super(context);
        mContext = context;
        mStatusBarHeight = getStatusBarHeight();
        mBoxViewHeight = mStatusBarHeight;
        LogNdu.i(TAG, "mStatusBarHeight: " + mStatusBarHeight);
        initSpeedBox(mContext);
    }

    private void initSpeedBox(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mSpeedBoxView = LayoutInflater.from(context).inflate(R.layout.speed_window_small, this);
        //mSpeedBoxView.setAlpha(0.5F);
        mSpeedText = (TextView) mSpeedBoxView.findViewById(R.id.speed_text);
        LayoutParams layoutParams = new LayoutParams(mBoxViewWidth, mBoxViewHeight);
        mSpeedText.setLayoutParams(layoutParams);
        mSpeedText.setText("hello");
        mSupportView = (ImageView) mSpeedBoxView.findViewById(R.id.speed_box_support_view);

        //获取屏幕大小
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
        LogNdu.i(TAG, "mScreenWidth: " + mScreenWidth + " mScreenHeight: " + mScreenHeight);

        mBoxInScreenX = DataSaveUtils.getSpeedBoxX(context);
        mBoxInScreenY = DataSaveUtils.getSpeedBoxY(context);
        //mBoxInScreenY = mScreenHeight/2;
        checkRange();

        //mBoxViewWidth = mSpeedBoxView.getMeasuredWidth();//mSpeedBoxView.getLayoutParams().width;
        //mBoxViewHeight = mSpeedBoxView.getMeasuredHeight();//mSpeedBoxView.getLayoutParams().height;
        LogNdu.i(TAG, "mBoxViewWidth: " + mBoxViewWidth + " mBoxViewHeight: " + mBoxViewHeight);
        addView();
        updateView();
        setViewAlpha(false);

    }

    private void addView() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;//TYPE_SYSTEM_OVERLAY 比 TYPE_PHONE 更高级，可以避免应用程序改变状态栏时被覆盖，但是不能点击滑动
        //mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // 不阻塞事件传递到后面的窗口, 不加可能会导致不响应屏幕其他点击
                //| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // 悬浮窗口较小时，后面的应用图标由不可长按变为可长按,不加会导致屏蔽back键
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; //不受手机界面限制，比如y值设置为负的时候可以在状态栏上显示

        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;//设置起点位置，当mLayoutParams.x=0，mLayoutParams.y=0时，起点是左上角
        mLayoutParams.width = mBoxViewWidth; //宽高必须设置
        mLayoutParams.height = mBoxViewHeight;//这里设置view的实际高度，这时mBoxViewHeight要比view布局里面设置的height要小，所以布局里面height最好设为match_parent
        mLayoutParams.x = mBoxInScreenX;
        mLayoutParams.y = mBoxInScreenY - mStatusBarHeight;//mScreenHeight/2;//-mStatusBarHeight;

//        mSpeedBoxView.setLayoutParams(mLayoutParams);
        mWindowManager.addView(mSpeedBoxView, mLayoutParams);
    }

    private void updateView() {
        checkRange();
        int boxViewHeight = 0;
        if (mBoxInScreenY < mStatusBarHeight) {
            boxViewHeight = mBoxViewHeight + mStatusBarHeight/4*3;
        } else {
            boxViewHeight = mBoxViewHeight;
        }
        mLayoutParams.width = mBoxViewWidth;
        mLayoutParams.height = boxViewHeight;
        mLayoutParams.x = mBoxInScreenX;
        mLayoutParams.y = mBoxInScreenY - mStatusBarHeight;//设置为0时，是在包括状态栏下方的
        mWindowManager.updateViewLayout(mSpeedBoxView, mLayoutParams);

        //ImageView imageView = ((ImageView)mSupportView);
        //Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        //int pixel = bitmap.getPixel(0, 0);
        //LogNdu.i(TAG, "get color: " + pixel);
    }

    public void removeBoxView() {
        mWindowManager.removeView(mSpeedBoxView);
    }

    private void checkRange() {
        if (mBoxInScreenX < 0) {
            mBoxInScreenX = 0;
        }
        if (mBoxInScreenX + mBoxViewWidth > mScreenWidth) {
            mBoxInScreenX = mScreenWidth - mBoxViewWidth;
        }
        if (mBoxInScreenY + mBoxViewHeight > mScreenHeight) {
            mBoxInScreenY = mScreenHeight - mBoxViewHeight;
        }
        if (mBoxInScreenY < 0) {
            mBoxInScreenY = 0;
        }
    }

    private void setViewAlpha(boolean pressed) {
        if (mHandler.hasMessages(MSG_DELAY_CHANGE_BOX_VIEW)) {
            mHandler.removeMessages(MSG_DELAY_CHANGE_BOX_VIEW);
        }
        if (pressed) {
            mSpeedBoxView.setBackgroundColor(getResources().getColor(R.color.glass_grey));
            mSpeedBoxView.setAlpha(0.9F);
            mSupportView.setBackgroundColor(getResources().getColor(R.color.grey_blue));
            mSupportView.setAlpha(0.7F);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_DELAY_CHANGE_BOX_VIEW, 800);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogNdu.i(TAG, "Motion event: " + event.getAction());
        //LogNdu.i(TAG, "xInScreen = " + event.getRawX() + "; yInScreen = " + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXInBox = (int) event.getX();
                mYInBox = (int) event.getY();
                setViewAlpha(true);
                break;
            case MotionEvent.ACTION_MOVE:
                mBoxInScreenX = (int) event.getRawX() - mXInBox;
                mBoxInScreenY = (int) event.getRawY() - mYInBox;
                LogNdu.i(TAG, "mBoxInScreenX: " + mBoxInScreenX + " mBoxInScreenY: " + mBoxInScreenY);
                updateView();
                break;
            case MotionEvent.ACTION_UP:
                setViewAlpha(false);
                DataSaveUtils.saveSpeedBoxX(mContext, mBoxInScreenX);
                DataSaveUtils.saveSpeedBoxY(mContext, mBoxInScreenY);
                break;

        }
        return true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY_CHANGE_BOX_VIEW:
                    mSpeedBoxView.setBackgroundColor(Color.TRANSPARENT);
                    mSpeedBoxView.setAlpha(1F);
                    mSupportView.setAlpha(0.0F);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    public static boolean isColorDark(@ColorInt int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    /**
     * Get status bar height
     *
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

package littleblue.com.ndu.ViewCustom;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.DataSaveUtils;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by 10964492 on 2016/12/9.
 */

public class SideSlideView extends RelativeLayout {
    private String TAG = "SideSlideLayout";

    private Context mContext;
    private WindowManager mWindowManager;
    private Vibrator mVibrator;
    private WindowManager.LayoutParams mLayoutParams;
    private View mSideSlideView;
    private OvalView mOvalView;
    private GestureDetector mGestureDetector;

    private int TYPE_CHANGE_SIZE = 1;
    private int TYPE_CHANGE_POSITHION_MOVE = 2;
    private int TYPE_CHANGE_POSITHION_DONE = 3;
    private int TYPE_CHANGE_ALL = 4;

    private int mScreenHeight;
    private int mScreenWidth;
    private int mSideViewHeight;
    private int mSideViewWidth;
    private int mViewInScreenX;
    private int mViewInScreenY;
    private int mStatusBarHeight;

    private long mStartMoveTime;
    private int mStartMoveX = 0;
    private int mEndMoveX = 0;
    private int mStartMoveY = 0;
    private int mEndMoveY = 0;
    private int mXInSideView = 0;
    private int mYInSideView = 0;
    private int mMovedX = 0;
    private boolean mCanMove = false;

    private float mOvalViewAlpha = 0.2f;
    private float OVAL_VIEW_ALPHA = 0.2f;

    public SideSlideView(Context context) {
        this(context, null);
    }

    public SideSlideView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        if (isLandscapeScreen()) return;
        initSideSlideView(context);
        mGestureDetector = new GestureDetector(context, new MySimpleOnGestureListener());//利用GestureDetector的构造方法传入自定义的SimpleOnGestureListener
    }

    private void initSideSlideView(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        mSideSlideView = LayoutInflater.from(context).inflate(R.layout.side_slide_vew, this);
        mOvalView = (OvalView) findViewById(R.id.side_oval_view);

        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        mScreenHeight = point.y;
        mScreenWidth = point.x;
        mStatusBarHeight = getStatusBarHeight();
        LogNdu.i(TAG, "mScreenWidth: " + mScreenWidth + " mScreenHeight: " + mScreenHeight);

        mSideViewHeight = mScreenHeight / 3;
        mSideViewWidth = mScreenWidth/ 20;

        mViewInScreenX = DataSaveUtils.getSideSlideX(context);
        mViewInScreenY = DataSaveUtils.getSideSlideY(context);
        if(mViewInScreenY == 0) {
            mViewInScreenX = -mSideViewWidth/2;
            mViewInScreenY = mScreenHeight/5 * 2;
        }

        mOvalView.setWidthAndHeight(mSideViewWidth, mSideViewHeight);
        mOvalView.setColor(getResources().getColor(R.color.glass_grey));
        mOvalView.setAlpha(mOvalViewAlpha);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowManager.addView(mSideSlideView, mLayoutParams);
        upateView(mSideViewWidth, mSideViewHeight, mViewInScreenX, mViewInScreenY, TYPE_CHANGE_ALL);
    }

    private void updateViewSize(int width, int height) {
        upateView(width, height, 0, 0, TYPE_CHANGE_SIZE);
    }

    private void updateViewPosition(int x, int y, int type) {
        upateView(0, 0, x, y, type);
    }

    private void upateView(int width, int height, int x, int y, int type) {
        if (TYPE_CHANGE_SIZE == type || TYPE_CHANGE_ALL == type) {
            mLayoutParams.width = width;
            mLayoutParams.height = height;
            //mLayoutParams.x = mViewInScreenX - mMovedX;

           /* if (TYPE_CHANGE_SIZE == type) {
                int newHeight = height;
                if (mMovedX-mSideViewWidth>0) {
//                newHeight= height * (mSideViewWidth/(mMovedX-mSideViewWidth));
                }
                mOvalView.setWidthAndHeight(width, newHeight);
                mOvalView.postInvalidate();
            }*/
        }
        if (TYPE_CHANGE_POSITHION_MOVE == type || TYPE_CHANGE_ALL == type) {
            mLayoutParams.x = x;
            if (mSideViewHeight + y > mScreenHeight) {
                mLayoutParams.y = mScreenHeight - mStatusBarHeight - mSideViewHeight;
            } else if (y < 0) {
                mLayoutParams.y = 0;
            } else {
                mLayoutParams.y = y;
            }
        }
        if (TYPE_CHANGE_POSITHION_MOVE != type) {
            if (x < mScreenWidth/2 - mSideViewWidth/2) {
                mLayoutParams.x = -mSideViewWidth/2;
            } else {
                mLayoutParams.x = mScreenWidth - mSideViewWidth/2;
            }
        }
        mWindowManager.updateViewLayout(mSideSlideView, mLayoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //LogNdu.i(TAG, "Motion event: " + event.getAction());
        LogNdu.i(TAG, "xInScreen = " + event.getRawX() + "; yInScreen = " + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogNdu.i(TAG, "Motion ACTION_DOWN");
                mStartMoveTime = System.currentTimeMillis();
                mStartMoveX = (int) event.getRawX();
                mStartMoveY = (int) event.getRawY();
                mXInSideView = (int) event.getX();
                mYInSideView = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
//                LogNdu.i(TAG, "Motion ACTION_MOVE");
                int currentX = (int) event.getRawX();
                int newMovedX = Math.abs(currentX - mStartMoveX);

                if (!mCanMove && newMovedX > mScreenWidth / 2) {
                    mCanMove = true;
                    mVibrator.vibrate(50);
                }
                if (mCanMove) {
                    mViewInScreenX = currentX - mXInSideView;
                    mViewInScreenY = (int) event.getRawY() - mYInSideView;
                    updateViewPosition(mViewInScreenX, mViewInScreenY, TYPE_CHANGE_POSITHION_MOVE);
                } else {
                    long timeSpend = System.currentTimeMillis() - mStartMoveTime;
                    if (timeSpend > 300) {
                        if (newMovedX - mMovedX > 0) {
                            mOvalViewAlpha += 0.03f;
                        } else if (newMovedX - mMovedX < 0 && mOvalViewAlpha > OVAL_VIEW_ALPHA) {
                            mOvalViewAlpha -= 0.02f;
                        }
                        if (mOvalViewAlpha <= 1){
                            mOvalView.setAlpha(mOvalViewAlpha);
                        } else {
                            mOvalViewAlpha = 1;
                        }
                    }
                }
                mMovedX = newMovedX;
                break;
            case MotionEvent.ACTION_UP:
                LogNdu.i(TAG, "Motion ACTION_UP");
                mEndMoveX = (int) event.getRawX();
                mEndMoveY = (int) event.getRawY();
                if (mCanMove) {
                    mCanMove = false;
                    updateViewPosition(mViewInScreenX, mViewInScreenY, TYPE_CHANGE_POSITHION_DONE);
                    DataSaveUtils.saveSideSlideX(mContext, mViewInScreenX);
                    DataSaveUtils.saveSideSlideY(mContext, mViewInScreenY);
                } else {
                    doKeyType();
                    mMovedX = 0;
                }
                if (mOvalViewAlpha != OVAL_VIEW_ALPHA) {
                    mOvalViewAlpha = OVAL_VIEW_ALPHA;
                    mOvalView.setAlpha(mOvalViewAlpha);
                }
                break;
        }
//        mGestureDetector.onTouchEvent(event);//必须要加这句才会触发MySimpleOnGestureListener里事件
        return true;
    }

    /**
     *
     */
    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogNdu.i(TAG, " onFling: e1: " + e1.getAction() + " e2: " + e2.getAction()
                + " velocityX: " + velocityX + " velocityY: " + velocityY);
//            doKeyType();
            return true;
        }

       /* @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogNdu.i(TAG, " onScroll: e1: " + e1.getAction() + " e2: " + e2.getAction()
                    + " distanceX: " + distanceX + " distanceY: " + distanceY);
            return true;
        }*/

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            LogNdu.i(TAG, "onDoubleTap e: " + e.getAction());
            //keyHome();
            return true;
        }

        /*@Override
        public void onShowPress(MotionEvent e) {
            LogNdu.i(TAG, "onShowPress e: " + e.getAction());
        }*/

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
    };

    private void doKeyType() {
        int xDistance = mEndMoveX - mStartMoveX;
        int yDistance = mEndMoveY - mStartMoveY;
        LogNdu.i(TAG, "xDistance: " + xDistance + " yDistance: " + yDistance);
        int xThreshold = mScreenWidth/10;
        int yThreshold = mScreenWidth/12;
        int xDistanceAbs = Math.abs(xDistance);
        int yDistanceAbs = Math.abs(yDistance);

        int angle = (int) (90 * Math.atan2(xDistanceAbs, yDistanceAbs));//以按下点为
        LogNdu.i(TAG, "angle = " + angle);
        if (yDistance > yThreshold && angle < 30) {
            //Down fling
            LogNdu.i(TAG, "doKeyType keyHome");
            keyHome();
        } else if (yDistance < 0 && angle < 50) {
            //Up fling
            LogNdu.i(TAG, "doKeyType keyMenu");
            keyMenu();
        } else if (xDistanceAbs > 0) {
            LogNdu.i(TAG, "doKeyType keyBack");
            keyBack();
        }
    }

    public void keyBack(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                }
                catch (Exception e) {
                    LogNdu.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }

    public void keyHome(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
                }
                catch (Exception e) {
                    LogNdu.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }

    public void keyMenu(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
                }
                catch (Exception e) {
                    LogNdu.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            LogNdu.i(TAG, "Pressed a while");
            //updateViewPosition(0, mViewInScreenY);
            mVibrator.vibrate(100);
            mCanMove = true;
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //LogNdu.i(TAG, "onMeasure getWidth: " + getWidth() + " getHeight: " + getHeight());

    }

    public void removeSideSlideView() {
        if (mWindowManager != null) {
            mWindowManager.removeView(mSideSlideView);
        }
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

    private boolean isLandscapeScreen() {
        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            LogNdu.i(TAG, "is ORIENTATION_LANDSCAPE");
            return true;
        } else {
            return false;
        }
    }


}

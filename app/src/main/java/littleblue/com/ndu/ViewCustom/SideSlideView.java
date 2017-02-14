package littleblue.com.ndu.ViewCustom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.DataSaveUtils;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by 10964492 on 2016/12/9.
 */

public class SideSlideView extends LinearLayout {
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

    private long mMoveStartTime;
    private int mStartPointX = 0;
    private int mEndPointX = 0;
    private int mStartPointY = 0;
    private int mEndPointY = 0;
    private int mXInSideView = 0;
    private int mYInSideView = 0;
    private int mLastOrientation;
    private long mMovedMaxDistanceX = 0;
    private boolean mCanMove = false;
    private boolean mNeedFeedback = true;
    private boolean isSlideBarOnLeft = true;

    private float mOvalViewAlpha;
    private float OVAL_VIEW_ALPHA = 0.3f;

    public SideSlideView(Context context) {
        this(context, null);
    }

    public SideSlideView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        if (isLandscapeScreen()) return;
        //mSideSlideLauncherView = new SideSlideLauncherView(mContext);
        initSideSlideView(context);
        mGestureDetector = new GestureDetector(context, new MySimpleOnGestureListener());//利用GestureDetector的构造方法传入自定义的SimpleOnGestureListener
        mLastOrientation = context.getResources().getConfiguration().orientation;
    }

    /**
     * 监听屏幕方向变化，横屏时取消bar显示
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mLastOrientation != newConfig.orientation) {
            mLastOrientation = newConfig.orientation;
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LogNdu.i(TAG, "onConfigurationChanged now is ORIENTATION_LANDSCAPE");
                //updateViewSize(0, 0);
                mLayoutParams.x = -mSideViewWidth;
                mWindowManager.updateViewLayout(mSideSlideView, mLayoutParams);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                LogNdu.i(TAG, "onConfigurationChanged now is ORIENTATION_PORTRAIT");
                //updateViewSize(mSideViewWidth, mSideViewHeight);
                mLayoutParams.x = mViewInScreenX;
                mWindowManager.updateViewLayout(mSideSlideView, mLayoutParams);
            }
        }
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

        mSideViewHeight = mScreenHeight / 4;
        mSideViewWidth = mScreenWidth/ 17;

        mNeedFeedback = DataSaveUtils.getKeyNeedFeedback(context);
        mViewInScreenX = DataSaveUtils.getSideSlideX(context);
        mViewInScreenY = DataSaveUtils.getSideSlideY(context);
        isSlideBarOnLeft = DataSaveUtils.getSlideBarIsOnLeft(context);
        if(mViewInScreenY == 0) {
            mViewInScreenX = -mSideViewWidth/2;
            mViewInScreenY = mScreenHeight/5 * 2;
        }

        mOvalViewAlpha = OVAL_VIEW_ALPHA;
        mOvalView.setWidthAndHeight(mSideViewWidth, mSideViewHeight);
        mOvalView.setColor(getResources().getColor(R.color.glass_grey));
        mOvalView.setAlpha(mOvalViewAlpha);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (!isSlideBarOnLeft) {
            mLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        } else {
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        }
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
        if (TYPE_CHANGE_POSITHION_MOVE != type
                && TYPE_CHANGE_SIZE != type) {
            if (isSlideBarOnLeft) {
                if (x > mScreenWidth/2 - mSideViewWidth/2) {
                    isSlideBarOnLeft = false;
                    mLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
                }
            } else {
                if (x > mScreenWidth/2 - mSideViewWidth/2) {
                    isSlideBarOnLeft = true;
                    mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                }
            }
            //以上根据在左边还是右边设置不同的起点
            mLayoutParams.x = mViewInScreenX = -mSideViewWidth/2;
        }
        mWindowManager.updateViewLayout(mSideSlideView, mLayoutParams);
        DataSaveUtils.saveSlideBarIsOnLeft(mContext, isSlideBarOnLeft);
    }

    private  void animationKeyBack(boolean isMoreBig) {
        LogNdu.i(TAG, "animationKeyBack ");

        if (isSlideBarOnLeft) {
            ObjectAnimator translationX = ObjectAnimator.ofFloat(mOvalView, "translationX", 0, -mSideViewWidth/2, 0);
            translationX.setDuration(300).start();
        } else {
            ObjectAnimator translationX = ObjectAnimator.ofFloat(mOvalView, "translationX", 0, mSideViewWidth/2, 0);
            translationX.setDuration(300).start();
        }

        //mWindowManager.updateViewLayout(mSideSlideView, mLayoutParams);
    }
    private  void animationKeyHome() {
        LogNdu.i(TAG, "animationKeyHome ");

        if (isSlideBarOnLeft) {
            ObjectAnimator translationX = ObjectAnimator.ofFloat(mOvalView, "translationX", 0, -mSideViewWidth/2, 0);
            //translationX.setRepeatCount(1);
            translationX.setDuration(200).start();
        } else {
            ObjectAnimator translationX = ObjectAnimator.ofFloat(mOvalView, "translationX", 0, mSideViewWidth/2, 0);
            //translationX.setRepeatCount(1);
            translationX.setDuration(200).start();
        }
    }

    private SideSlideLauncherView mSideSlideLauncherView = null;
    private boolean isStartedAnimation = false;
    private int Double_Slide_Time = 250;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //LogNdu.i(TAG, "Motion event: " + event.getAction());
//        LogNdu.i(TAG, "xInScreen = " + event.getRawX() + "; yInScreen = " + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogNdu.i(TAG, "Motion ACTION_DOWN");
                mMoveStartTime = System.currentTimeMillis();
                mStartPointX = isSlideBarOnLeft ? (int) event.getRawX() : mScreenWidth - (int) event.getRawX();
                mStartPointY = (int) event.getRawY();
                mXInSideView = (int) event.getX();
                mYInSideView = (int) event.getY();
                mMovedMaxDistanceX = 0;
                /*if (mMoveStartTime - mMoveEndTime < Double_Slide_Time && !isDoubleSlide) {
                    if (mLastKeyType == KeyEvent.KEYCODE_BACK) {
                        isDoubleSlide = true;
                        removeCallbacks(mKeyBackRunnable);
                        //keyHome();
                        postDelayed(mKeyHomeRunnable, 200);
                        animationKeyHome();
                    }
                } else {
                    isDoubleSlide = false;
                }*/
                //LogNdu.i(TAG, "isDoubleSlide = " + isDoubleSlide);
                break;
            case MotionEvent.ACTION_MOVE:
//                LogNdu.i(TAG, "Motion ACTION_MOVE");
                int currentX = isSlideBarOnLeft ? (int) event.getRawX() : mScreenWidth - (int) event.getRawX();;
                int newMovedX = Math.abs(currentX - mStartPointX);
                //mMovedMaxDistanceX = newMovedX - mMovedMaxDistanceX > 0 ? newMovedX : mMovedMaxDistanceX;
                //LogNdu.i(TAG, "mMovedMaxDistanceX: " + mMovedMaxDistanceX);
                if (!mCanMove
                        && newMovedX > (mScreenWidth/2 - mSideViewWidth/2) //滑动超过屏幕一半宽度
                        ) {
                    mCanMove = true;
                    mVibrator.vibrate(50);
                }
                if (mCanMove) {
                    mViewInScreenX = currentX - mXInSideView;
                    mViewInScreenY = (int) event.getRawY() - mYInSideView;
                    updateViewPosition(mViewInScreenX, mViewInScreenY, TYPE_CHANGE_POSITHION_MOVE);
                }
                break;
            case MotionEvent.ACTION_UP:
                LogNdu.i(TAG, "Motion ACTION_UP");
                mEndPointX = isSlideBarOnLeft ? (int) event.getRawX() : mScreenWidth - (int) event.getRawX();;
                mEndPointY = (int) event.getRawY();
                mMoveEndTime = System.currentTimeMillis();
                if (mCanMove) {
                    mCanMove = false;
                    updateViewPosition(mViewInScreenX, mViewInScreenY, TYPE_CHANGE_POSITHION_DONE);
                    DataSaveUtils.saveSideSlideX(mContext, mViewInScreenX);
                    DataSaveUtils.saveSideSlideY(mContext, mViewInScreenY);
                } else {
                    if (isStartedAnimation) {
                        isStartedAnimation = false;
                    }
                    if (isDoubleSlide) {
                        LogNdu.i(TAG, "isDoubleSlide: " + isDoubleSlide);
                        isDoubleSlide = false;
                    } else {
                        doKeyType();
                    }
                }
                break;
        }
//        mGestureDetector.onTouchEvent(event);//必须要加这句才会触发MySimpleOnGestureListener里事件
        return true;
    }

    private long mMoveEndTime = 0;
    private boolean isDoubleSlide = false;
    private int mLastKeyType = -1;
    private void doKeyType() {
        if (mMoveEndTime - mMoveStartTime > 800) {
            return;
        }
        int xDistance = mEndPointX - mStartPointX;
        int yDistance = mEndPointY - mStartPointY;
        //LogNdu.i(TAG, "xDistance: " + xDistance + " yDistance: " + yDistance);
        int xThreshold = mScreenWidth/10;
        int yThreshold = mScreenWidth/16;
        int xDistanceAbs = Math.abs(xDistance);
        int yDistanceAbs = Math.abs(yDistance);

        int angle = (int) (90 * Math.atan2(xDistanceAbs, yDistanceAbs));//以按下点为
        //LogNdu.i(TAG, "angle = " + angle);
        if (yDistance > 0 && angle < 20) {
            //Down fling
            mLastKeyType = KeyEvent.KEYCODE_MENU;
            keyMenu();
        } else if (yDistance < 0 && angle < 50) {
            //Up fling
            mLastKeyType = KeyEvent.KEYCODE_HOME;
            keyHome();
        } else if (xDistanceAbs > 0) {
            mLastKeyType = KeyEvent.KEYCODE_BACK;
            postDelayed(mKeyBackRunnable, Double_Slide_Time);
            animationKeyBack(true);
        } else {
            mLastKeyType = -1;
        }
    }

    private Runnable mKeyBackRunnable = new Runnable() {
        @Override
        public void run() {
            LogNdu.i(TAG, "mKeyBackRunnable");
            keyBack();
//            animationKeyBack(false);
        }
    };

    private Runnable mKeyHomeRunnable = new Runnable() {
        @Override
        public void run() {
            LogNdu.i(TAG, "mKeyBackRunnable");
            keyHome();
        }
    };

    public void keyBack(){
        new Thread(){
            public void run() {
                try{
                    LogNdu.i(TAG, "doKeyType keyBack");
                    shotVibrate();
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    /*String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
                    Runtime runtime = Runtime.getRuntime();
                    Process proc = runtime.exec(keyCommand);*/
                } catch (Exception e) {
                    LogNdu.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }

    public void keyHome(){
        new Thread(){
            public void run() {
                try{
                    LogNdu.i(TAG, "doKeyType keyHome");
                    shotVibrate();
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
                } catch (Exception e) {
                    LogNdu.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }

    public void keyMenu(){
        new Thread(){
            public void run() {
                try{
                    LogNdu.i(TAG, "doKeyType keyMenu");
                    shotVibrate();
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
                } catch (Exception e) {
                    LogNdu.e("Exception when onBack", e.toString());
                }
            }
        }.start();
    }

    private void shotVibrate() {
        if (mNeedFeedback) {
            mVibrator.vibrate(30);
        }
    }

    public void setNeedFeedback() {
        mNeedFeedback = DataSaveUtils.getKeyNeedFeedback(mContext);
    }

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
}

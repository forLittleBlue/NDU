package littleblue.com.ndu.ViewCustom;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

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

    private int mScreenHeight;
    private int mScreenWidth;
    private int mSideViewHeight;
    private int mSideViewWidth;
    private int mViewInScreenX;
    private int mViewInScreenY;
    private int TYPE_CHANGE_SIZE = 1;
    private int TYPE_CHANGE_POSITHION_MOVE = 2;
    private int TYPE_CHANGE_POSITHION_DONE = 3;
    private int TYEP_CHANGE_ALL = 4;


    public SideSlideView(Context context) {
        this(context, null);
    }

    public SideSlideView(Context context, AttributeSet attr) {
        super(context, attr);
        initSideSlideView(context);
        mGestureDetector = new GestureDetector(context, new MySimpleOnGestureListener());//利用GestureDetector的构造方法传入自定义的SimpleOnGestureListener
    }

    private void initSideSlideView(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        mSideSlideView = LayoutInflater.from(context).inflate(R.layout.side_slide_vew, this);
        mOvalView = (OvalView) findViewById(R.id.side_oval_view);

        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        mScreenHeight = point.y;
        mScreenWidth = point.x;
        LogNdu.i(TAG, "mScreenWidth: " + mScreenWidth + " mScreenHeight: " + mScreenHeight);

        mSideViewHeight = mScreenHeight / 3;
        mSideViewWidth = mScreenWidth/ 18;

        mViewInScreenX = DataSaveUtils.getSideSlideX(context);
        mViewInScreenY = DataSaveUtils.getSideSlideY(context);
        if(mViewInScreenY == 0) {
            mViewInScreenX = -mSideViewWidth/2;
            mViewInScreenY = mScreenHeight/2;
        }

        mOvalView.setWidthAndHeight(mSideViewWidth, mSideViewHeight);
        mOvalView.setColor(getResources().getColor(R.color.glass_grey));
        mOvalView.setAlpha(0.5f);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowManager.addView(mSideSlideView, mLayoutParams);
        upateView(mSideViewWidth, mSideViewHeight, mViewInScreenX, mViewInScreenY, TYEP_CHANGE_ALL);
    }

    private void updateViewSize(int width, int height) {
        upateView(width, height, 0, 0, TYPE_CHANGE_SIZE);
    }

    private void updateViewPosition(int x, int y, int type) {
        upateView(0, 0, x, y, type);
    }

    private void upateView(int width, int height, int x, int y, int type) {
        if (TYPE_CHANGE_SIZE == type || TYEP_CHANGE_ALL == type) {
            mLayoutParams.width = width;
            mLayoutParams.height = height;
            mLayoutParams.x = mViewInScreenX - mMovedX;

            int newHeight = height;
            if (mMovedX-mSideViewWidth>0) {
//                newHeight= height * (mSideViewWidth/(mMovedX-mSideViewWidth));
            }
            mOvalView.setWidthAndHeight(width, newHeight);
            mOvalView.postInvalidate();
        }
        if (TYPE_CHANGE_POSITHION_MOVE == type || TYEP_CHANGE_ALL == type) {
            mLayoutParams.x = x;
            if (mSideViewHeight + y > mScreenHeight) {
                mLayoutParams.y = mScreenHeight - mSideViewHeight;
            } else if (y < 0) {
                mLayoutParams.y = 0;
            } else {
                mLayoutParams.y = y;
            }
        }
        if (TYPE_CHANGE_POSITHION_DONE == type || TYEP_CHANGE_ALL == type) {
            if (x < mScreenWidth/2) {
                mLayoutParams.x = -mSideViewWidth/2;
            } else {
                mLayoutParams.x = mScreenWidth - mSideViewWidth/2;
            }
        }
        mWindowManager.updateViewLayout(mSideSlideView, mLayoutParams);
    }

    private int mStartMoveX = 0;
    private int mXInSideView = 0;
    private int mYInSideView = 0;
    private int mMovedX = 0;
    private boolean mCanMove = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //LogNdu.i(TAG, "Motion event: " + event.getAction());
        LogNdu.i(TAG, "xInScreen = " + event.getRawX() + "; yInScreen = " + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogNdu.i(TAG, "Motion ACTION_DOWN");
                mStartMoveX = (int) event.getRawX();
                mXInSideView = (int) event.getX();
                mYInSideView = (int) event.getY();
                postDelayed(mRunnable, 1500);
                break;
            case MotionEvent.ACTION_MOVE:
//                LogNdu.i(TAG, "Motion ACTION_MOVE");
                int currentX = (int) event.getRawX();
                mMovedX = Math.abs(currentX - mStartMoveX);
                if (mMovedX > mSideViewWidth && !mCanMove) {
                    removeCallbacks(mRunnable);
                }
                if (mCanMove) {
                    mViewInScreenX = currentX - mXInSideView;
                    mViewInScreenY = (int) event.getRawY() - mYInSideView;
                    updateViewPosition(mViewInScreenX, mViewInScreenY, TYPE_CHANGE_POSITHION_MOVE);
                } else {
                    updateViewSize(mSideViewWidth + 2*mMovedX, mSideViewHeight);
                }
                break;
            case MotionEvent.ACTION_UP:
                LogNdu.i(TAG, "Motion ACTION_UP");
                if (mCanMove) {
                    mCanMove = false;
                    updateViewPosition(mViewInScreenX, mViewInScreenY, TYPE_CHANGE_POSITHION_DONE);
                    DataSaveUtils.saveSideSlideX(mContext, mViewInScreenX);
                    DataSaveUtils.saveSideSlideY(mContext, mViewInScreenY);
                } else {
                    mMovedX = 0;
                    updateViewSize(mSideViewWidth, mSideViewHeight);
                }
                removeCallbacks(mRunnable);
                break;
        }
        if (!mCanMove) mGestureDetector.onTouchEvent(event);//必须要加这句才会触发MySimpleOnGestureListener里事件
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
            return true;
        }

        /*@Override
        public void onShowPress(MotionEvent e) {
            LogNdu.i(TAG, "onShowPress e: " + e.getAction());
        }*/
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            LogNdu.i(TAG, "Pressed a while");
            //updateViewPosition(0, mViewInScreenY);
            mVibrator.vibrate(1);
            mCanMove = true;
        }
    };

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                LogNdu.i(TAG, "onGenericMotionEvent ACTION_HOVER_ENTER");
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                LogNdu.i(TAG, "onGenericMotionEvent ACTION_HOVER_EXIT");
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                LogNdu.i(TAG, "onGenericMotionEvent ACTION_HOVER_MOVE");
                break;
            case MotionEvent.ACTION_SCROLL:
                int axis = (int) event.getRawX();
                LogNdu.i(TAG, "onGenericMotionEvent ACTION_SCROLL " + event.getAxisValue(axis));
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //LogNdu.i(TAG, "onMeasure getWidth: " + getWidth() + " getHeight: " + getHeight());

    }

    public void removeSideSlideView() {
        mWindowManager.removeView(mSideSlideView);
    }

}

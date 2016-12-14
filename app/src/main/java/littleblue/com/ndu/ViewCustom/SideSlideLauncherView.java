package littleblue.com.ndu.ViewCustom;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by 10964492 on 2016/12/14.
 */

public class SideSlideLauncherView extends RelativeLayout {
    private String TAG = "SideSlideLauncher";

    private Context mContext;
    private WindowManager mWindowManager;
    private View mSideSlideLauncherView;
    private WindowManager.LayoutParams mLayoutParams;

    private int mViewWidth = 500;
    private int mViewHeight = 1500;
    private int mViewInscreenX = 0;
    private float mViewAlpha = 0f;

    public SideSlideLauncherView(Context context) {
        this(context, null);
    }

    public SideSlideLauncherView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        initView();
    }

    private void initView() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mSideSlideLauncherView = LayoutInflater.from(mContext).inflate(R.layout.side_slide_launcher_layout, this);

        Button button = (Button) mSideSlideLauncherView.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogNdu.i(TAG, "button is click");
            }
        });

        mViewInscreenX = -mViewWidth;
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.width = mViewWidth;
        mLayoutParams.height = mViewHeight;
        mLayoutParams.x = mViewInscreenX;
        mLayoutParams.y = 0;
        mWindowManager.addView(mSideSlideLauncherView, mLayoutParams);
    }

    public void updateViewX(boolean isGoRight) {
        if (isGoRight && mViewInscreenX <= 0) {
            if (mViewAlpha < 1) mViewAlpha += 0.02f;
            mSideSlideLauncherView.setAlpha(mViewAlpha);

            mViewInscreenX += 20;
            mLayoutParams.x = mViewInscreenX;
            mWindowManager.updateViewLayout(mSideSlideLauncherView, mLayoutParams);
        } else if (mViewInscreenX > -mViewWidth) {
            if (mViewAlpha > 0) mViewAlpha -= 0.02f;
            if (mViewInscreenX <= -mViewWidth) mViewAlpha = 0.1f;
            mSideSlideLauncherView.setAlpha(mViewAlpha);

            mSideSlideLauncherView.setAlpha(mViewAlpha);
            mViewInscreenX -= 20;
            mLayoutParams.x = mViewInscreenX;
            mWindowManager.updateViewLayout(mSideSlideLauncherView, mLayoutParams);
        }
    }

    public void disappearView() {
        mViewInscreenX = -mViewWidth;
        mLayoutParams.x = mViewInscreenX;
        mWindowManager.updateViewLayout(mSideSlideLauncherView, mLayoutParams);
    }

    public void removeLauncherView() {
        if (mWindowManager != null) {
            mViewInscreenX = 0;
            mWindowManager.removeView(mSideSlideLauncherView);
        }
    }
}

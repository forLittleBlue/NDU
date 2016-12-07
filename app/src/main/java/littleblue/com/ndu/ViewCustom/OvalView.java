package littleblue.com.ndu.ViewCustom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import littleblue.com.ndu.R;
import littleblue.com.ndu.Utils.LogNdu;

/**
 * Created by xieqingyu on 2016/12/7.
 */

public class OvalView extends View {
    private String TAG = "OvalView";

    public OvalView(Context context) {
        this(context, null);
        LogNdu.i(TAG, "OvalView(context)");
    }

    public OvalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        LogNdu.i(TAG, "OvalView(context, attrs)");
    }

    public OvalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogNdu.i(TAG, "OvalView(context, attrs, defStyleAttr)");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        RectF rectF = new RectF();//矩形
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = 100;
        rectF.bottom = 300;

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.glass_grey));
        paint.setStrokeWidth(5);
        canvas.drawOval(rectF, paint);
//        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
    }
}

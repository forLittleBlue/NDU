package littleblue.com.ndu.ViewCustom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by 10964492 on 2016/12/1.
 */

public class OvalLayout extends ViewGroup {
    private String TAG = "OvalLayout";

    public OvalLayout(Context context) {
        this(context, null);
    }

    public OvalLayout(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}

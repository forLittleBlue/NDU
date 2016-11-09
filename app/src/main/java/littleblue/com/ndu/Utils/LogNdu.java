package littleblue.com.ndu.Utils;

import android.util.Log;

/**
 * Created by 10964492 on 2016/11/9.
 */

public class LogNdu {
    private static String TAG = "NDU";

    public static void i(String tag, String msg) {
        Log.i(TAG + "." + tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(TAG + "." + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG + "." + tag, msg);
    }
}

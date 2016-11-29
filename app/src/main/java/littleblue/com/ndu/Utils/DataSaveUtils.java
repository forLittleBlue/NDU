package littleblue.com.ndu.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 10964492 on 2016/11/28.
 */

public class DataSaveUtils {
    private String TAG = "DataSaveUtils";

    private static String PRE = "DataSaveUtils";
    private static String Key_Speed_Box_X = "Key_Speed_Box_X";
    private static String Key_Speed_Box_Y = "Key_Speed_Box_Y";

    public static void saveSpeedBoxX(Context context, int x) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Key_Speed_Box_X, x);
        editor.commit();
    }

    public static int getSpeedBoxX(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        int value = preferences.getInt(Key_Speed_Box_X, 0);
        return value;
    }

    public static void saveSpeedBoxY(Context context, int y) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Key_Speed_Box_Y, y);
        editor.commit();
    }

    public static int getSpeedBoxY(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        int value = preferences.getInt(Key_Speed_Box_Y, 0);
        return value;
    }
}

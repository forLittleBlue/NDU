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
    private static String Key_Side_Slide_X = "Key_Side_Slide_X";
    private static String Key_Side_Slide_Y = "Key_Side_Slide_Y";
    private static String Key_Need_Feedback = "Key_Need_Feedback";

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

    public static void saveSideSlideX(Context context, int x) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Key_Side_Slide_X, x);
        editor.commit();
    }

    public static int getSideSlideX(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        int value = preferences.getInt(Key_Side_Slide_X, 0);
        return value;
    }

    public static void saveSideSlideY(Context context, int y) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Key_Side_Slide_Y, y);
        editor.commit();
    }

    public static int getSideSlideY(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        int value = preferences.getInt(Key_Side_Slide_Y, 0);
        return value;
    }

    public static void saveKeyNeedFeedback(Context context, boolean y) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Key_Need_Feedback, y);
        editor.commit();
    }

    public static boolean getKeyNeedFeedback(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE, Context.MODE_PRIVATE);
        boolean value = preferences.getBoolean(Key_Need_Feedback, true);
        return value;
    }

}

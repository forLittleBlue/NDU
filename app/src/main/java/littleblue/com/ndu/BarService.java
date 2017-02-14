package littleblue.com.ndu;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.view.WindowManager;

import littleblue.com.ndu.SpeedBox.SpeedWindowSmallView;
import littleblue.com.ndu.Utils.DataSaveUtils;
import littleblue.com.ndu.Utils.FakeService;
import littleblue.com.ndu.Utils.LogNdu;
import littleblue.com.ndu.ViewCustom.SideSlideView;

public class BarService extends Service {
    private static final String TAG = "BarService";

    private SideSlideView mSideSlideView;
    private SpeedWindowSmallView mSpeedWindowSmallView;
    private SharedPreferences mSharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSideSlideView = new SideSlideView(this);

        //mSpeedWindowSmallView = new SpeedWindowSmallView(this);//网速box

        mSharedPreferences = getSharedPreferences(DataSaveUtils.PRE, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mPreChangeListener);

    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPreChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            LogNdu.i(TAG, "onSharedPreferenceChanged key: " + key);
            if (key == DataSaveUtils.Key_Need_Feedback) {
                mSideSlideView.setNeedFeedback();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogNdu.i(TAG, "onStartCommand");
        startForeground(FakeService.NOTIFY_ID, new Notification());
        startService(new Intent(this, FakeService.class));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSideSlideView.removeSideSlideView();
        //mSpeedWindowSmallView.removeBoxView();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mPreChangeListener);
        super.onDestroy();
    }
}

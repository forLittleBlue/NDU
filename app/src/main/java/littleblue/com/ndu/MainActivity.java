package littleblue.com.ndu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;

import littleblue.com.ndu.SpeedBox.SpeedWindowSmallView;
import littleblue.com.ndu.Utils.DataSaveUtils;
import littleblue.com.ndu.Utils.LogNdu;
import littleblue.com.ndu.ViewCustom.SideSlideView;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        final Switch vibrateCheck = (Switch) findViewById(R.id.switch_open_vibrate);
        boolean isVibrateChecked = DataSaveUtils.getKeyNeedFeedback(mContext);
        vibrateCheck.setChecked(isVibrateChecked);
        vibrateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogNdu.i(TAG, "vibrateCheck clicked: " + vibrateCheck.isChecked());
                if (vibrateCheck.isChecked()) {
                    DataSaveUtils.saveKeyNeedFeedback(mContext, true);
                } else {
                    DataSaveUtils.saveKeyNeedFeedback(mContext, false);
                }
            }
        });

        startService(new Intent(this, BarService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

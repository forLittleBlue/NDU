package littleblue.com.ndu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import littleblue.com.ndu.SpeedBox.SpeedWindowSmallView;
import littleblue.com.ndu.ViewCustom.SideSlideView;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    private SpeedWindowSmallView mSpeedWindowSmallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

//        mSpeedWindowSmallView = new SpeedWindowSmallView(this);
        new SideSlideView(this);
    }

    @Override
    protected void onDestroy() {
        //mSpeedWindowSmallView.removeBoxView();
        super.onDestroy();
    }

}

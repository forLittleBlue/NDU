package littleblue.com.ndu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import littleblue.com.ndu.SpeedBox.SpeedWindowSmallView;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        SpeedWindowSmallView speedWindowSmallView = new SpeedWindowSmallView(this);
    }
}

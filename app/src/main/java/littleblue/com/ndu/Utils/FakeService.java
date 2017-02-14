package littleblue.com.ndu.Utils;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FakeService extends Service {
    public static int NOTIFY_ID = 100101;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFY_ID, new Notification());
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}

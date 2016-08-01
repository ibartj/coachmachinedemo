package demo.ibartj.coachmachine.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import demo.ibartj.coachmachine.MainActivity;

/**
 * Receives notification on connectivity change (network available or unavailable).
 *
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent updateIntent = new Intent(MainActivity.ACTION_CONNECTIVITY_CHANGE);
        context.sendBroadcast(updateIntent);
    }
}
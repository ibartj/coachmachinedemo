package demo.ibartj.coachmachine;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
@ReportsCrashes(formUri = "https://collector.tracepot.com/6f31c6ab")
public class CoachMachineApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.detectAll()
				.penaltyFlashScreen()
				.penaltyLog()
				.build();
		StrictMode.setThreadPolicy(policy);*/
        AppContext.initialize(getApplicationContext());
        ACRA.init(this);
    }
}

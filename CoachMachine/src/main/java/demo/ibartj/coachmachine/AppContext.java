package demo.ibartj.coachmachine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.letsgood.synergykitsdkandroid.Synergykit;
import demo.ibartj.coachmachine.dao.LocalStorageHelper;
import demo.ibartj.coachmachine.exceptions.NoAppContextException;
import demo.ibartj.coachmachine.service.WorkoutService;

import java.util.Locale;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class AppContext {
    private static AppContext instance = null;
    public static final String LOG_TAG = "CoachMachine app";
    private static final String SYNERGY_APP_TENANT = "coach-machine-z9f6r";
    private static final String SYNERGY_APP_KEY = "b15b90cf-98a2-4844-bd3c-ff3bb5caf6a0";

    private Context context;
    private LocalStorageHelper localStorageHelper;
    private WorkoutService workoutService;

    private AppContext(Context context) {
        this.context = context;
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new AppContext(context);
            instance.initialize();
        }
    }

    private void initialize() {
        localStorageHelper = new LocalStorageHelper(context);
        workoutService = new WorkoutService();
        Synergykit.init(SYNERGY_APP_TENANT, SYNERGY_APP_KEY);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            throw new NoAppContextException();
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public SQLiteDatabase getDB() throws SQLiteException {
        SQLiteDatabase db = localStorageHelper.getWritableDatabase();
        db.setLocale(new Locale("cz_CZ"));
        return db;
    }

    public void closeDB(SQLiteDatabase db) throws SQLiteException {
        localStorageHelper.closeDatabase(db);
    }

    public void closeLocalStorageHelper() {
        localStorageHelper.close();
    }

    public WorkoutService getWorkoutService() {
        return workoutService;
    }
}

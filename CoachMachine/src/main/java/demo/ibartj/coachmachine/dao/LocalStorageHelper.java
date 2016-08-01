package demo.ibartj.coachmachine.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class LocalStorageHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    public LocalStorageHelper(Context context) {
        super(context, "CoachMachine", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        new WorkoutSqliteDbDao().createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    private int connectionCounter = 0;

    public synchronized void closeDatabase(SQLiteDatabase db) throws SQLiteException {
        if (--connectionCounter == 0 && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() throws SQLiteException {
        SQLiteDatabase db = super.getWritableDatabase();
        connectionCounter++;
        return db;
    }
}

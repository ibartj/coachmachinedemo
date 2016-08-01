package demo.ibartj.coachmachine.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import demo.ibartj.coachmachine.dao.util.DbColumn;
import demo.ibartj.coachmachine.dao.util.DbType;
import demo.ibartj.coachmachine.model.Workout;

import java.util.List;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class WorkoutSqliteDbDao extends AbstractSqliteDbDao<Workout> {
    @Override
    String getTable() {
        return "workouts";
    }

    @Override
    DbColumn[] getColumnDefinitions() {
        return new DbColumn[]{
                new DbColumn("_id", DbType.ID, "Id", String.class),
                new DbColumn("title", DbType.TEXT, "Title", String.class),
                new DbColumn("place", DbType.TEXT, "Place", String.class),
                new DbColumn("duration", DbType.INT, "Duration", Integer.class),
                new DbColumn("created_at", DbType.INT, "CreatedAt", Long.class),
                new DbColumn("user", DbType.TEXT, "User", String.class)
        };
    }

    @Override
    public void createTable(SQLiteDatabase database) {
        super.createTable(database);
    }

    public List<Workout> getByUsername(String username) {
        CursorTemplate<Workout> c = new CursorTemplate<Workout>() {
            @Override
            public void doExecute(Cursor cursor) {
                result(fromCursor(cursor));
            }
        };
        return c.get(this, Workout.class, "user=?", new String[]{username}, "created_at");
    }
}

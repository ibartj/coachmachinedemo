package demo.ibartj.coachmachine.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import demo.ibartj.coachmachine.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
@SuppressWarnings("unused")
abstract class CursorTemplate<T> {
    protected SQLiteDatabase database;
    protected List<T> result;
    protected HashMap<T, Integer> keymap;

    public abstract void doExecute(Cursor cursor);

    public void result(T object) {
        if (this.result != null) {
            this.result.add(object);
        }
    }

    public void keymap(Integer key, T object) {
        if (this.keymap != null) {
            this.keymap.put(object, key);
        }
    }

    public T getOne(AbstractSqliteDbDao<T> daObject, Class<T> daClass, String selection, String[] selectionArgs) {
        return getOne(daObject, daClass, selection, selectionArgs, null);
    }

    public T getOne(AbstractSqliteDbDao<T> daObject, Class<T> daClass, String selection, String[] selectionArgs, String orderBy) {
        try {
            Set keys = daObject.getContentValues(daClass.newInstance()).keySet();
            String[] values = new String[keys.size()];
            values = (String[]) keys.toArray(values);
            return getOne(daObject.getTable(), values, selection, selectionArgs, orderBy);
        } catch (InstantiationException ex) {
            Log.e(AppContext.LOG_TAG, "Failed to instantiate object", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.e(AppContext.LOG_TAG, "Failed to instantiate object", ex);
            return null;
        }
    }

    public T getOne(String table, String[] columns, String selection, String[] selectionArgs) {
        return getOne(table, columns, selection, selectionArgs, null);
    }

    public T getOne(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return getOne(table, columns, selection, selectionArgs, null, null, orderBy);
    }

    public T getOne(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        database = AppContext.getInstance().getDB();
        get(database.query("`" + table + "`", columns, selection, selectionArgs, groupBy, having, orderBy, "1"));
        return result == null || result.isEmpty() ? null : result.get(0);
    }

    public List<T> get(AbstractSqliteDbDao<T> daObject, Class<T> daClass, String selection, String[] selectionArgs, String orderBy) {
        return get(daObject, daClass, selection, selectionArgs, orderBy, null);
    }

    public List<T> get(AbstractSqliteDbDao<T> daObject, Class<T> daClass, String selection, String[] selectionArgs, String orderBy, String limit) {
        try {
            Set keys = daObject.getContentValues(daClass.newInstance()).keySet();
            String[] values = new String[keys.size()];
            values = (String[]) keys.toArray(values);
            return get(daObject.getTable(), values, selection, selectionArgs, null, null, orderBy, limit);
        } catch (InstantiationException ex) {
            Log.e(AppContext.LOG_TAG, "Failed to instantiate object", ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.e(AppContext.LOG_TAG, "Failed to instantiate object", ex);
            return null;
        }
    }

    public List<T> get(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return get(table, columns, selection, selectionArgs, null, null, orderBy, null);
    }

    public List<T> get(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return get(table, columns, selection, selectionArgs, groupBy, having, orderBy, null);
    }

    public List<T> get(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        database = AppContext.getInstance().getDB();
        return get(database.query("`" + table + "`", columns, selection, selectionArgs, groupBy, having, orderBy, limit));
    }

    public List<T> get(String rawSql, String[] args) {
        database = AppContext.getInstance().getDB();
        return get(database.rawQuery(rawSql, args));
    }

    public List<T> get(Cursor cursor) {
        result = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    doExecute(cursor);
                } while (cursor.moveToNext());
            }
        } catch (IllegalStateException e) {
            Log.d(AppContext.LOG_TAG, "CursorTemplate illegal state exception", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            AppContext.getInstance().closeDB(database);
        }
        return result;
    }

    public HashMap<T, Integer> getKeyMap(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        database = AppContext.getInstance().getDB();
        return getKeyMap(database.query("`" + table + "`", columns, selection, selectionArgs, null, null, orderBy, null));
    }

    public HashMap<T, Integer> getKeyMap(Cursor cursor) {
        keymap = new HashMap<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    doExecute(cursor);
                } while (cursor.moveToNext());
            }
        } catch (IllegalStateException e) {
            Log.d(AppContext.LOG_TAG, "CursorTemplate illegal state exception", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            AppContext.getInstance().closeDB(database);
        }
        return keymap;
    }
}

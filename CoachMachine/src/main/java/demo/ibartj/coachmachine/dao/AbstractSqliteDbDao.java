package demo.ibartj.coachmachine.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import demo.ibartj.coachmachine.AppContext;
import demo.ibartj.coachmachine.dao.util.DbColumn;
import demo.ibartj.coachmachine.dao.util.DbIndex;
import demo.ibartj.coachmachine.dao.util.DbType;
import demo.ibartj.coachmachine.exceptions.DaoDbException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
@SuppressWarnings("unused")
abstract class AbstractSqliteDbDao<T> {
    abstract String getTable();

    abstract DbColumn[] getColumnDefinitions();

    protected DbColumn[] columnDefinitions;

    protected DbIndex[] getIndexDefinitions() {
        return new DbIndex[0];
    }

    protected DbIndex[] indexDefinitions;

    protected DbColumn[] getMyColumnDefinitions() {
        if (columnDefinitions == null) {
            columnDefinitions = getColumnDefinitions();
        }
        return columnDefinitions;
    }

    protected DbIndex[] getMyIndexDefinitions() {
        if (indexDefinitions == null) {
            indexDefinitions = getIndexDefinitions();
        }
        return indexDefinitions;
    }

    public String getIdentityColumnName() {
        for (DbColumn column : getMyColumnDefinitions()) {
            if (column.getType() == DbType.ID) {
                return column.getName();
            }
        }
        return null;
    }

    ContentValues getContentValues(T object) {
        ContentValues vals = new ContentValues();
        for (DbColumn column : getMyColumnDefinitions()) {
            column.addContentValue(vals, object);
        }
        return vals;
    }

    @SuppressWarnings("unchecked")
    protected T getNewInstance() {
        T object;
        try {
            object = (T) getTClass().newInstance();
        } catch (IllegalAccessException ex) {
            throw new DaoDbException("Problem instantiating " + getClass().getGenericSuperclass().getClass().toString(), ex);
        } catch (InstantiationException ex) {
            throw new DaoDbException("Problem instantiating " + getClass().getGenericSuperclass().getClass().toString(), ex);
        }
        return object;
    }

    protected Class getTClass() {
        Type t = getClass().getGenericSuperclass();
        if (!(t instanceof ParameterizedType)) {
            t = getClass().getSuperclass().getGenericSuperclass();
            if (!(t instanceof ParameterizedType)) {
                t = getClass().getSuperclass().getSuperclass().getGenericSuperclass();
                if (!(t instanceof ParameterizedType)) {
                    t = getClass().getSuperclass().getSuperclass().getSuperclass().getGenericSuperclass();
                }
            }
        }
        return (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
    }

    T fromCursor(Cursor cursor) {
        T object = getNewInstance();
        for (DbColumn column : getMyColumnDefinitions()) {
            column.setFromCursor(object, cursor);
        }
        return object;
    }

    public void createTable(SQLiteDatabase database) {
        StringBuilder builder = new StringBuilder();
        List<String> indexes = new ArrayList<>();
        try {
            for (DbColumn column : getMyColumnDefinitions()) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append("`");
                builder.append(column.getName());
                builder.append("` ");
                builder.append(column.getType().getDbConstruct());
                if (column.isIndexed()) {
                    indexes.add("CREATE INDEX IF NOT EXISTS ix_" + getTable() + "_" + column.getName() + " ON " + getTable() + " (" + column.getName() + ");");
                }
            }
        } catch (Exception ex) {
            Class clazz = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            throw new DaoDbException("Error creating table (" + getTable() + " : " + clazz.getName() + ").");
        }
        try {
            for (DbIndex index : getMyIndexDefinitions()) {
                indexes.add("CREATE INDEX IF NOT EXISTS ix_" + getTable() + "_" + index.getName() + " ON `" + getTable() + "` (" + index.getColumnList() + ");");
            }
        } catch (Exception ex) {
            Class clazz = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            throw new DaoDbException("Error creating table (" + getTable() + " : " + clazz.getName() + ").");
        }
        if (builder.length() > 0) {
            builder.insert(0, "CREATE TABLE IF NOT EXISTS `" + getTable() + "` (");
            builder.append(");");
            database.execSQL(builder.toString());
            for (String index : indexes) {
                database.execSQL(index);
            }
        }
    }

    public void insert(T object) {
        SQLiteDatabase database = AppContext.getInstance().getDB();
        long id = database.insert("`" + getTable() + "`", null, getContentValues(object));
        AppContext.getInstance().closeDB(database);
        if (object instanceof Identity) {
            ((Identity) object).setId((int) id);
        }
    }

    public void insert(T object, SQLiteDatabase database) {
        long id = database.insert("`" + getTable() + "`", null, getContentValues(object));
        if (object instanceof Identity) {
            ((Identity) object).setId((int) id);
        }
    }

    @SuppressWarnings("unused")
    public int update(T object) {
        if (!(object instanceof Identity)) {
            throw new DaoDbException("Argument does not implement Identity interface.");
        }
        return update(getContentValues(object), ((Identity) object).getId());
    }

    public int update(T object, SQLiteDatabase database) {
        if (!(object instanceof Identity)) {
            throw new DaoDbException("Argument does not implement Identity interface.");
        }
        return update(getContentValues(object), ((Identity) object).getId(), database);
    }

    public int update(ContentValues contentValues, Integer id) {
        return update(contentValues, getIdentityColumnName() + "=?", new String[]{id.toString()});
    }

    public int update(ContentValues contentValues, Integer id, SQLiteDatabase database) {
        return update(contentValues, getIdentityColumnName() + "=?", new String[]{id.toString()}, database);
    }

    public int update(ContentValues contentValues, String where, String[] whereArgs, SQLiteDatabase database) {
        return database.update("`" + getTable() + "`", contentValues, where, whereArgs);
    }

    public int update(ContentValues contentValues, String where, String[] whereArgs) {
        SQLiteDatabase database = AppContext.getInstance().getDB();
        int rowsAffected = update(contentValues, where, whereArgs, database);
        AppContext.getInstance().closeDB(database);
        return rowsAffected;
    }

    public int delete(Identity object) {
        if (object.getId() != null && object.getId() > 0) {
            return delete(getIdentityColumnName() + "=?", new String[]{object.getId().toString()});
        }
        return 0;
    }

    public int delete(String whereClause, String[] whereParams) {
        SQLiteDatabase database = AppContext.getInstance().getDB();
        int rows = database.delete("`" + getTable() + "`", whereClause, whereParams);
        AppContext.getInstance().closeDB(database);
        return rows;
    }

    @SuppressWarnings("unused")
    public void truncate() {
        delete("", new String[0]);
    }

    public T insertOrUpdate(T object) {
        SQLiteDatabase database = AppContext.getInstance().getDB();
        if (object instanceof Identity) {
            Identity i = (Identity) object;
            if (i.getId() != null && i.getId() != 0) {
                database.delete("`" + getTable() + "`", getIdentityColumnName() + "=?", new String[]{i.getId().toString()});
            }
        } else {
            database.delete("`" + getTable() + "`", "", null);
        }

        Integer insertId = (int) database.insertWithOnConflict("`" + getTable() + "`", null, getContentValues(object), SQLiteDatabase.CONFLICT_ABORT);

        if (object instanceof Identity) {
            Identity i = (Identity) object;
            if (i.getId() == null || i.getId() == 0) {
                i.setId(insertId);
            }
        }

        AppContext.getInstance().closeDB(database);

        return object;
    }

    @SuppressWarnings("unused")
    public int getCount() {
        return getCount(null, null);
    }

    public int getCount(String whereClause, String[] whereParams) {
        return new CursorTemplate<Integer>() {
            @Override
            public void doExecute(Cursor cursor) {
                result(cursor.getInt(0));
            }
        }.getOne(getTable(), new String[]{"COUNT(*)"}, whereClause, whereParams, null);
    }

    @SuppressWarnings("unused")
    public int getMaxId() {
        return new CursorTemplate<Integer>() {
            @Override
            public void doExecute(Cursor cursor) {
                result(cursor.getInt(0));
            }
        }.getOne(getTable(), new String[]{"MAX(" + getIdentityColumnName() + ")"}, "", null);
    }

    @SuppressWarnings("unchecked")
    public T getById(Integer id) {
        CursorTemplate<T> c = new CursorTemplate<T>() {
            @Override
            public void doExecute(Cursor cursor) {
                result(fromCursor(cursor));
            }
        };
        return (T) c.getOne(this, getTClass(), getIdentityColumnName() + "=?", new String[]{id.toString()});
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return new CursorTemplate<T>() {
            @Override
            public void doExecute(Cursor cursor) {
                result(fromCursor(cursor));
            }
        }.get(this, getTClass(), "", null, getIdentityColumnName());
    }

    public HashMap<String, Integer> getKeyMap(String valueColumn) {
        return new CursorTemplate<String>() {
            @Override
            public void doExecute(Cursor cursor) {
                keymap(cursor.getInt(0), cursor.getString(1));
            }
        }.getKeyMap(getTable(), new String[]{"_id", valueColumn}, "", null, null);
    }
}

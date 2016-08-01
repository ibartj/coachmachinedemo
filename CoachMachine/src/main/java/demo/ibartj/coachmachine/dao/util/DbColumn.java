package demo.ibartj.coachmachine.dao.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import demo.ibartj.coachmachine.AppContext;
import demo.ibartj.coachmachine.exceptions.DaoDbException;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class DbColumn {
    /**
     * Database column name.
     */
    protected String name;
    /**
     * Data type. Member of DataType enum.
     */
    protected DbType type;
    /**
     * Name of a getter/setter in the object (getSomething/setSomething = "Something");
     */
    protected String getSetName;
    /**
     * Return type of getter / parameter type of setter.
     */
    protected Class getSetClass;
    /**
     * True if the column should be indexed.
     */
    protected boolean indexed;

    /**
     * Column object constructor.
     *
     * @param name        Database column name.
     * @param type        Data type. Member of DataType enum.
     * @param getSetName  Name of a getter/setter in the object (getSomething/setSomething = "Something");
     * @param getSetClass Return type of getter / parameter type of setter.
     * @param indexed     True if the column should be indexed.
     */
    public DbColumn(String name, DbType type, String getSetName, Class getSetClass, boolean indexed) {
        this.name = name;
        this.type = type;
        this.getSetName = getSetName;
        this.getSetClass = getSetClass;
        this.indexed = indexed;
    }

    /**
     * Column object constructor.
     *
     * @param name        Database column name.
     * @param type        Data type. Member of DataType enum.
     * @param getSetName  Name of a getter/setter in the object (getSomething/setSomething = "Something");
     * @param getSetClass Return type of getter / parameter type of setter.
     */
    public DbColumn(String name, DbType type, String getSetName, Class getSetClass) {
        this(name, type, getSetName, getSetClass, false);
    }

    /**
     * Returns database column name.
     *
     * @return Column name.
     */
    public String getName() {
        return name;
    }

    /**
     * Data type. Member of DataType enum.
     *
     * @return Data type.
     */
    public DbType getType() {
        return type;
    }

    /**
     * Name of a getter/setter in the object (getSomething/setSomething = "Something");
     *
     * @return Getter/setter name.
     */
    public String getGetSetName() {
        return getSetName;
    }

    /**
     * Return type of getter / parameter type of setter.
     *
     * @return Getter / setter type.
     */
    public Class getGetSetClass() {
        return getSetClass;
    }

    /**
     * True if the column should be indexed.
     *
     * @return Indexed.
     */
    public boolean isIndexed() {
        return indexed;
    }

    public Method getGetMethod(Object object) {
        try {
            if (getGetSetClass() == Boolean.class) {
                return object.getClass().getMethod("is" + getGetSetName());
            } else {
                return object.getClass().getMethod("get" + getGetSetName());
            }
        } catch (NoSuchMethodException ex) {
            throw new DaoDbException("Invalid DB column definition " + getGetSetName(), ex);
        }
    }

    @SuppressWarnings("unused")
    public Method getSetMethod(Object object) {
        try {
            return object.getClass().getMethod("set" + getGetSetName(), getGetSetClass());
        } catch (NoSuchMethodException ex) {
            throw new DaoDbException("Invalid DB column definition", ex);
        }
    }

    public void addContentValue(ContentValues values, Object object) {
        Method method = getGetMethod(object);
        Object result;
        try {
            result = method.invoke(object);
        } catch (InvocationTargetException ex) {
            throw new DaoDbException("Invalid DB column definition: " + this.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new DaoDbException("Invalid DB column definition: " + this.getName(), ex);
        }
        switch (getType()) {
            case ID:
                if (result instanceof Integer) {
                    Integer id = (Integer) result;
                    values.put(getName(), id == null || id.equals(0) ? null : id);
                } else {
                    String id = (String) result;
                    values.put(getName(), id == null || id.equals("") ? null : id);
                }
                break;
            default:
                addContentValueBySimpleName(values, result);
        }
    }

    private void addContentValueBySimpleName(ContentValues values, Object result) {
        switch (getGetSetClass().getSimpleName()) {
            case "BigDecimal":
                values.put(getName(), result == null ? null : ((BigDecimal) result).doubleValue());
                break;
            case "Double":
                values.put(getName(), (Double) result);
                break;
            case "Float":
                values.put(getName(), (Float) result);
                break;
            case "Integer":
                values.put(getName(), (Integer) result);
                break;
            case "Boolean":
                values.put(getName(), (Boolean) result);
                break;
            default:
                values.put(getName(), result == null ? null : result.toString());
                break;
        }
    }

    public void setFromCursor(Object object, Cursor cursor) {
        try {
            object.getClass().getMethod("set" + getGetSetName(), getGetSetClass()).invoke(object, getValueFromCursor(object, cursor));
        } catch (DbValueFormatException ex) {
            Log.d(AppContext.LOG_TAG, "Format error", ex);
        } catch (NoSuchMethodException ex) {
            throw new DaoDbException("Invalid DB column definition in " + getGetSetName(), ex);
        } catch (IllegalAccessException ex) {
            throw new DaoDbException("Invalid DB column definition in " + getGetSetName(), ex);
        } catch (InvocationTargetException ex) {
            throw new DaoDbException("Invalid DB column definition in " + getGetSetName(), ex);
        }
    }

    public Object getValueFromCursor(Object object, Cursor cursor) throws DbValueFormatException {
        int columnIndex = cursor.getColumnIndexOrThrow(getName());
        return getValueFromCursorByClassName(columnIndex, getGetSetClass().getSimpleName(), cursor);
    }

    private Object getValueFromCursorByClassName(int columnIndex, String className, Cursor cursor) throws DbValueFormatException {
        switch (className) {
            case "String":
                return cursor.getString(columnIndex);
            case "SerializableJSONObject":
                try {
                    return new SerializableJSONObject(cursor.getString(columnIndex));
                } catch (JSONException ex) {
                    throw new DbValueFormatException(ex);
                }
            case "SerializableJSONArray":
                try {
                    return new SerializableJSONArray(cursor.getString(columnIndex));
                } catch (JSONException ex) {
                    throw new DbValueFormatException(ex);
                }
            case "DbDate":
                try {
                    String date = cursor.getString(columnIndex);
                    return date == null ? null : new DbDate(date);
                } catch (ParseException ex) {
                    throw new DbValueFormatException(ex);
                }
            case "Float":
                return cursor.getFloat(columnIndex);
            case "Double":
                return cursor.getDouble(columnIndex);
            case "BigDecimal":
                return new BigDecimal(cursor.getDouble(columnIndex));
            case "Integer":
                return cursor.getInt(columnIndex);
            case "Long":
                return cursor.getLong(columnIndex);
            case "Boolean":
                return cursor.getInt(columnIndex) > 0;
            default:
                return null;
        }
    }
}
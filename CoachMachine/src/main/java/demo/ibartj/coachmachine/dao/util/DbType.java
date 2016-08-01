package demo.ibartj.coachmachine.dao.util;

import java.math.BigDecimal;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public enum DbType {
    ID("INTEGER PRIMARY KEY AUTOINCREMENT", Integer.class),
    BOOL("INTEGER", Boolean.class),
    INT("INTEGER NOT NULL", Integer.class),
    INT_NULLABLE("INTEGER", Integer.class),
    FLOAT("REAL NOT NULL", BigDecimal.class),
    FLOAT_NULLABLE("REAL", BigDecimal.class),
    TEXT("TEXT NOT NULL", String.class),
    TEXT_NULLABLE("TEXT", String.class),
    JSON_ARRAY("TEXT", String.class),
    JSON_OBJECT("TEXT", String.class),
    PASSWORD("TEXT", String.class),
    BLOB("BLOB NOT NULL", String.class),
    BLOB_NULLABLE("BLOB", String.class),
    DATETIME("DATETIME NOT NULL", DbDate.class),
    DATETIME_DC("DATETIME DEFAULT CURRENT_TIMESTAMP", DbDate.class),
    DATETIME_NULLABLE("DATETIME", DbDate.class),
    DATETIME_NULLABLE_DC("DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP", DbDate.class),
    TIME("TIME NOT NULL", DbTime.class),
    TIME_NULLABLE("TIME", DbTime.class),
    POSSYNCID("TEXT", String.class);

    /**
     * A part of the database table create query.
     */
    private String dbConstruct;
    private Class cls;

    /**
     * DataType constructor.
     *
     * @param dbConstruct A part of the database table create query.
     */
    DbType(String dbConstruct, Class cls) {
        this.dbConstruct = dbConstruct;
        this.cls = cls;
    }

    /**
     * Returns an SQL query part.
     *
     * @return A part of the database table create query.
     */
    public String getDbConstruct() {
        return this.dbConstruct;
    }

    @SuppressWarnings("unused")
    public Class getCls() {
        return this.cls;
    }
}

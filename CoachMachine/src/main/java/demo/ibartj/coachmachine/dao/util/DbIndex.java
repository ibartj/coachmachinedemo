package demo.ibartj.coachmachine.dao.util;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class DbIndex {
    protected String name;
    protected String[] columns;

    public DbIndex(String name, String[] columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public String[] getColumns() {
        return columns;
    }

    public String getColumnList() {
        StringBuilder columnList = new StringBuilder();
        for (String column : columns) {
            if (columnList.length() > 0) {
                columnList.append(",");
            }
            columnList.append(column);
        }
        return columnList.toString();
    }
}
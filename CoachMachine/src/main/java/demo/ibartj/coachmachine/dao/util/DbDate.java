package demo.ibartj.coachmachine.dao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class DbDate extends Date {
    protected final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @SuppressWarnings("unused")
    public DbDate() {
        super(Calendar.getInstance().getTime().getTime());
    }

    public DbDate(String date) throws ParseException {
        super(DATE_FORMAT.parse(date).getTime());
    }

    @SuppressWarnings("unused")
    public DbDate(Date date) {
        super(date == null ? Calendar.getInstance().getTime().getTime() : date.getTime());
    }

    @Override
    public String toString() {
        return DATE_FORMAT.format(this);
    }
}

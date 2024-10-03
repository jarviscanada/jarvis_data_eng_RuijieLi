package ca.jrvs.stockquote.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
    // https://stackoverflow.com/a/1149721/11627201
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

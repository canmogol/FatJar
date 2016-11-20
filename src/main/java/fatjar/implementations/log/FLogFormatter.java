package fatjar.implementations.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class FLogFormatter extends SimpleFormatter {

    private static Integer logClassStack = null;

    private static Map<String, String> logKey = new HashMap<String, String>() {{
        put("OFF", " OFF  ");
        put("SEVERE", "SEVERE");
        put("WARNING", "WARNIN");
        put("INFO", " INFO ");
        put("CONFIG", "CONFIG");
        put("FINE", " FINE ");
        put("FINER", "FINER ");
        put("FINEST", "FINEST");
        put("ALL", "  ALL ");
    }};

    private StackTraceElement getLogClassStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (logClassStack == null) {
            for (int i = 0; i < stackTrace.length; i++) {
                if (fatjar.Log.class.getName().equals(stackTrace[i].getClassName())) {
                    logClassStack = i;
                    break;
                }
            }
        }
        if (logClassStack != null && stackTrace.length > logClassStack) {
            return stackTrace[logClassStack + 1];
        } else {
            return stackTrace[0];
        }
    }

    @Override
    public synchronized String format(LogRecord record) {
        StackTraceElement stackTraceElement = getLogClassStack();
        StringBuilder stringBuilder = new StringBuilder();
        // date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(record.getMillis());
        // add logger key
        stringBuilder.append("[LOG] ");
        // current date time
        stringBuilder.append("[");
        stringBuilder.append(simpleDateFormat.format(date));
        stringBuilder.append("] ");
        // log level
        stringBuilder.append("[");
        stringBuilder.append(logKey.getOrDefault(record.getLevel().getName(), record.getLevel().getName()));
        stringBuilder.append("] ");
        // current thread
        stringBuilder.append("[");
        stringBuilder.append(Thread.currentThread().getId());
        stringBuilder.append("] ");
        // get caller
        stringBuilder.append("[");
        stringBuilder.append(stackTraceElement.getClassName());
        stringBuilder.append(".");
        stringBuilder.append(stackTraceElement.getMethodName());
        stringBuilder.append(":");
        stringBuilder.append(stackTraceElement.getLineNumber());
        stringBuilder.append("] ");
        // format message
        stringBuilder.append(record.getMessage());
        // add new line
        stringBuilder.append(System.lineSeparator());
        // return log message
        return stringBuilder.toString();
    }

    @Override
    public synchronized String formatMessage(LogRecord record) {
        return format(record);
    }

}
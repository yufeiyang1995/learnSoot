package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class LoggerFactory {
	private static final Map<String, Logger> loggerMap = new HashMap<>();

    public static final String LOGFILE = "/data/local/tmp/classLoad.log";

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        if (!loggerMap.containsKey(name)) {
            loggerMap.put(name, new Logger(name, System.out));
        }
        return loggerMap.get(name);
    }

    // Get logger, redirecting to a file.
    public static Logger getLogger(String name, String filename) throws FileNotFoundException {
        if (!loggerMap.containsKey(name)) {
            PrintStream out = new PrintStream(new FileOutputStream(new File(filename)), true);
            loggerMap.put(name, new Logger(name, out));
        }
        return loggerMap.get(name);
    }

    public static Logger getGlobal() throws FileNotFoundException {
        return getLogger("global", LOGFILE);
    }
}

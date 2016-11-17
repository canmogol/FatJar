package fatjar;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface Log {

    Logger LOG = Logger.getLogger(Log.class.getName());

    static void info(String message) {
        LOG.log(Level.INFO, message);
    }

    static void debug(String message) {
        LOG.log(Level.FINEST, message);
    }

    static void error(String message) {
        LOG.log(Level.SEVERE, message);
    }

}

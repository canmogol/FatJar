package fatjar.implementations.log;


import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class FLogger {

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        // do not use parent handlers
        logger.setUseParentHandlers(false);
        // if there are any, remove them
        for (Handler handler : logger.getHandlers()) {
            // remove already registered handlers
            logger.removeHandler(handler);
        }
        // add new console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new FLogFormatter());
        logger.addHandler(consoleHandler);
        return logger;
    }

}
package Model;

import sun.rmi.runtime.Log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by vn130 on 11/18/2015.
 */
public class CustomLogger {
    private Logger logger;
    public CustomLogger(String name){
        this.logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.valueOf(record.getLevel()) + " : " + formatMessage(record) + "\n";
            }
        });
        logger.addHandler(consoleHandler);
    }

    public Logger getLogger(){
        return this.logger;
    }
}

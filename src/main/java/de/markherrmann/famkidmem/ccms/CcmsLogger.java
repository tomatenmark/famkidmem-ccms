package de.markherrmann.famkidmem.ccms;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CcmsLogger {

    private Logger logger;

    public CcmsLogger(Class where){
        try {
            logger = Logger.getLogger(where.getSimpleName());
            FileHandler fileHandler = new FileHandler("./famkidmem-ccms.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void info(String message){
        logger.info(message);
    }

    public void error(String message){
        logger.severe(message);
    }
}

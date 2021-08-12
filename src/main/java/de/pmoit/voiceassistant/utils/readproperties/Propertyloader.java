package de.pmoit.voiceassistant.utils.readproperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Propertyloader {
    private static Logger logger = LoggerFactory.getLogger("Propertyloader");

    public static Properties loadProperties(String propertiesFile) {
        Properties props = new Properties();
        String userdir = System.getProperty("user.dir");
        File configFile = new File(userdir + "/" + propertiesFile);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            props.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            logger.error("Konfigurationseintrag konnte nicht gefunden werden! " + propertiesFile, e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return props;
    }

}

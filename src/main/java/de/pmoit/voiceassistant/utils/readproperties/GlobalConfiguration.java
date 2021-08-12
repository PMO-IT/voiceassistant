package de.pmoit.voiceassistant.utils.readproperties;

import java.util.Properties;


/**
 * Read the global configuration file
 */
public class GlobalConfiguration {
    private final static String CONFIG_PROPERTIES_PATH = "/resources/config.properties";
    private final static Properties CONFIG = Propertyloader.loadProperties(CONFIG_PROPERTIES_PATH);

    public static String getVoice() {
        return getProperty("voice");
    }

    public static String getAssistantName() {
        return getProperty("name");
    }

    public static String getCallToAction1() {
        return getProperty("call_to_action1");
    }

    public static String getCallToAction2() {
        return getProperty("call_to_action2");
    }

    public static String getStopAction() {
        return getProperty("stop_action");
    }

    public static String getServerAdress() {
        return getProperty("server_adress");
    }

    public static int getAdditionalListeningTime() {
        return getPropertyInt("additional_listening_time");
    }

    private static String getProperty(String property) {
        return CONFIG.getProperty(property);
    }

    private static int getPropertyInt(String property) {
        return Integer.parseInt(CONFIG.getProperty(property));
    }
}

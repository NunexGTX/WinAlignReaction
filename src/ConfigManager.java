import Interface.MonitorProbe;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Properties;

public class ConfigManager {
    //Configuration Values
    private Path xml_dir = Path.of(System.getProperty("user.home"));
    private int windowTimeoutSeconds = 5;
    private int windowWidth = 512;
    private int windowHeight = 512;
    private int windowX = 0;
    private int windowY = 0;
    private int monitor_number = 0;
    private MEASObjects measObject = MEASObjects.TIRE_CHECK;

    //Config File
    private final static String ConfigFileName = "QuickCheckReactorConfig.properties";
    private Path configFilePath;

    private Properties ProgramProperties;

    public ConfigManager(){
        initConfig();
    }

    private void initConfig(){
        configFilePath = getConfigPath(QuickCheckReactor.AppName);

        if(Files.exists(configFilePath)){
            try {
                loadConfigFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                load_default_monitor_dimensions();
                makeConfigFile();
                //Open a new Settings menu for the user to adjust the config
                new SettingsMenu(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Path getConfigPath(String appName){
        return getAppDataDir(appName).resolve(ConfigFileName);
    }

    public static Path getAppDataDir(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        Path base;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA"); //Gets the Roaming AppData from Window's environment variable
            base = Paths.get(appData != null ? appData : home); //Use user's folder if not available
        } else if (os.contains("mac")) {
            base = Paths.get(home, "Library", "Application Support");
        } else {
            // Linux / other Unix
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            base = Paths.get(xdgConfig != null ? xdgConfig : home + "/.config");
        }

        return base.resolve(appName);
    }

    private void loadConfigFile() throws  IOException{
        ProgramProperties = new Properties();

        try (InputStream in = Files.newInputStream(configFilePath)) {
            ProgramProperties.load(in);

            Path xml_path_load = Path.of(ProgramProperties.getProperty("Quick_Check_XML_PATH"));
            int windowTimeoutSeconds_load = Integer.parseInt(ProgramProperties.getProperty("emoji_window_timeout_second"));
            int windowWidth_load = Integer.parseInt(ProgramProperties.getProperty("window_width"));
            int windowHeight_load = Integer.parseInt(ProgramProperties.getProperty("window_height"));
            int windowX_load = Integer.parseInt(ProgramProperties.getProperty("window_x"));
            int windowY_load = Integer.parseInt(ProgramProperties.getProperty("window_y"));
            int monitor_number_load = Integer.parseInt(ProgramProperties.getProperty("monitor_number"));
            MEASObjects measObject_load = MEASObjects.valueOf(ProgramProperties.getProperty("MEASObjectName"));

            //Register the values
            xml_dir = xml_path_load;
            windowTimeoutSeconds = windowTimeoutSeconds_load;
            windowWidth = windowWidth_load;
            windowHeight = windowHeight_load;
            windowX = windowX_load;
            windowY = windowY_load;
            monitor_number = monitor_number_load;
            measObject = measObject_load;
        }catch (IOException | NumberFormatException | InvalidPathException | NullPointerException e) {
            try {
                System.out.println("The configuration file contains invalid values or has been corrupted. Resetting to default values...");
                Files.move(configFilePath, configFilePath.resolveSibling(configFilePath.getFileName() + ".broken"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException deleteEx) {
                deleteEx.printStackTrace();
            }
            makeConfigFile(); // recreate with fresh defaults
            System.out.println("WARNING: Config file was invalid and has been reset to defaults");
        }

        System.out.println("Loaded existing config from: " + configFilePath);

    }

    private void makeConfigFile() throws IOException {
        Files.createDirectories(configFilePath.getParent());

        //New properties object, to directly receive the configuration parameters
        ProgramProperties = new Properties();

        ProgramProperties.setProperty("Quick_Check_XML_PATH", String.valueOf(xml_dir));
        ProgramProperties.setProperty("emoji_window_timeout_second", String.valueOf(windowTimeoutSeconds));
        ProgramProperties.setProperty("window_width",String.valueOf(windowWidth));
        ProgramProperties.setProperty("window_height",String.valueOf(windowHeight));
        ProgramProperties.setProperty("window_x", String.valueOf(windowX));
        ProgramProperties.setProperty("window_y",String.valueOf(windowY));
        ProgramProperties.setProperty("monitor_number",String.valueOf(monitor_number));
        ProgramProperties.setProperty("MEASObjectName",measObject.name());

        try (OutputStream out = Files.newOutputStream(configFilePath)) {
            ProgramProperties.store(out, "Quick Check Reactor - Default Configuration");
        }
        System.out.println("Created default config at: " + configFilePath);
    }

    private void load_default_monitor_dimensions(){
        int[] screenBounds = MonitorProbe.getScreenBounds(MonitorProbe.getDefaultDisplay());
        windowX = screenBounds[0];
        windowY = screenBounds[1];
        windowWidth = screenBounds[2];
        windowHeight = screenBounds[3];
    }

    private void update_config_file() throws IOException{
        try (OutputStream out = Files.newOutputStream(configFilePath)) {
            ProgramProperties.store(out, "Quick Check Reactor - New User Configuration");
        }

        System.out.println("New config saved at: "+configFilePath);
    }

    public Path getXml_dir() {
        return xml_dir;
    }

    public int getWindowTimeoutSeconds() {
        return windowTimeoutSeconds;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowX() {
        return windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public int getMonitor_number() {
        return monitor_number;
    }

    public MEASObjects getMeasObject(){return measObject;}


    //Setters for the settings menu
    public void setXml_dir(String pickedXMLDir){
        xml_dir = Path.of(pickedXMLDir);
        ProgramProperties.setProperty("Quick_Check_XML_PATH", String.valueOf(xml_dir));
        try {
            update_config_file();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMonitor_number(int monitor_number) {
        this.monitor_number = monitor_number;
        ProgramProperties.setProperty("monitor_number",String.valueOf(monitor_number));

        //Set the new screen dimensions with the ones from this monitor
        int[] screenBounds = MonitorProbe.getScreenBounds(monitor_number);
        windowX = screenBounds[0];
        windowY = screenBounds[1];
        windowWidth = screenBounds[2];
        windowHeight = screenBounds[3];
        ProgramProperties.setProperty("window_width",String.valueOf(windowWidth));
        ProgramProperties.setProperty("window_height",String.valueOf(windowHeight));
        ProgramProperties.setProperty("window_x", String.valueOf(windowX));
        ProgramProperties.setProperty("window_y",String.valueOf(windowY));

        try {
            update_config_file();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMeasObject(MEASObjects pickedMeasObject){
        measObject = pickedMeasObject;
        ProgramProperties.setProperty("MEASObjectName",pickedMeasObject.name());
        try {
            update_config_file();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWindowTimeoutSeconds(int windowTimeoutSeconds){
        this.windowTimeoutSeconds = windowTimeoutSeconds;
        ProgramProperties.setProperty("emoji_window_timeout_second", String.valueOf(windowTimeoutSeconds));
        try {
            update_config_file();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

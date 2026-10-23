import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Properties;

public class ConfigManager {
    //Configuration Values
    private Path xml_dir = Path.of(System.getProperty("user.home"));
    private long windowTimeoutSeconds = 5;
    private int windowWidth = 512;
    private int windowHeight = 512;
    private int windowX = 0;
    private int windowY = 0;

    //Config File
    private final static String ConfigFileName = "QuickCheckReactorConfig.properties";
    private Path configFilePath;

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
                makeConfigFile();
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
        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(configFilePath)) {
            properties.load(in);

            Path xml_path_load = Path.of(properties.getProperty("Quick_Check_XML_PATH"));
            int windowTimeoutSeconds_load = Integer.parseInt(properties.getProperty("emoji_window_timeout_second"));
            int windowWidth_load = Integer.parseInt(properties.getProperty("window_width"));
            int windowHeight_load = Integer.parseInt(properties.getProperty("window_height"));
            int windowX_load = Integer.parseInt(properties.getProperty("window_x"));
            int windowY_load = Integer.parseInt(properties.getProperty("window_y"));

            //Register the values
            xml_dir = xml_path_load;
            windowTimeoutSeconds = windowTimeoutSeconds_load;
            windowWidth = windowWidth_load;
            windowHeight = windowHeight_load;
            windowX = windowX_load;
            windowY = windowY_load;
        }catch (IOException | NumberFormatException | InvalidPathException | NullPointerException e) {
            try {
                System.out.println("The configuration file contains invalid values or has been corrupted. Resetting to default values...");
                Files.move(configFilePath, configFilePath.resolveSibling(configFilePath.getFileName() + ".broken"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException deleteEx) {
                deleteEx.printStackTrace();
            }
            makeConfigFile(); // recreate with fresh defaults
            throw new RuntimeException("Config file was invalid and has been reset to defaults", e);
        }

        System.out.println("Loaded existing config from: " + configFilePath);

    }

    private void makeConfigFile() throws IOException {
        Files.createDirectories(configFilePath.getParent());

        //New properties object, to directly receive the configuration parameters
        Properties propertiess = new Properties();

        propertiess.setProperty("Quick_Check_XML_PATH", String.valueOf(xml_dir));
        propertiess.setProperty("emoji_window_timeout_second", String.valueOf(windowTimeoutSeconds));
        propertiess.setProperty("window_width",String.valueOf(windowWidth));
        propertiess.setProperty("window_height",String.valueOf(windowHeight));
        propertiess.setProperty("window_x", String.valueOf(windowX));
        propertiess.setProperty("window_y",String.valueOf(windowY));

        try (OutputStream out = Files.newOutputStream(configFilePath)) {
            propertiess.store(out, "Quick Check Reactor - Default Configuration");
        }
        System.out.println("Created default config at: " + configFilePath);
    }

    public Path getXml_dir() {
        return xml_dir;
    }

    public long getWindowTimeoutSeconds() {
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
}

import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    //Configuration Values
    private String xml_path = ""; //TODO: FIX PATH
    private long windowTimeoutSeconds = 5;
    private int windowWidth = 512;
    private int windowHeight = 512;

    //Config File
    private final static String ConfigFileName = "QuickCheckReactor_config";
    private Path configFilePath;

    public ConfigManager(){
        initConfig();
    }

    private void initConfig(){
        configFilePath = getConfigPath(QuickCheckReactor.AppName);

        if(Files.exists(configFilePath)){
            loadConfigFile();
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

    private void loadConfigFile(){
        //TODO
    }

    private void makeConfigFile() throws IOException {
        Files.createDirectories(configFilePath);

        //New properties object, to directly receive the configuration parameters
        Properties props = new Properties();

        props.setProperty("Quick_Check_XML_PATH", xml_path);
        props.setProperty("emoji_window_timeout_second", String.valueOf(windowTimeoutSeconds));

    }

    public String getXml_path() {
        return xml_path;
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

}

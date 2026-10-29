import Interface.MonitorProbe;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/*
This is the main class of the WinAlignReaction program. It's gonna run as a system tray program and it will only draw a window with the result emoji to be destroyed right after
It includes the main runtime and its objective is to loop and check for changes in the XML file that includes the diagnostic results
The XML file's path will be specified in a config file that is read at startup, or a default path
When the result is read, the corresponding emoji result (or emoticon as fallback) will be displayed. After a configurable timeout, the window is destroyed and it waits until the XML file shows a new result.
Changes on the XML file should be tracked by some kind of fast hash algorithm and its result reading should be ignored when the program starts.
A settings menu to adjust the configuration would be a nice addition, but not mandatory
 */
public class QuickCheckReactor {
    public static String AppName = "Quick Check Reactor";

    //Child Class Instances
    private static ConfigManager configManager = new ConfigManager();
    private static QuickCheckResultWatcher quickCheckResultWatcher = new QuickCheckResultWatcher(configManager);
    private static EmojiDrawer emojiDrawer = new EmojiDrawer(configManager);
    //private static SettingsMenu settingsMenu = new SettingsMenu(configManager);

    //Register and show the program in system tray, along with its right click options
    private static void createTray(){
        //checking for support
        if(!SystemTray.isSupported()){
            System.out.println("System tray is not supported! Program will keep its execution and wait for the Quick Check result to show the Emoji Window.");
            return;
        }
        //Get system's tray
        SystemTray systemTray = SystemTray.getSystemTray();

        //Create a new options menu
        PopupMenu trayPopupMenu = new PopupMenu();

        //Register options menu contents
        MenuItem action = new MenuItem("Settings");
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!SettingsMenu.settingsOpen){
                   new SettingsMenu(configManager);
                }
            }
        });
        trayPopupMenu.add(action);

        MenuItem close = new MenuItem("Exit");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayPopupMenu.add(close);

        //setting tray icon
        //TODO: System Tray logo
        Image image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(QuickCheckReactor.class.getResourceAsStream("/assets/emojis/persevering_face_emoji.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TrayIcon trayIcon = new TrayIcon(image, "WinAlign QuickCheck Reaction", trayPopupMenu);
        trayIcon.setImageAutoSize(true);
        try{
            systemTray.add(trayIcon);
        }catch(AWTException awtException){
            awtException.printStackTrace();
        }
    }

    public static void resetLoop(){
        //Skips to the next loops, by resetting the watchXML
        quickCheckResultWatcher.watchRelease();
    }

    private static void process_args(String[] args){
        if (Arrays.asList(args).contains("log")){
            try {
                Path logFile = Path.of(ConfigManager.getAppDataDir(AppName)+"/log.txt");
                PrintStream logger = new PrintStream(new FileOutputStream(logFile.toFile(), true),true,StandardCharsets.UTF_8);

                System.setOut(logger);
                System.setErr(logger);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        createTray();
        //new SettingsMenu(configManager);

        process_args(args);

        while (true){
            try {
                if(quickCheckResultWatcher.watchXML()){ //Wait for XML to change (blocks and waits)
                    emojiDrawer.DrawQuickCheckResult(quickCheckResultWatcher.quickCheckResultVal());
                    TimeUnit.SECONDS.sleep(configManager.getWindowTimeoutSeconds()); //Wait for window timeout and halt the loop
                    emojiDrawer.DestroyResultWindow(); //Destroy the emoji window and continue the loop
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
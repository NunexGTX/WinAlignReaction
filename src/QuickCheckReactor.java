import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

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
    private static QuickCheckResultWatcher quickCheckResultWatcher = new QuickCheckResultWatcher(configManager.getXml_path());
    private static EmojiconDrawer emojiconDrawer = new EmojiconDrawer(configManager);
    private static SettingsMenu settingsMenu = new SettingsMenu(configManager);

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
                JOptionPane.showMessageDialog(null, "Action Clicked");
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

        /*setting tray icon
        //TODO: System Tray logo
        Image image = Toolkit.getDefaultToolkit().getImage("src/img.jpg");
        TrayIcon trayIcon = new TrayIcon(image, "WinAlign QuickCheck Reaction", trayPopupMenu);
        trayIcon.setImageAutoSize(true);
        try{
            systemTray.add(trayIcon);
        }catch(AWTException awtException){
            awtException.printStackTrace();
        }*/
    }

    public static void main(String[] args) {
        createTray();

        while (true){
            try {
                if(quickCheckResultWatcher.watchXML()){ //Wait for XML to change (blocks and waits)
                    emojiconDrawer.DrawQuickCheckResult(quickCheckResultWatcher.quickCheckResultVal());
                    TimeUnit.SECONDS.sleep(configManager.getWindowTimeoutSeconds()); //Wait for window timeout and halt the loop
                    emojiconDrawer.DestroyResultWindow(); //Destroy the emoji window and continue the loop
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
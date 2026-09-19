import UI.ImagePanel;

import javax.swing.*;
import java.awt.*;

public class SettingsMenu {
    public static boolean settingsOpen = false;

    private ConfigManager config;
    private JFrame settingsFrame;

    //TODO: Make this class in a test machine and check settings requirements

    public SettingsMenu(ConfigManager config){
        settingsOpen = true;
        this.config = config;

        showSettings();
    }

    private void showSettings(){
        //Set the frame
        settingsFrame = new JFrame("Quick Check Reactor - Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Do not close the app when the window closes

        //Create JPanel
        //TODO: Fix the layouts and general UI
        JPanel panel = new JPanel(new GridLayout(3,2));

        //File picker
        JFileChooser xmlFilePicker = new JFileChooser();
        panel.add(xmlFilePicker);


        //Only set visibility when fully drawn
        settingsFrame.setVisible(true);
    }


}

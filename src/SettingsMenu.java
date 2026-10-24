import Interface.MonitorProbe;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class SettingsMenu {
    public static boolean settingsOpen = false;

    private ConfigManager config;
    private JFrame settingsFrame;

    public SettingsMenu(ConfigManager config){
        settingsOpen = true;
        this.config = config;

        showSettings();
    }

    private void showSettings(){
        //Set the frame
        settingsFrame = new JFrame("Quick Check Reactor - Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Do not close the app when the window closes

        //Set Panel
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));   // gaps between cells
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // File picker row
        JTextField xmlPathField = new JTextField(config.getXml_dir().toString(),25);
        xmlPathField.setEditable(false);
        JButton browseButton = new JButton("Browse...");

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(xmlPathField.getText());
            chooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // use FILES_ONLY if picking a file
            if (chooser.showOpenDialog(settingsFrame) == JFileChooser.APPROVE_OPTION) {
                String pickedXMLDir = chooser.getSelectedFile().getAbsolutePath();
                xmlPathField.setText(pickedXMLDir);
                config.setXml_dir(pickedXMLDir);
            }
        });

        panel.add(new JLabel("XML folder:"));
        JPanel pickerRow = new JPanel(new BorderLayout(4, 0));
        pickerRow.add(xmlPathField, BorderLayout.CENTER);
        pickerRow.add(browseButton, BorderLayout.EAST);
        panel.add(pickerRow); //Add xml folder picker to panel

        //Monitor Dropdown menu
        JComboBox<String> MonitorsList = new JComboBox<>(MonitorProbe.monitorsDescripts());
        MonitorsList.setSelectedIndex(config.getMonitor_number());

        MonitorsList.addActionListener(e ->{
            config.setMonitor_number(MonitorsList.getSelectedIndex());
        });

        panel.add(new JLabel("Output Monitor:"));
        panel.add(MonitorsList);

        //MEASObject Dropwdown Selector
        JComboBox<MEASObjects> MEASObjectsDropwdown = new JComboBox<>(MEASObjects.values());
        MEASObjectsDropwdown.setSelectedItem(config.getMeasObject());

        MEASObjectsDropwdown.addActionListener(e ->{
            MEASObjects MEASelected = (MEASObjects) MEASObjectsDropwdown.getSelectedItem();
            if(MEASelected != null){
                config.setMeasObject(MEASelected);
            }
        });

        panel.add(new JLabel("Measurement type:"));
        panel.add(MEASObjectsDropwdown);

        //Add panel to frame and dynamically adjust its dimensions
        settingsFrame.add(panel);
        settingsFrame.pack();
        //Only set visibility when fully drawn
        settingsFrame.setVisible(true);

    }


}

import Interface.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;

public class EmojiDrawer {
    private ConfigManager config;

    private boolean borderless = true;

    private JFrame frame;

    public EmojiDrawer(ConfigManager config){
        this.config = config;
    }

    public static BufferedImage loadImage(String imagePath){
        try (InputStream in = QuickCheckReactor.class.getResourceAsStream(imagePath)) { //at main class
            if (in == null) throw new IllegalStateException("Missing image resource: " + imagePath);
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + imagePath, e);
        }
    }

    public void DrawQuickCheckResult(TestResultValue testResultValue){
        //Set the frame
        frame = new JFrame("Quick Check Reactor - Smiley Result");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setUndecorated(borderless); //Makes the window borderless

        frame.getContentPane().setBackground(testResultValue.getPanelbackground());

        frame.setSize(config.getWindowWidth(),config.getWindowHeight());
        frame.setLocation(config.getWindowX(),config.getWindowY());

        frame.setContentPane(new ImagePanel(testResultValue.getEmojimage()));

        //Only set visibility when fully drawn
        frame.setVisible(true);
    }

    public void DestroyResultWindow(){
        frame.dispose();
    }
}

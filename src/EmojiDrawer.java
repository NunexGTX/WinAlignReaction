import Interface.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;

public class EmojiDrawer {
    private ConfigManager config;

    private boolean borderless = true;

    private JFrame frame;

    //Emoji and Emoticon hashmaps
    HashMap<TestResultValue,BufferedImage> resultToEmojimage = new HashMap<>();

    public EmojiDrawer(ConfigManager config){
        this.config = config;
        storeEmojis();
    }

    private void storeEmojis(){
        resultToEmojimage.put(TestResultValue.PASS,loadImage("assets/emojis/grinning.png"));
        resultToEmojimage.put(TestResultValue.WARNING,loadImage("assets/emojis/face_with_diagonal_mouth.png"));
        resultToEmojimage.put(TestResultValue.FAIL,loadImage("assets/emojis/white_frowning_face.png"));
    }

    private BufferedImage loadImage(String imagePath){
        try (InputStream in = getClass().getResourceAsStream(imagePath)) {
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

        frame.setSize(config.getWindowWidth(),config.getWindowHeight());
        frame.setLocation(config.getWindowX(),config.getWindowY());

        frame.setContentPane(new ImagePanel(resultToEmojimage.get(testResultValue)));

        //Only set visibility when fully drawn
        frame.setVisible(true);
    }

    public void DestroyResultWindow(){
        frame.dispose();
    }
}

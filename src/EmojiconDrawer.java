import javax.swing.*;
import java.util.HashMap;

public class EmojiconDrawer {
    private ConfigManager config;

    private JFrame frame;

    //Emoji and Emoticon hashmaps
    HashMap<TestResultValue,String> resultToEmoji = new HashMap<>();
    HashMap<TestResultValue,String> resultToEmoticon = new HashMap<>();

    public EmojiconDrawer(ConfigManager config){
        this.config = config;

        storeEmojis();
        storeEmoticons();
    }

    private void storeEmojis(){
        resultToEmoji.put(TestResultValue.PASS,"😃");
        resultToEmoji.put(TestResultValue.WARNING,"🤨");
        resultToEmoji.put(TestResultValue.FAIL,"😧");
    }

    private void storeEmoticons(){
        resultToEmoticon.put(TestResultValue.PASS,":)");
        resultToEmoticon.put(TestResultValue.WARNING,":/");
        resultToEmoticon.put(TestResultValue.FAIL,":(");
    }

    public void DrawQuickCheckResult(TestResultValue testResultValue){
        //Set the frame
        frame = new JFrame("Quick Check - Smiley Result");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(config.getWindowWidth(),config.getWindowHeight());
        frame.setLocation(config.getWindowX(),config.getWindowY());
        frame.setVisible(true);

        //TODO: Display Emoji or Emoticon
    }

    public void DestroyResultWindow(){
        frame.dispose();
    }
}

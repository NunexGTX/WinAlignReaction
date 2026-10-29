import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public enum TestResultValue {
    ERROR(-1, EmojiDrawer.loadImage("assets/emojis/persevering_face_emoji.png"),Color.GRAY),
    UNDEFINED(0, EmojiDrawer.loadImage("assets/emojis/white_frowning_face.png"),Color.GRAY),
    PASS(1, EmojiDrawer.loadImage("assets/emojis/grinning.png"),Color.GREEN),
    WARNING(2, EmojiDrawer.loadImage("assets/emojis/face_with_diagonal_mouth.png"),Color.yellow),
    FAIL(3, EmojiDrawer.loadImage("assets/emojis/white_frowning_face.png"),Color.RED);

    private final int code;
    private final BufferedImage emojimage;
    private final Color panelbackground;

    TestResultValue(int code, BufferedImage emojimage, Color panelbackground) {
        this.code = code;
        this.emojimage = emojimage;
        this.panelbackground = panelbackground;
    }

    public int getCode() {
        return code;
    }

    public BufferedImage getEmojimage(){
        return emojimage;
    }

    public Color getPanelbackground() {
        return panelbackground;
    }

    public static TestResultValue fromCode(int code) {
            return Arrays.stream(values())
                    .filter(r -> r.code == code)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown test result code: " + code));
    }

}

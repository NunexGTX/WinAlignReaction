package Interface;

import javax.swing.*;
import java.awt.*;

public class MonitorProbe {
    private static GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

    public static GraphicsDevice[] getScreens(){
        return screens;
    }

    public static GraphicsDevice getDefaultDisplay(){
        return screens[0];
    }

    public static GraphicsDevice getLastDisplay(){
        return screens[screens.length-1];
    }

    public static String[] monitorsDescripts(){
        String[] monitorsDescript = new String[screens.length];

        for(int i = 0; i < screens.length; i++){
            monitorsDescript[i] = String.format("Monitor %d: %s", i, screens[i].getIDstring());
        }

        return monitorsDescript;
    }

    public static int[] getScreenBounds(int screenidx){
        Rectangle screenBounds = screens[screenidx].getDefaultConfiguration().getBounds();
        return new int[] {screenBounds.x,screenBounds.y,screenBounds.width,screenBounds.height};
    }

    public static int[] getScreenBounds(GraphicsDevice screen){
        Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
        return new int[] {screenBounds.x,screenBounds.y,screenBounds.width,screenBounds.height};
    }

}

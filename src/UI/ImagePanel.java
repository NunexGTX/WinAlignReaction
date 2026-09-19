package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Draws the image centered, scaled to the largest size that fits the panel. */
public class ImagePanel extends JPanel {
    private final BufferedImage image;

    public ImagePanel(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Largest scale that still fits both dimensions (keeps the aspect ratio)
            double scale = Math.min((double) getWidth() / image.getWidth(),
                    (double) getHeight() / image.getHeight());
            int w = (int) Math.round(image.getWidth() * scale);
            int h = (int) Math.round(image.getHeight() * scale);
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;

            g2.drawImage(image, x, y, w, h, null);
        } finally {
            g2.dispose();
        }
    }
}

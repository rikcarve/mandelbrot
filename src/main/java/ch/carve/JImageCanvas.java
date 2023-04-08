package ch.carve;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Canvas fuer BufferedImages (fuer Swing), erweitert JLabel
 */
public class JImageCanvas extends JLabel {
    private BufferedImage image;

    public JImageCanvas() {
    }

    public JImageCanvas(BufferedImage im) {
        setImage(im);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        setIcon(new ImageIcon(image));
    }
}

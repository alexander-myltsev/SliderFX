package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class JImagePanel extends JPanel {

    private BufferedImage image;

    public JImagePanel(BufferedImage image) {
        this.image = image;
        //Border blackline = BorderFactory.createLineBorder(Color.black);
        //setBorder(blackline);
    }

    public void updateImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        /*
        int w = image.getWidth() / 2;
        int h = image.getHeight() / 2;
        int xStart = (getWidth() - w) / 2;
        int yStart = (getHeight() - h) / 2;
        g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), xStart, yStart, null);
        */

        float fitToFrameScale = Math.min((float) getWidth() / (float) image.getWidth(), (float) getHeight() / (float) image.getHeight());
        int widthScaled = (int) (image.getWidth() * fitToFrameScale);
        int heightScaled = (int) (image.getHeight() * fitToFrameScale);
        int xStart = (getWidth() - widthScaled) / 2;
        int yStart = (getHeight() - heightScaled) / 2;
        g.drawImage(image.getScaledInstance(widthScaled, heightScaled, Image.SCALE_SMOOTH), xStart, yStart, null);
    }
}

package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class JImagePanel extends JPanel {

    private BufferedImage image;

    private static BufferedImage overlayImages(BufferedImage bgImage, BufferedImage fgImage) {

        if (fgImage.getHeight() > bgImage.getHeight() || fgImage.getWidth() > fgImage.getWidth()) {
            JOptionPane.showMessageDialog(null,
                    "Foreground Image Is Bigger In One or Both Dimensions"
                            + "\nCannot proceed with overlay."
                            + "\n\n Please use smaller Image for foreground");
            return bgImage;
        }

        Graphics2D g = bgImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bgImage, 0, 0, null);
        float alpha = 0.2f;
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(composite);
        g.drawImage(fgImage, 350, 200, null);

        g.dispose();
        return bgImage;
    }

    public JImagePanel(String path, boolean isPlayable) {
        try {
            image = ImageIO.read(new File(path));
            if (isPlayable)
                image = JImagePanel.overlayImages(image, ImageIO.read(new File("resource/Silver-Play-Button.jpg")));
            Border blackline = BorderFactory.createLineBorder(Color.black);
            setBorder(blackline);
        } catch (IOException ex) {
            // handle exception...
        }
    }

    public void updateImage(String path, boolean isPlayable) {
        try {
            image = ImageIO.read(new File(path));
            if (isPlayable)
                image = JImagePanel.overlayImages(image, ImageIO.read(new File("resource/Silver-Play-Button.jpg")));
            repaint();
        } catch (IOException ex) {
            // handle exception...
        }
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

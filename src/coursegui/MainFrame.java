package coursegui;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

class JImagePanel extends JPanel {

    private BufferedImage image;

    public JImagePanel(String path) {
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException ex) {
            // handle exception...
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        double imageRatio = (double) image.getWidth() / (double) image.getHeight();
        double panelRatio = (double) getWidth() / (double) getHeight();
        int w, h;
        if (panelRatio > imageRatio) {
            w = Math.min(image.getWidth(), getWidth());
            h = (int) (w / panelRatio);
        } else {
            h = Math.min(image.getHeight(), getHeight());
            w = (int) (h * panelRatio);
        }
        g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
    }
}

public class MainFrame implements ActionListener, MouseListener {
    private static final boolean OPAQUE = false;
    private int currentHeight = 0;
    private int currentWidth = 0;

    private JScrollPane createTextAreaScroll(String text, int rows, int cols, boolean hasVerScroll) {
        JTextArea ta = new JTextArea(text, rows, cols);
        ta.setFont(UIManager.getFont("TextField.font"));
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(
                ta,
                hasVerScroll ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private String news = "January 16-18, 2012. A two day CUDA course at the Technical University of Munich. Germany.\n" +
            "\n" +
            "GPU Computing and CUDA. Supercomputing winter school at MSU for \"T-Platforms'\n" +
            "\n" +
            "APC and NVIDIA hold a \"GPU computing with CUDA\" seminar at the Informatics and Radioelectronics University, Minsk, Belarus\n" +
            "\n" +
            "October 17-19, 2011 A three- day CUDA course at the Supercomputing center of Poznan, Poland.";

    private ArrayList<JButton> getSocialButtons() {
        String[][] pathsToIcons = {
                {"resource/rss-icon.jpg", "rss-clicked"},
                {"resource/youtube-icon.jpg", "youtube-clicked"},
                {"resource/facebook-icon.jpg", "facebook-clicked"},
                {"resource/linkedinicon.jpg", "linkedin-clicked"},
                {"resource/twitter+icon.jpg", "twitter-clicked"}
        };
        ArrayList<JButton> buttons = new ArrayList<JButton>();
        for (int i = 0; i < pathsToIcons.length; i++) {
            JButton button = new JButton("", new ImageIcon(pathsToIcons[i][0]));
            button.addActionListener(new SocialButtonsActionListener());
            button.setActionCommand(pathsToIcons[i][1]);
            buttons.add(button);
        }
        return buttons;
    }

    private JPanel createLectureSelectorPanel() {
        final JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow,fill][grow,fill][grow,fill][grow,fill]10[]",
                "[][c,grow 33,fill][c,grow 67,fill]1[][]"), true);

        JImagePanel lectureContentViewer = new JImagePanel("resource/Lectures/Lecture1/Slide1.PNG");
        lectureContentViewer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lectureContentViewer.addMouseListener(this);
        panel.add(lectureContentViewer, "span 4 3, grow");

        JLabel banner = new JLabel(new ImageIcon("resource/TESLA_200x100.jpg"));
        banner.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(banner, "w 200!, h 100!, wrap, spanx 6");
        panel.add(createTextAreaScroll(news, 4, 30, true), "w 200!, grow, wrap, spanx 6");
        panel.add(createTextAreaScroll("Type your question or query here and click \"send\" to receive a consultation", 4, 30, true), "w 200!, grow, wrap, spanx 6");

        for (int i = 1; i <= 4; i++) {
            //JToggleButton lectureButton = new JToggleButton("Lecture " + i);
            JButton lectureButton = new JButton("Lecture " + i);
            lectureButton.setActionCommand("lecture-" + i);
            //lectureButton.addActionListener(???);
            panel.add(lectureButton, "spany 3,grow");
        }

        JButton sendButton = new JButton("Send");
        panel.add(sendButton, "wrap, gap push");

        ArrayList<JButton> socialButtons = getSocialButtons();
        panel.add(socialButtons.get(0), "gap push,w 35!,cell 4 5");
        for (int i = 1; i < socialButtons.size(); i++) {
            panel.add(socialButtons.get(i), "w 35!,cell 4 5");
        }

        return panel;
    }

    private JPanel createSlidesSelectorPanel() {
        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[][grow,fill][]",
                "[grow,fill][]"), true);

        for (int i = 1; i < 12; i++)
            panel.add(new JButton("Slide" + i), "cell 0 0,flowy,sg g1");

        panel.add(new JImagePanel("resource/Lectures/Lecture1/Slide1.PNG"), "grow");

        JLabel banner = new JLabel(new ImageIcon("resource/TESLA_200x100.jpg"));
        banner.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(banner, "w 200!, h 100!, cell 2 0,flowy");
        panel.add(createTextAreaScroll(news, 4, 30, true), "w 200!, grow, cell 2 0,flowy");
        panel.add(createTextAreaScroll("Type your question or query here and click \"send\" to receive a consultation", 4, 30, true), "w 200!, grow, cell 2 0,flowy");

        panel.add(new JButton("||"), "cell 1 1,w 50!");
        panel.add(new JSlider(0, 100, 0), "cell 1 1,growx 90");
        panel.add(new JButton("Mute"), "cell 1 1,w 65!");
        JSlider jSlider = new JSlider(0, 100, 100);
        panel.add(jSlider, "cell 1 1,w 100!");
        panel.add(new JButton("Send"), "cell 2 1,gapx push,wrap");

        JButton backToLectureSelectionButton = new JButton("<html>Lecture<br/>selection</html>");
        backToLectureSelectionButton.addActionListener(this);
        panel.add(backToLectureSelectionButton, "");

        ArrayList<JButton> socialButtons = getSocialButtons();
        panel.add(socialButtons.get(0), "gap push,w 35!,cell 2 2");
        for (int i = 1; i < socialButtons.size(); i++) {
            panel.add(socialButtons.get(i), "w 35!,cell 2 2");
        }

        return panel;
    }

    JFrame frame = new JFrame("CourseGUI");

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension dimension = toolkit.getScreenSize();
                System.out.println("Width: " + dimension.width);
                System.out.println("Height: " + dimension.height);

                final MainFrame mainFrame = new MainFrame();
                mainFrame.currentHeight = dimension.height;
                mainFrame.currentWidth = dimension.width;
                //JFrame frame = new JFrame("CourseGUI");
                mainFrame.frame.getContentPane().add(mainFrame.createLectureSelectorPanel());
                mainFrame.frame.pack();
                mainFrame.frame.setSize(1200, 600);
                mainFrame.frame.setLocation(100, 100);
                mainFrame.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                mainFrame.frame.setVisible(true);

                mainFrame.frame.addComponentListener(new java.awt.event.ComponentAdapter() {
                    public void componentResized(ComponentEvent event) {
                        mainFrame.currentHeight = Math.max(600, mainFrame.frame.getHeight());
                        mainFrame.currentWidth = Math.max(1200, mainFrame.frame.getWidth());
                        mainFrame.frame.setSize(mainFrame.currentWidth, mainFrame.currentHeight);
                    }
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Back to lecture selection is clicked");
        JPanel lectureSelectorPanel = createLectureSelectorPanel();
        frame.setVisible(false);
        frame.setContentPane(lectureSelectorPanel);
        frame.pack();
        frame.setSize(currentWidth, currentHeight);
        frame.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked");
        JPanel slidesSelectorPanel = createSlidesSelectorPanel();
        frame.setVisible(false);
        frame.setContentPane(slidesSelectorPanel);
        frame.pack();
        frame.setSize(currentWidth, currentHeight);
        frame.setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

class SocialButtonsActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(e.toString());
        try {
            URI u;
            if ("rss-clicked".equals(e.getActionCommand())) u = new URI("http://parallel-compute.com/news/");
            else if ("facebook-clicked".equals(e.getActionCommand())) u = new URI("http://facebook.com");
            else if ("linkedin-clicked".equals(e.getActionCommand())) u = new URI("http://linkedin.com");
            else if ("twitter-clicked".equals(e.getActionCommand())) u = new URI("http://twitter.com");
            else if ("youtube-clicked".equals(e.getActionCommand())) u = new URI("http://youtube.com");
            else u = new URI("http://parallel-compute.com");
            java.awt.Desktop.getDesktop().browse(u);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}

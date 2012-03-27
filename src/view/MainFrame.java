package view;

import controller.*;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

// TODO: Make custom button

public class MainFrame {
    private static Font font = new Font("PT Sans", Font.PLAIN, 14);
    private static final boolean OPAQUE = false;
    private int currentHeight = 0;
    private int currentWidth = 0;
    private String textOfQuestion = "\n\n\nType your question or query here and click \"send\" to receive a consultation";

    private String news;

    private controller.Controller controller;

    public MainFrame(Controller controller) {
        this.controller = controller;
        GetNewsCmd command = new GetNewsCmd();
        controller.executeCommand(command);
        news = command.newsHtml();
    }

    /*
    private void paintGradient(Graphics g, JPanel panel) {
        //UIDefaults uid = UIManager.getDefaults();
        Graphics2D g2d = (Graphics2D) g;
        Dimension d = panel.getSize();

        //g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, d.height, Color.BLACK, true));
        java.awt.geom.Point2D center = new java.awt.geom.Point2D.Float(d.width / 2, d.height / 2);
        float radius = 2 * Math.min(d.width, d.height);
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {new Color(0.0f, 0.0f, 0.0f, 0.0f), Color.BLACK};
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(p);
        g2d.fillRect(0, 0, d.width, d.height);
    }
    */

        String emailSubjectTxt = "A test from gmail";
        String emailFromAddress = "alexander.myltsev@gmail.com";
    private JScrollPane createTextAreaScroll(int rows, int cols, boolean hasVerScroll) {
        final String text = "\n\n\nType your question or query here and click \"send\" to receive a consultation";
        final JTextArea ta = new JTextArea(textOfQuestion, rows, cols);
        ta.setFont(font);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);

        ta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textOfQuestion = ta.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textOfQuestion = ta.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textOfQuestion = ta.getText();
            }
        });

        ta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (ta.getText().equals(text)) {
                    ta.setText("");
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (ta.getText().equals("")) {
                    ta.setText(text);
                    ta.setCaretPosition(0);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(
                ta,
                hasVerScroll ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private ArrayList<JComponent> getSocialButtons() {
        String[][] pathsToIcons = {
                {"resource/rss.png", "http://parallel-compute.com/news/"},
                {"resource/youtube.png", "http://youtube.com"},
                {"resource/facebook.png", "http://facebook.com"},
                {"resource/linkedin.png", "http://linkedin.com"},
                {"resource/twitter.png", "http://twitter.com"}
        };
        ArrayList<JComponent> buttons = new ArrayList<JComponent>();
        class SocialButtonsListener extends MouseAdapter {
            private String siteToGo;

            SocialButtonsListener(String siteToGo) {
                this.siteToGo = siteToGo;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Desktop.getDesktop().browse(new URI(this.siteToGo));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        }
        for (int i = 0; i < pathsToIcons.length; i++) {
            URL iconURL = Thread.currentThread().getContextClassLoader().getResource(pathsToIcons[i][0]);
            JLabel socialButtonLabel = new JLabel(new ImageIcon(iconURL));
            socialButtonLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            socialButtonLabel.addMouseListener(new SocialButtonsListener(pathsToIcons[i][1]));
            buttons.add(socialButtonLabel);
        }
        return buttons;
    }

    /*
    class LectureButtonListener implements ActionListener {
        private JImagePanel lectureContentViewer;

        LectureButtonListener(JImagePanel lectureContentViewer) {
            this.lectureContentViewer = lectureContentViewer;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //lectureNumber = Integer.parseInt(e.getActionCommand());
            //String path = "resource/Lectures/Lecture" + lectureNumber + "/Slide1.PNG";
            //lectureContentViewer.updateImage(path, true);

            int lectNum = Integer.parseInt(e.getActionCommand());
            SelectLectureCmd command = new SelectLectureCmd(lectNum);
            controller.executeCommand(command);
            LectureDescription lectureDescription = command.lectureDescription();
            lectureContentViewer.updateImage(lectureDescription.content());
        }
    }
    */

    private JPanel createLectureSelectorPanel() {
        final JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow,fill][]",
                "[grow,fill][]"), true
        ) {
            /*
            @Override
            protected void paintComponent(Graphics g) {
                paintGradient(g, this);
            }
            */
        };

        GetCurrentLectureCmd getCurrentLectureCmd = new GetCurrentLectureCmd();
        controller.executeCommand(getCurrentLectureCmd);
        final JImagePanel lectureContentViewer = new JImagePanel(getCurrentLectureCmd.content().getContent());
        lectureContentViewer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lectureContentViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Mouse clicked");
                JPanel slidesSelectorPanel = createSlidesSelectorPanel();
                frame.setVisible(false);
                frame.setContentPane(slidesSelectorPanel);
                frame.pack();
                frame.setSize(currentWidth, currentHeight);
                frame.setVisible(true);
            }
        });
        panel.add(lectureContentViewer, "cell 0 0");

        panel.add(createBanner(), "w 200!, h 100!, flowy, cell 1 0");
        panel.add(createNewsPanel(news, true), "w 200!, h 50%,flowy, cell 1 0");
        JLabel askQuestionLabel = new JLabel("Ask a question:");
        askQuestionLabel.setFont(font);
        askQuestionLabel.setForeground(new Color(47, 103, 166));
        panel.add(askQuestionLabel, "gapy 30, cell 1 0");
        panel.add(createTextAreaScroll(4, 30, true), "w 200!, h 50%,flowy, grow, cell 1 0");
        panel.add(createSendButton(), "gapx push, gapy 0px, cell 1 0, h 35!, w 96!"); // pos visual.x2-pref visual.y2-pref-35

        //LectureButtonListener lectureButtonActionListener = new LectureButtonListener(lectureContentViewer);
        URL buttonSlideURL = Thread.currentThread().getContextClassLoader().getResource("resource/button_lecture.png");

        try {
            final BufferedImage bufferedImage = ImageIO.read(buttonSlideURL);
            GetLecturesDescriptionsCmd getLecturesDescriptionsCmd = new GetLecturesDescriptionsCmd();
            controller.executeCommand(getLecturesDescriptionsCmd);
            for (int i = 0; i < getLecturesDescriptionsCmd.lecturesDescriptions().length; i++) {
                LectureDescription lectureDescription = getLecturesDescriptionsCmd.lecturesDescriptions()[i];

                //JButton lectureButton = new JButton("Lecture " + i);
                JLabel lectureButton = new JLabel(lectureDescription.getInformation(), SwingConstants.CENTER) {
                    @Override
                    public void paint(Graphics g) {
                        //int imageWidth = bufferedImage.getWidth();
                        //int imageHeight = bufferedImage.getHeight();
                        //int x = (getWidth() - imageWidth) / 2;
                        //int y = (getHeight() - imageHeight) / 2 + 1;
                        g.drawImage(bufferedImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
                        super.paint(g);
                    }
                };
                lectureButton.setFont(font);
                //lectureButton.setForeground(new Color(47, 103, 166));
                lectureButton.setForeground(new Color(255, 255, 255));
                final int finalI = i;
                lectureButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SelectLectureCmd command = new SelectLectureCmd(finalI);
                        controller.executeCommand(command);
                        LectureDescription lectureDescription = command.lectureDescription();
                        lectureContentViewer.updateImage(lectureDescription.getContent());
                    }
                });
                panel.add(lectureButton, "cell 0 1,h 35!,flowx");
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        ArrayList<JComponent> socialButtons = getSocialButtons();
        for (int i = 0; i < socialButtons.size(); i++) {
            panel.add(socialButtons.get(i), "gapx push,gapy push,w 35!,h 35!,cell 1 1");
        }

        return panel;
    }

    private JComponent createSendButton() {
        URL buttonSlideURL = Thread.currentThread().getContextClassLoader().getResource("resource/button_send.png");
        final BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(buttonSlideURL);
            JLabel sendButton = new JLabel("", SwingConstants.CENTER) {
                public void paint(Graphics g) {
                    int x = (getWidth() - bufferedImage.getWidth()) / 2;
                    int y = (getHeight() - bufferedImage.getHeight()) / 2 + 1;
                    g.drawImage(bufferedImage, x, y, null);
                    super.paint(g);
                }
            };
            //sendButton.setFont(font);
            //sendButton.setForeground(new Color(47, 103, 166));
            sendButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    controller.executeCommand(new SendQuestionCmd(textOfQuestion));
                }
            });
            return sendButton;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private JScrollPane createNewsPanel(String news, boolean hasVerScroll) {
        JEditorPane ep = new JEditorPane();
        ep.setContentType("text/html");
        ep.setText(news);
        ep.setEditable(false);
        ep.setFont(font);
        ep.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                //System.out.println(e);

                try {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                        Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        ep.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(
                ep,
                hasVerScroll ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel createSlidesSelectorPanel() {
        MigLayout migLayout = new MigLayout(
                "insets 10",
                "[][grow,fill][]",
                "[grow,fill][]");
        JPanel panel = new JPanel(migLayout, true);
        JPanel slidesButtonsPanel = new JPanel(new MigLayout("insets 0", "[]", "[]"), true);
        JScrollPane slidesButtonsScrollPane = new JScrollPane(slidesButtonsPanel);
        slidesButtonsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        GetCurrentSlideCmd getCurrentSlideCmd = new GetCurrentSlideCmd();
        controller.executeCommand(getCurrentSlideCmd);
        final JImagePanel slideSelectorPanel = new JImagePanel(getCurrentSlideCmd.content().getContent());


        URL buttonSlideURL = Thread.currentThread().getContextClassLoader().getResource("resource/button_slide.png");
        try {
            final BufferedImage bufferedImage = ImageIO.read(buttonSlideURL);
            //ImageIcon imageIcon = new ImageIcon(buttonSlideURL);
            GetSlidesCmd getSlidesCmd = new GetSlidesCmd();
            controller.executeCommand(getSlidesCmd);

            URL mediaUrl = getSlidesCmd.slidesInfo()[0].getMediaURL();
            final JAudioPanel jAudioPanel = new JAudioPanel(mediaUrl);

            for (int i = 0; i < getSlidesCmd.slidesInfo().length; i++) {
                SlideInfo slideInfo = getSlidesCmd.slidesInfo()[i];

                JLabel slideButton = new JLabel(slideInfo.getTitle(), SwingConstants.CENTER) {
                    public void paint(Graphics g) {
                        int x = (getWidth() - bufferedImage.getWidth()) / 2;
                        int y = (getHeight() - bufferedImage.getHeight()) / 2 + 1;
                        g.drawImage(bufferedImage, x, y, null);
                        super.paint(g);
                    }
                };
                slideButton.setFont(font);
                slideButton.setForeground(new Color(47, 103, 166));
                final int slideNum = i;
                slideButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SelectSlideCmd selectSlideCmd = new SelectSlideCmd(slideNum);
                        controller.executeCommand(selectSlideCmd);
                        slideSelectorPanel.updateImage(selectSlideCmd.slideInfo().getContent());
                        //jAudioPanel.play("E:/temp/music/music/onclassical_demo_luisi_chopin_scherzo_2_31_small-version_i-middle.wav");
                        jAudioPanel.play(selectSlideCmd.slideInfo().getMediaURL());
                    }
                });
                //panel.add(slideButton, "cell 0 0,sg g1,w 74!,flowy");
                slidesButtonsPanel.add(slideButton, "cell 0 0,sg g1,w 74!,flowy");
            }

            panel.add(slidesButtonsScrollPane, "cell 0 0, w 95!");
            panel.add(slideSelectorPanel, "grow,flowy");
            panel.add(createBanner(), "w 200!, h 100!, cell 2 0,flowy");
            panel.add(createNewsPanel(news, true), "w 200!, h 50%, grow, cell 2 0,flowy");
            JLabel askQuestionLabel = new JLabel("Ask a question:");
            askQuestionLabel.setForeground(new Color(47, 103, 166));
            askQuestionLabel.setFont(font);
            panel.add(askQuestionLabel, "gapy 30, cell 2 0");
            panel.add(createTextAreaScroll(4, 30, true), "w 200!, h 50%, grow, cell 2 0,flowy");
            panel.add(createSendButton(), "cell 2 0, gapx push, gapy 0px, h 35!, w 96!");
            jAudioPanel.addListener(new JAudioPanelListener() {
                @Override
                public void trackIsEnded() {
                    //jAudioPanel.play("E:/temp/music/music/onclassical_demo_luisi_chopin_scherzo_2_31_small-version_i-middle.wav");
                    SelectNextSlideCmd cmd = new SelectNextSlideCmd();
                    controller.executeCommand(cmd);
                    jAudioPanel.play(cmd.slideInfo().getMediaURL());
                    slideSelectorPanel.updateImage(cmd.slideInfo().getContent());
                }
            });
            panel.add(jAudioPanel, "cell 1 1");

            //JButton backToLectureSelectionButton = new JButton("<html><p style=\"text-align: center;\">Lecture<br/>selection</p></html>");
            JLabel backToLectureSelectionButton = new JLabel("<html><p style=\"text-align: center;\">Lecture<br/>selection</p></html>", SwingConstants.CENTER) {
                public void paint(Graphics g) {
                    int imageWidth = bufferedImage.getWidth();
                    int imageHeight = bufferedImage.getHeight() * 2;
                    int x = (getWidth() - imageWidth) / 2;
                    int y = (getHeight() - imageHeight) / 2 + 1;
                    g.drawImage(bufferedImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH), x, y, null);
                    super.paint(g);
                }
            };
            backToLectureSelectionButton.setFont(font);
            backToLectureSelectionButton.setForeground(new Color(47, 103, 166));
            backToLectureSelectionButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back to lecture selection is clicked");
                    jAudioPanel.stop();
                    JPanel lectureSelectorPanel = createLectureSelectorPanel();
                    frame.setVisible(false);
                    frame.setContentPane(lectureSelectorPanel);
                    frame.pack();
                    frame.setSize(currentWidth, currentHeight);
                    frame.setVisible(true);
                }
            });
            panel.add(backToLectureSelectionButton, "w 80!,h 40!,cell 0 1");

            ArrayList<JComponent> socialButtons = getSocialButtons();
            for (int i = 0; i < socialButtons.size(); i++) {
                panel.add(socialButtons.get(i), "gapx push,gapy push,w 35!,h 35!,cell 2 1");
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return panel;
    }

    private JLabel createBanner() {
        URL bannerURL = Thread.currentThread().getContextClassLoader().getResource("resource/TESLA_200x100.jpg");
        JLabel banner = new JLabel(new ImageIcon(bannerURL));
        banner.setCursor(new Cursor(Cursor.HAND_CURSOR));
        banner.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.nvidia.com/object/workstation-solutions-tesla.html"));
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        return banner;
    }

    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    JFrame frame = new JFrame("Training course by Applied Parallel Computing");

    public static void launch(final Controller controller) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    /*
                    LookAndFeels:
                    Metal
                    Nimbus
                    CDE/Motif
                    Windows
                    Windows Classic

                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        System.out.println(info.getName());
                    }
                     */
                    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//                    Painter<Component> painter = new Painter<Component>() {
//                        public void paint(Graphics2D g, Component c, int width, int height) {
//                            g.setColor(c.getBackground());
//                            //and so forth
//                        }
//                    };
                    //UIManager.put("ProgressBar[Enabled].foregroundPainter", painter);
                    //UIManager.put("ProgressBar[Enabled].backgroundPainter", painter);

                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }

                    setUIFont(new javax.swing.plaf.FontUIResource(font));
                    UIManager.put("nimbusOrange", new Color(53, 137, 199));
                    UIManager.put("background", new Color(201, 212, 216));

                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Dimension dimension = toolkit.getScreenSize();
                    System.out.println("Width: " + dimension.width);
                    System.out.println("Height: " + dimension.height);

                    final MainFrame mainFrame = new MainFrame(controller);

                    mainFrame.currentHeight = (int) (dimension.height * 0.8);
                    mainFrame.currentWidth = (int) (dimension.width * 0.8);

                    int startX = (dimension.width - mainFrame.currentWidth) / 2;
                    int startY = (dimension.height - mainFrame.currentHeight) / 2;

                    //JFrame frame = new JFrame("CourseGUI");

                    // MAIN PANEL SELECTOR
                    //mainFrame.frame.getContentPane().add(mainFrame.createContactInformationPanel());
                    mainFrame.frame.getContentPane().add(mainFrame.createLectureSelectorPanel());
                    //mainFrame.frame.getContentPane().add(mainFrame.createSlidesSelectorPanel());

                    mainFrame.frame.pack();
                    mainFrame.frame.setSize(mainFrame.currentWidth, mainFrame.currentHeight);
                    mainFrame.frame.setLocation(startX, startY);
                    mainFrame.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    mainFrame.frame.setVisible(true);

                    mainFrame.frame.addComponentListener(new java.awt.event.ComponentAdapter() {
                        public void componentResized(ComponentEvent event) {
                            mainFrame.currentHeight = mainFrame.frame.getHeight();
                            mainFrame.currentWidth = mainFrame.frame.getWidth();
                            //mainFrame.currentHeight = Math.max(600, mainFrame.frame.getHeight());
                            //mainFrame.currentWidth = Math.max(1200, mainFrame.frame.getWidth());
                            //mainFrame.frame.setSize(mainFrame.currentWidth, mainFrame.currentHeight);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private JPanel createContactInformationPanel() {
        MigLayout colLM = new MigLayout(
                "",
                "[25%][fill][grow][25%]",
                "[33%][][][]");

        URL backgroundImageURL = Thread.currentThread().getContextClassLoader().getResource("resource/background.jpg");

        try {
            final BufferedImage bufferedImage = ImageIO.read(backgroundImageURL);
            JPanel panel = new JPanel(colLM) {
                private int width = -1;
                private int height = -1;
                private Image scaledBufferedImage = null;

                @Override
                protected void paintComponent(Graphics g) {
                    if (this.width != getWidth() || this.height != getHeight()) {
                        this.width = getWidth();
                        this.height = getHeight();
                        scaledBufferedImage = bufferedImage.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH);
                    }
                    g.drawImage(scaledBufferedImage, 0, 0, null);
                }
            };

            JLabel apcText = new JLabel("APPLIED PARALLEL COMPUTING");
            Font font1 = new Font(font.getName(), font.getStyle(), font.getSize() + 3);
            apcText.setFont(font1);
            apcText.setForeground(new Color(47, 103, 166));
            panel.add(apcText, "cell 2 1");
            URL apclogoURL = Thread.currentThread().getContextClassLoader().getResource("resource/logo_APC.png");
            panel.add(new JLabel(new ImageIcon(apclogoURL)), "cell 1 1");

            JLabel fullNameLabel = new JLabel("Full Name:");
            panel.add(fullNameLabel, "cell 1 2");
            final JTextField fullNameText = new JTextField();
            panel.add(fullNameText, "cell 2 2,growx,wrap");

            JLabel emailLabel = new JLabel("Email:");
            panel.add(emailLabel, "cell 1 3");
            final JTextField emailText = new JTextField();
            panel.add(emailText, "cell 2 3,growx,wrap");

            JLabel organizationLabel = new JLabel("Organization:");
            panel.add(organizationLabel, "cell 1 4");
            final JTextField organizationText = new JTextField();
            panel.add(organizationText, "cell 2 4,growx,wrap");

            JLabel keyLabel = new JLabel("ID key:");
            panel.add(keyLabel, "cell 1 5");

            JTextField keytosendText = new JTextField(".JCKBH3J.CN7016681H097Z.BFEBFBFF000006FD");
            keytosendText.setEditable(false);
            panel.add(keytosendText, "cell 2 5,growx,wrap");

            JLabel activationKeyLabel = new JLabel("Activation key:");
            panel.add(activationKeyLabel, "cell 1 6");
            panel.add(new JTextField(), "cell 2 6,growx,wrap");

            URL registerButtonURL = Thread.currentThread().getContextClassLoader().getResource("resource/button_register.png");
            final BufferedImage bufferedImage1 = ImageIO.read(registerButtonURL);
            JLabel registrationButton = new JLabel("Register", SwingConstants.CENTER) {
                @Override
                public void paint(Graphics g) {
                    int width = bufferedImage1.getWidth() / 2;
                    int height = bufferedImage1.getHeight() / 2;
                    g.drawImage(bufferedImage1.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
                    super.paint(g);
                }
            };
            registrationButton.setFont(font);
            registrationButton.setForeground(new Color(255, 255, 255));
            registrationButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Contacts contacts = new Contacts(fullNameText.getText(), organizationText.getText(), emailText.getText());
                    UpdateContactsCmd updateContactsCmd = new UpdateContactsCmd(contacts);
                    controller.executeCommand(updateContactsCmd);

                    System.out.println("Pass registration is clicked");
                    JPanel lectureSelectorPanel = createLectureSelectorPanel();
                    frame.setVisible(false);
                    frame.setContentPane(lectureSelectorPanel);
                    frame.pack();
                    frame.setSize(currentWidth, currentHeight);
                    frame.setVisible(true);
                }
            });
            panel.add(registrationButton, "cell 2 7, gapx push, w 84!, h 25!");

            JComponent[] controls = {fullNameLabel, emailLabel, keyLabel, keytosendText, organizationLabel, activationKeyLabel};
            for (int i = 0; i < controls.length; i++) {
                controls[i].setFont(font);
                controls[i].setForeground(new Color(47, 103, 166));
            }

            //panel.add(new JSlider(), "cell 2 7, w 600!");

            return panel;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
package view;

import net.miginfocom.swing.MigLayout;

import javax.media.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

interface JAudioPanelListener {
    void trackIsEnded();
}

public class JAudioPanel extends JPanel {
    private JAudioPanelListener listener = null;
    private Player audioPlayer;
    private JSlider audioPlayerLocator;

    public JAudioPanel() {
        try {
            this.setLayout(new MigLayout("", "[][grow,fill][][]", "[]"));
            this.setBackground(new Color(0, 0, 0, 0));

            String path = "E:/temp/music/music/onclassical_demo_fiati-di-parma_thuille_terzo-tempo_sestetto_small-version.wav";
            File audioFile = new File(path);

            audioPlayer = Manager.createRealizedPlayer(audioFile.toURL());
            audioPlayer.start();

            final JButton jButton = new JButton("|>");
            jButton.addActionListener(new ActionListener() {
                private boolean isPlaying = true;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isPlaying) {
                        audioPlayer.stop();
                        jButton.setText("|>");
                    } else {
                        audioPlayer.start();
                        jButton.setText("||");
                    }
                    isPlaying = !isPlaying;
                }
            });

            int seconds = (int) audioPlayer.getDuration().getSeconds();
            audioPlayerLocator = new JSlider(0, seconds, 0);
            audioPlayerLocator.setUI(new BasicSliderUI(audioPlayerLocator));
            audioPlayerLocator.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    double seconds = (double) audioPlayerLocator.getValue();
                    System.out.println("Mouse location clicked to: " + seconds);
                    audioPlayer.setMediaTime(new Time(seconds));
                }
            });

            final JSlider volumeSlider = new JSlider(0, 100, 50);
            volumeSlider.setUI(new BasicSliderUI(volumeSlider));

            Timer timer = new Timer(300, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    float volume = (float) volumeSlider.getValue() / (float) volumeSlider.getMaximum();
                    //System.out.println("Mouse volume clicked to: " + volume);
                    audioPlayer.getGainControl().setLevel(volume);

                    int mediaTime = (int) Math.floor(audioPlayer.getMediaTime().getSeconds());
                    audioPlayerLocator.setValue(mediaTime);
                    System.out.println("Times: " + audioPlayer.getMediaTime().getSeconds() + " | " + audioPlayer.getDuration().getSeconds());
                    if (listener != null &&
                            Math.floor(audioPlayer.getMediaTime().getSeconds()) >= Math.floor(audioPlayer.getDuration().getSeconds())) {
                        listener.trackIsEnded();
                    }
                }
            });
            timer.setInitialDelay(1000);
            timer.start();

            this.add(jButton, "h 35!,w 50!");
            this.add(audioPlayerLocator, "h 35!, growx");
            this.add(new JLabel("V"), "h 35!,w 35!");
            this.add(volumeSlider, "h 35!,w 100!");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    void addListener(JAudioPanelListener jAudioPanelListener) {
        this.listener = jAudioPanelListener;
    }

    public void play(String path) {
        try {
            audioPlayer = Manager.createRealizedPlayer(new File(path).toURL());
            audioPlayer.start();

            int seconds = (int) audioPlayer.getDuration().getSeconds();
            audioPlayerLocator.setValue(0);
            audioPlayerLocator.setMaximum(seconds);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoPlayerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CannotRealizeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void stop() {
        audioPlayer.stop();
        audioPlayer.close();
    }
}

package view;

import net.miginfocom.swing.MigLayout;

import javax.media.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

interface JAudioPanelListener {
    void trackIsEnded();
}

public class JAudioPanel extends JPanel {
    private JAudioPanelListener listener = null;
    private Player audioPlayer;
    //private JSlider audioPlayerLocator;
    private JProgressBar audioPlayerLocator;
    private int scale = 3;

    public JAudioPanel() {
        try {
            this.setLayout(new MigLayout("", "[][grow,fill][][]", "[]"));
            //this.setBackground(new Color(0, 0, 0, 0));

            String path = "E:/temp/music/music/onclassical_demo_ensemble-la-tempesta_porpora_iii-notturno_iii-lezione_live_small-version.wav";
            File audioFile = new File(path);

            audioPlayer = Manager.createRealizedPlayer(audioFile.toURL());
            audioPlayer.start();
            audioPlayer.getGainControl().setLevel(0.5f);

            URL buttonPauseImgURL = ClassLoader.getSystemResource("resource/button_pause.png");
            URL buttonPlayImgURL = ClassLoader.getSystemResource("resource/button_play.png");
            final ImageIcon pauseIcon = new ImageIcon(buttonPauseImgURL);
            final ImageIcon playIcon = new ImageIcon(buttonPlayImgURL);

            final JLabel playPauseButton = new JLabel(pauseIcon);
            playPauseButton.addMouseListener(new MouseAdapter() {
                private boolean isPlaying = true;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isPlaying) {
                        audioPlayer.stop();
                        playPauseButton.setIcon(playIcon);
                    } else {
                        audioPlayer.start();
                        playPauseButton.setIcon(pauseIcon);
                    }
                    isPlaying = !isPlaying;

                }
            });

            int seconds = (int) audioPlayer.getDuration().getSeconds();
            //audioPlayerLocator = new JSlider(0, seconds * scale, 0);
            audioPlayerLocator = new JProgressBar(0, seconds * scale);

            audioPlayerLocator.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    float s = (float) e.getX() / (float) audioPlayerLocator.getWidth();
                    audioPlayerLocator.setValue((int) (s * audioPlayerLocator.getMaximum()));
                    double seconds = (double) audioPlayerLocator.getValue() / (double) scale;
                    audioPlayer.setMediaTime(new Time(seconds));
                }
            });

            final JSlider volumeSlider = new JSlider(0, 100, 50);

            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    float volume = (float) volumeSlider.getValue() / (float) volumeSlider.getMaximum();
                    volume = volume / 2.0f; // Due to BUG: https://forums.oracle.com/forums/thread.jspa?threadID=1277232
                    //System.out.println("volume: " + volume);
                    audioPlayer.getGainControl().setLevel(volume);

                    int mediaTime = (int) (scale * audioPlayer.getMediaTime().getSeconds());
                    audioPlayerLocator.setValue(mediaTime);

                    //System.out.println("Times: " + audioPlayer.getMediaTime().getSeconds() + " | " + audioPlayer.getDuration().getSeconds());
                    if (listener != null &&
                            Math.floor(audioPlayer.getMediaTime().getSeconds()) >= Math.floor(audioPlayer.getDuration().getSeconds())) {
                        listener.trackIsEnded();
                    }
                }
            });
            timer.setInitialDelay(1000);
            timer.start();

            this.add(playPauseButton, "h 35!,w 50!");
            this.add(audioPlayerLocator, "h 35!, growx");
            URL buttonVolumeImgURL = ClassLoader.getSystemResource("resource/button_volume.png");
            this.add(new JLabel(new ImageIcon(buttonVolumeImgURL)), "h 35!,w 50!");
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
            audioPlayer.stop();
            audioPlayer.close();
            audioPlayer = Manager.createRealizedPlayer(new File(path).toURL());
            audioPlayer.getGainControl().setLevel(0.5f);
            audioPlayer.start();

            int seconds = (int) audioPlayer.getDuration().getSeconds();
            audioPlayerLocator.setValue(0);
            audioPlayerLocator.setMaximum(seconds * scale);
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

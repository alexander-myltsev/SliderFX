package view;

import net.miginfocom.swing.MigLayout;

import javax.media.Manager;
import javax.media.Player;
import javax.media.Time;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class JAudioPanel extends JPanel {
    public JAudioPanel() {
        try {
            this.setLayout(new MigLayout("", "[][grow,fill][][]", "[]"));
            this.setBackground(new Color(0, 0, 0, 0));

            String path = "E:/temp/music/music/onclassical_demo_fiati-di-parma_thuille_terzo-tempo_sestetto_small-version.wav";
            File audioFile = new File(path);

            final Player audioPlayer = Manager.createRealizedPlayer(audioFile.toURL());

            final JButton jButton = new JButton("|>");
            jButton.addActionListener(new ActionListener() {
                private boolean isPlaying = false;

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
            final JSlider audioPlayerLocator = new JSlider(0, seconds, 0);
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

            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int mediaTime = (int) Math.round(audioPlayer.getMediaTime().getSeconds());
                    audioPlayerLocator.setValue(mediaTime);
                }
            });
            timer.setInitialDelay(1000);
            timer.start();

            final JSlider volumeSlider = new JSlider(0, 100, 50);
            volumeSlider.setUI(new BasicSliderUI(volumeSlider));
            volumeSlider.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    float volume = (float) volumeSlider.getValue() / (float) volumeSlider.getMaximum();
                    System.out.println("Mouse voolume clicked to: " + volume);
                    audioPlayer.getGainControl().setLevel(volume);
                }
            });

            this.add(jButton, "h 35!,w 50!");
            this.add(audioPlayerLocator, "h 35!, growx");
            this.add(new JLabel("V"), "h 35!,w 35!");
            this.add(volumeSlider, "h 35!,w 100!");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //audioPlayer.setMediaTime(new Time(10.0));
        //audioPlayer.start();
        //DefaultControlPanel controlPanelComponent = (DefaultControlPanel)audioPlayer.getControlPanelComponent();
        //panel.add(controlPanelComponent, "cell 1 2");
    }


}

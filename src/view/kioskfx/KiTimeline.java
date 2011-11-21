package kioskfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.Vector;

public class KiTimeline {

    private int duration;
    private Vector<KiJob> jobs = new Vector<KiJob>();

    public KiTimeline duration(int t) {
        duration = t;
        return this;
    }

    public KiTimeline job(KiJob t) {
        this.jobs.add(t);
        return this;
    }

    public void start() {
        if (jobs.size() < 2) {
            //System.err.println("" + this.getClass().getName() + ": jobs.size() < 2");
            return;
        }
        int step = duration / (jobs.size() - 1);

        Timeline timeline = new Timeline();
        for (int i = 0; i < jobs.size(); i++) {
            final KiJob job = jobs.get(i);
            EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    job.start();
                }
            };
            Duration delay = new Duration(step * i);
            KeyFrame frame = new KeyFrame(delay, handler);
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }
}

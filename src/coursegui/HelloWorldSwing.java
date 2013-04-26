package coursegui;

/*
import net.miginfocom.swing.MigLayout;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class HelloWorldSwing {
    private static boolean buttonOpaque = true;
	private static boolean contentAreaFilled = true;

    private static JComponent createPanel123(String s) {
        JLabel panel = new JLabel(s, SwingConstants.CENTER) {
            public void addNotify() {
                super.addNotify();
                if (getText().length() == 0) {
                    String lText = (String) ((MigLayout) getParent().getLayout()).getComponentConstraints(this);
                    setText(lText != null && lText.length() > 0 ? lText : "<Empty>");
                }
            }
        };
        panel.setBorder(new EtchedBorder());
        panel.setOpaque(true);

        return panel;
    }

    private JButton createButton(String text) {
        return createButton(text, false);
    }

    private JButton createButton(String text, boolean bold) {
        JButton b = new JButton(text) {
            public void addNotify() {
                super.addNotify();
                if (getText().length() == 0) {
                    String lText = (String) ((MigLayout) getParent().getLayout()).getComponentConstraints(this);
                    setText(lText != null && lText.length() > 0 ? lText : "<Empty>");
                }
            }
        };

        if (bold)
            b.setFont(b.getFont().deriveFont(Font.BOLD));

        b.setOpaque(buttonOpaque); // Or window's buttons will have strange border
        b.setContentAreaFilled(contentAreaFilled);

        return b;
    }

    private static JPanel createPanel1() {
        JPanel panel = new JPanel(new MigLayout());

        panel.add(new JLabel("First Name"));
        panel.add(new JTextField(10));
        panel.add(new JLabel("Surname"), "gap unrelated");  // Unrelated size is resolved per platform
        panel.add(new JTextField(10), "wrap");           // Wraps to the next row
        panel.add(new JLabel("Address"));
        panel.add(new JTextField(), "span, grow");    // Spans cells in row and grows to fit that


        //ImageIcon imageIcon = new ImageIcon("resource/rss-icon.jpg");
        //ImageIcon imageIcon = new ImageIcon("e:\\Projects\\ParallelCompute\\CourseGUI\\miglayout\\resourсe\\rss-icon.jpg");
        //JButton jButton = new JButton("", imageIcon);
        //panel.add(jButton, "pos visual.x2-pref visual.y2-pref");


        String filePath = "e:\\Projects\\ParallelCompute\\MSU-GPU-book\\msu-gpu-book\\boreskov\\tex\\slides\\123.pdf";
        SwingController controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        JPanel viewerComponentPanel = factory.buildViewerPanel();
        ComponentKeyBinding.install(controller, viewerComponentPanel);
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));
        JFrame applicationFrame = new JFrame();
        applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationFrame.getContentPane().add(viewerComponentPanel);
        controller.openDocument(filePath);
        panel.add(viewerComponentPanel);


        return panel;
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 40"));

        panel.add(createPanel123("abc1"), "dock west,w 150!");
        panel.add(createPanel123("abc2"), "dock south");
        panel.add(createPanel123("abc3"));


        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Example 01");
                frame.getContentPane().add(createPanel());
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
*/
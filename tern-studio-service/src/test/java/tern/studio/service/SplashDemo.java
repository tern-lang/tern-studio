package tern.studio.service;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SplashDemo {

    private JDialog dialog;
    private JFrame frame;
    private JProgressBar progress;

    protected void initUI() throws MalformedURLException {
        showSplashScreen();
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep(100);// Simulate loading
                    publish(i);// Notify progress
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                progress.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                showFrame();
                hideSplashScreen();
            }

        };
        worker.execute();
    }

    protected void hideSplashScreen() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    protected void showSplashScreen() throws MalformedURLException {
        dialog = new JDialog((Frame) null);
        dialog.setModal(false);
        dialog.setUndecorated(true);
        JLabel background = new JLabel(new ImageIcon("C:/temp/info.png"));
        background.setLayout(new BorderLayout());
        dialog.add(background);
        JLabel text = new JLabel("Loading, please wait...");
        text.setForeground(Color.WHITE);
        text.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));
        background.add(text);
        progress = new JProgressBar();
        background.add(progress, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    protected void showFrame() {
        frame = new JFrame(SplashDemo.class.getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel ui = new JLabel("UI loaded and ready");
        ui.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
        frame.add(ui);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    new SplashDemo().initUI();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
package org.ternlang.studio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ternlang.studio.resource.ClassPathReader;

import lombok.SneakyThrows;

public class SplashDemo {

    private JDialog dialog;

    protected void initUI() throws MalformedURLException {
        showSplashScreen();
    }

    protected void hideSplashScreen() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    @SneakyThrows
    protected void showSplashScreen() {
        dialog = new JDialog((Frame) null);
        dialog.setModal(false);
        dialog.setUndecorated(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screen.width, screen.height);
        InputStream data = ClassPathReader.findResourceAsStream("resource/img/logo.png");        
        BufferedImage image = ImageIO.read(data);
        JLabel background = new JLabel(new ImageIcon(image));
        background.setSize(screen.width, screen.height);
        background.setLayout(new BorderLayout()); 
        JPanel panel = new JPanel();
        JPanel spacer = new JPanel();
        spacer.setSize(10, 10);
        spacer.setBackground(Color.WHITE);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.decode("505050")));
        panel.add(spacer);
        panel.add(background);        
        JLabel text = new JLabel("Loading, please wait...");
        text.setForeground(Color.WHITE);
        text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));       
        panel.add(text);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true); 
        
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
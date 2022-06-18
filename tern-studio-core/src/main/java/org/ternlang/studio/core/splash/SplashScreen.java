package org.ternlang.studio.core.splash;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.simpleframework.module.common.ClassPathReader;
import org.simpleframework.module.common.ThreadBuilder;
import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.ui.WindowIcon;
import org.ternlang.ui.WindowIconLoader;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplashScreen {

   private static final SplashPanel INSTANCE = createPanel(
           "Starting ...",
           "resource/img/logo.png",
           "resource/img/icon-large.png",
           "0xffffff",
           "0x505050"
   );

   public static SplashPanel getPanel() {
      return INSTANCE;
   }
   
   public static void main(String[] list) throws Exception {
      getPanel().show(100000);
   }

   private static SplashPanel createPanel(final String resource, final String background, final String icon, final String foreground, final String message) {
      final CompletableFuture<SplashPanel> future = new CompletableFuture<SplashPanel>();
      final SplashPanelController controller = new SplashPanelController(future);

      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  SplashPanel panel = createSplashScreen(resource, background, icon, foreground, message);

                  future.complete(panel);
                  controller.start();
               } catch (Exception e) {
                  log.info("Could not create splash dialog", e);
               }
            }
         });
      } catch (Exception e) {
         log.info("Could not create splash panel", e);
      }
      ProgressManager.setProgress(controller);
      return controller;
   }

   private static class SplashPanelController implements SplashPanel, Runnable {

      private final Future<SplashPanel> panel;
      private final BlockingQueue<Runnable> tasks;
      private final ThreadBuilder builder;
      private final AtomicBoolean active;
      private final AtomicLong expiry;

      public SplashPanelController(Future<SplashPanel> panel) {
         this.tasks = new ArrayBlockingQueue<Runnable>(1000);
         this.builder = new ThreadBuilder(true);
         this.active = new AtomicBoolean();
         this.expiry = new AtomicLong();
         this.panel = panel;
      }

      @Override
      public void update(final String message) {
         tasks.offer(new Runnable() {
            @Override
            public void run() {
               try {
                  log.debug("Processing splash message: {}", message);
                  panel.get(4, TimeUnit.SECONDS).update(message);
               } catch (Exception e) {
                  log.debug("Could not update splash panel", e);
               }
            }
         });
      }

      @Override
      public void show(final long duration) {
         long time = System.currentTimeMillis();

         expiry.set(time + duration);
         tasks.offer(new Runnable() {
            @Override
            public void run() {
               try {
                  panel.get(4, TimeUnit.SECONDS).show(duration);
               } catch (Exception e) {
                  log.debug("Could not show splash panel", e);
               }
            }
         });
      }

      @Override
      public void dispose() {
         tasks.offer(new Runnable() {
            @Override
            public void run() {
               try {
                  panel.get(4, TimeUnit.SECONDS).dispose();
               } catch (Exception e) {
                  log.debug("Could not hide splash panel", e);
               }
            }
         });
      }

      @Override
      public void run() {
         try {
            while (active.get()) {
               long time = System.currentTimeMillis();
               long threshold = expiry.get();

               if (time > threshold) {
                  active.set(false);
               }
               process(1);
               Thread.sleep(100);
            }
         } catch (Exception e) {
            log.debug("Could not process splash panel events", e);
         } finally {
            dispose();
            active.set(false);
            process(Integer.MAX_VALUE);
         }
      }

      private void process(int count) {
         try {
            while (count-- > 0) {
               Runnable task = tasks.poll();

               if (task != null) {
                  task.run();
               } else {
                  break;
               }
            }
         } catch (Exception e) {
            log.debug("Could not process splash panel events", e);
         }
      }

      public void start() {
         if (active.compareAndSet(false, true)) {
            Thread thread = builder.newThread(this);
            thread.start();
         }
      }
   }

   @AllArgsConstructor
   private static class SplashDialog implements SplashPanel {

      private final WindowEvent event;
      private final JFrame frame;
      private final JLabel label;

      public SplashDialog(JFrame frame, JLabel label) {
         this.event = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
         this.frame = frame;
         this.label = label;
      }

      @Override
      public void update(String message) {
         label.setText(message);
         label.invalidate();
      }

      @Override
      public void show(long duration) {
         frame.setVisible(true);
      }

      @Override
      public void dispose() {
         frame.setVisible(false);
         frame.dispatchEvent(event);
      }
   }

   private static SplashPanel createSplashScreen(String message, String resource, String path, String background, String foreground) throws Exception {
      JFrame frame = new JFrame();
      BufferedImage image = createLogoImage(resource);
      JPanel panel = createPanel(image, background, foreground, 600, 400);
      JLabel label = createLabel(message, foreground);
      WindowIcon icon = WindowIconLoader.loadIcon(path);
      URL iconResource = icon.getResource();
      Image iconImage = Toolkit.getDefaultToolkit().getImage(iconResource);

      frame.setIconImage(iconImage);
      panel.add(label);
      frame.setUndecorated(true);
      frame.add(panel);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setBackground(Color.RED);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      return new SplashDialog(frame, label);

   }

   private static JLabel createLabel(String message, String color) {
      JLabel text = new JLabel(message);

      text.setForeground(Color.decode(color));
      text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      return text;
   }

   private static JPanel createPanel(BufferedImage image, String background, String border, int width, int height) {
      JPanel panel = new JPanel();
      JPanel spacer = new JPanel();
      ImageIcon icon = new ImageIcon(image);
      JLabel label = new JLabel(icon);
      BorderLayout layout = new BorderLayout();
      BoxLayout verticalLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
      
      label.setSize(width, height);
      label.setLayout(layout);
      label.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
      spacer.setSize(10, 10);
      spacer.setBackground(Color.decode(background));
      panel.setBackground(Color.decode(background));   
      panel.setLayout(verticalLayout);
      panel.setBorder(BorderFactory.createLineBorder(Color.decode(border)));
      panel.add(spacer);
      panel.add(label);
      return panel;
   }

   private static BufferedImage createLogoImage(String resource) {
      try {
         InputStream data = ClassPathReader.findResourceAsStream(resource);
         return ImageIO.read(data);
      } catch (Exception e) {
         log.info("Could not load image {}", resource, e);
      }
      return null;
   }
}

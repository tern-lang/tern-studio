package org.ternlang.studio.core.terminal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.simpleframework.http.socket.FrameChannel;
import org.ternlang.studio.project.HomeDirectory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import com.sun.jna.Platform;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TerminalProcess implements TerminalListener {

   private LinkedBlockingQueue<String> commands;
   private String[] termCommand;
   private PtyProcess process;
   private Integer columns = 100;
   private Integer rows = 100;
   private BufferedReader inputReader;
   private BufferedReader errorReader;
   private BufferedWriter outputWriter;
   private FrameChannel channel;
   private ObjectMapper mapper;
   private Executor executor;
   private File directory; // initial directory

   public TerminalProcess(FrameChannel channel, ObjectMapper mapper, File directory) {
      this.commands = new LinkedBlockingQueue<>();
      this.executor = Executors.newFixedThreadPool(1);
      this.directory = directory;
      this.mapper = mapper;
      this.channel = channel;

   }

   public void onTerminalClose() {
      log.info("Terminal destroy");
      process.destroyForcibly();
   }

   public void onTerminalInit() {
      log.info("Terminal open");
   }

   public void onTerminalReady() {

      new Thread(() -> {
         try {
            initializeProcess();
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }).start();

   }

   private void initializeProcess() throws Exception {
      String userHome = System.getProperty("user.home");
      File dataDir = HomeDirectory.getPath("terminalfx");
      String startPath = directory.getCanonicalPath();

      TerminalHelper.copyLibPty(dataDir);

      if (Platform.isWindows()) {
         this.termCommand = "cmd.exe".split("\\s+");
      } else {
         this.termCommand = "/bin/bash -i".split("\\s+");
      }

      Map<String, String> envs = new HashMap<>(System.getenv());
      envs.put("TERM", "xterm");

      System.setProperty("PTY_LIB_FOLDER", dataDir.toPath().resolve("libpty").toString());

      this.process = PtyProcess.exec(termCommand, envs, userHome);

      process.setWinSize(new WinSize(columns, rows));
      this.inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      this.errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      this.outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

      onTerminalCommand("cd " + startPath + "\r");

      new TerminalConsole(inputReader, channel, mapper).start();
      new TerminalConsole(errorReader, channel, mapper).start();
   }

   public void onTerminalCommand(String command) {
      if (command != null) {
         try {
            commands.put(command);
            executor.execute(() -> {
               try {
                  outputWriter.write(commands.poll());
                  outputWriter.flush();
               } catch (IOException e) {
                  e.printStackTrace();
               }
            });
         } catch (Exception e) {
            throw new IllegalStateException("Could not process command '" + command + "'", e);
         }
      }
   }

   public void onTerminalResize(String columns, String rows) {
      if (Objects.nonNull(columns) && Objects.nonNull(rows)) {
         this.columns = Integer.valueOf(columns);
         this.rows = Integer.valueOf(rows);

         if (Objects.nonNull(process)) {
            process.setWinSize(new WinSize(this.columns, this.rows));
         }

      }
   }
}

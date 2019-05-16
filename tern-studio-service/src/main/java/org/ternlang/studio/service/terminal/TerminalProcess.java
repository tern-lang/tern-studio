package org.ternlang.studio.service.terminal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.simpleframework.http.socket.FrameChannel;

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
    private Executor executor;
    private String shell; // initial shell command

    public TerminalProcess(FrameChannel channel) {
       this(channel, null);
    }
    
    public TerminalProcess(FrameChannel channel, String shell) {
       this.commands = new LinkedBlockingQueue<>();
       this.executor = Executors.newFixedThreadPool(1);
       this.channel = channel;
       this.shell = shell;
       
    }

    public void onTerminalClose(){
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
        Path dataDir = Paths.get(userHome).resolve(".ternd/terminalfx");
        TerminalHelper.copyLibPty(dataDir);

        if (Platform.isWindows()) {
            this.termCommand = "cmd.exe".split("\\s+");
        } else {
            this.termCommand = "/bin/bash -i".split("\\s+");
        }

        if(Objects.nonNull(shell)){
            this.termCommand = shell.split("\\s+");
        }

        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.put("TERM", "xterm");

        System.setProperty("PTY_LIB_FOLDER", dataDir.resolve("libpty").toString());

        this.process = PtyProcess.exec(termCommand, envs, userHome);

        process.setWinSize(new WinSize(columns, rows));
        this.inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        this.outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        onTerminalCommand("cd c:\\temp\r");
        new TerminalConsole(inputReader, channel).start();
        new TerminalConsole(errorReader, channel).start();
    }

    public void onTerminalCommand(String command) throws InterruptedException {

        if (Objects.isNull(command)) {
            return;
        }

        commands.put(command);
        executor.execute(() -> {
            try {
                outputWriter.write(commands.poll());
                outputWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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

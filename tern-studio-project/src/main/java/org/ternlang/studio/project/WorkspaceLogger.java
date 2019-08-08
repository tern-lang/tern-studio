package org.ternlang.studio.project;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;

public class WorkspaceLogger {

   private static final Set<File> FILES = new CopyOnWriteArraySet<File>();

   public static synchronized void create(File logFile, String level) {
      if(FILES.add(logFile)) {
         FileAppender appender = new FileAppender();
         PatternLayoutEncoder encoder = new PatternLayoutEncoder();
         
         try {
            Level logLevel = Level.valueOf(level);
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            String path = logFile.getCanonicalPath();
            
            if(logFile.exists()) {
               logFile.delete();
            }
            logFile.getParentFile().mkdirs();
            appender.setContext(context);
            appender.setName("workspace");
            appender.setFile(path);
            encoder.setContext(context);
            encoder.setPattern("%d{\"yyyy/MM/dd HH:mm:ss,SSS\"} [%p] [%t] %C{0}.%M - %msg%n");
            encoder.start();
            appender.setEncoder(encoder);
            appender.start();
   
            logger.addAppender(appender);
            logger.setLevel(logLevel);
         }catch(Throwable e) {
            
         }
      }
   }
}

package org.ternlang.studio.project;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

public class WorkspaceLogger {

   private static final Set<File> FILES = new CopyOnWriteArraySet<File>();
   
   public static synchronized void create(File logFile, String level) {
      if(FILES.add(logFile)) {
         RollingFileAppender appender = new RollingFileAppender();
         PatternLayoutEncoder encoder = new PatternLayoutEncoder();
         SizeBasedTriggeringPolicy trigger = new SizeBasedTriggeringPolicy();
         
         try {
            Level logLevel = Level.valueOf(level);
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            String path = logFile.getCanonicalPath();
            RollingPolicyBase policy = createRollingPolicy(path, false);
            
            if(logFile.exists()) {
               logFile.delete();
            }
            logFile.getParentFile().mkdirs();
            
            trigger.setMaxFileSize(FileSize.valueOf("5MB"));
            trigger.start();
            
            encoder.setContext(context);
            encoder.setPattern("%d{\"yyyy/MM/dd HH:mm:ss,SSS\"} [%p] [%t] %C{0}.%M - %msg%n");
            encoder.start();
            
            appender.setTriggeringPolicy(trigger);
            appender.setRollingPolicy(policy);
            appender.setContext(context);
            appender.setName("workspace");
            appender.setFile(path);
            appender.setEncoder(encoder);
            
            policy.setContext(context);
            policy.setParent(appender);
            policy.start();
            appender.start();
   
            logger.addAppender(appender);
            logger.setLevel(logLevel);
         }catch(Throwable e) {
            e.printStackTrace();
         }
      }
   }
   
   private static synchronized RollingPolicyBase createRollingPolicy(String path, boolean timeBased) {
      if(timeBased) {
         TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
         
         policy.setMaxHistory(7); // 1 week by default
         policy.setTotalSizeCap(FileSize.valueOf("1GB"));
         policy.setFileNamePattern(path + ".%d{yyyy-MM-dd}.zip");
         
         return policy;
      } else {
         FixedWindowRollingPolicy policy = new FixedWindowRollingPolicy();
         
         policy.setFileNamePattern(path + ".%i.zip");
         policy.setMaxIndex(3);
         policy.setMinIndex(1);
         
         return policy;
      }
   }
}

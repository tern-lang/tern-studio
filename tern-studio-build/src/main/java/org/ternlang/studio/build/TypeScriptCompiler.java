package org.ternlang.studio.build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.agent.log.ConsoleLog;
import org.ternlang.studio.agent.log.Log;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.agent.log.LogLogger;
import org.ternlang.studio.agent.log.TraceLogger;
import org.ternlang.studio.build.console.ConsoleListener;
import org.ternlang.studio.build.console.ConsoleManager;
import org.springframework.stereotype.Component;

import com.google.javascript.jscomp.CompilationLevel;

@Slf4j
@Component
public class TypeScriptCompiler {
   
   private static final String NODE_LOCATION = "C:/Program Files/nodejs/node.exe";
   private static final String TYPESCRIPT_COMPILER = "src/main/typescript/tsc.js";
   private static final String COMPRESS_FILE = "all.js";
   
   private final File compiler;
   private final File node;
   private final File root;
   
   public TypeScriptCompiler() throws Exception  {
      this.root = new File(".").getCanonicalFile();
      this.node = new File(NODE_LOCATION).getCanonicalFile();
      this.compiler = new File(root, TYPESCRIPT_COMPILER).getCanonicalFile();
   }
   
   public synchronized void compile(File sourceDir, File outputDir, List<String> libraryFiles) throws Exception {
      File generatedFile = new File(outputDir, COMPRESS_FILE);
      
      if(!sourceDir.exists()) {
         throw new IOException("Source directory '" +sourceDir+"' does not exist");
      }
      if(!sourceDir.isDirectory()) {
         throw new IOException("Source directory '"+sourceDir+"' is actually a file");
      }
      if(!outputDir.exists()) {
         throw new IOException("Output directory '" +outputDir+"' does not exist");
      }
      if(!outputDir.isDirectory()) {
         throw new IOException("Output directory '"+outputDir+"' is actually a file");
      }
      if(node.exists() && compiler.exists()) {
         List<String> command = new ArrayList<String>();
         
         command.add(node.getCanonicalPath());
         command.add(compiler.getCanonicalPath()); 
         command.add("--module"); 
         command.add("AMD");
         
         if(outputDir.isDirectory()) {
            command.add("--outDir");
            command.add(outputDir.getCanonicalPath());
         }
         File[] sourceFiles = sourceDir.listFiles();
         File work = compiler.getParentFile();
         long outputTime = outputDir.lastModified();
         long sourceTime = 0;
         int outputCount = 0;
         
         for(File file : sourceFiles) {
            String name = file.getName();
            
            if(file.isFile() && name.endsWith(".ts")) {
               String path = file.getCanonicalPath();
               long lastModified = file.lastModified();
   
               if(sourceTime < lastModified) {
                  sourceTime = lastModified;
               }
               command.add(path);
            }
         }
         if(outputDir.isDirectory()) {
            File[] outputFiles = outputDir.listFiles();
            
            for(File file : outputFiles) {
               String name = file.getName();
               
               if(file.isFile() && name.endsWith(".js")) {
                  long lastModified = file.lastModified();
                  
                  if(outputTime < lastModified) {
                     outputTime = lastModified;
                  }
                  outputCount++;
               }
            }
         }
         if(sourceTime > outputTime || outputCount == 0) {            
            ScriptCompiler compiler = new ScriptCompiler(CompilationLevel.SIMPLE_OPTIMIZATIONS);
            ScriptProcessor processor = new ScriptProcessor(compiler);
            ProcessBuilder builder = new ProcessBuilder(command);
            CompilerListener listener = new CompilerListener();
            ConsoleManager manager = new ConsoleManager(listener);
            StringBuilder buffer = new StringBuilder();
            
            buffer.append("\n----------------------------------------------------------------\n");
            buffer.append(command);
            buffer.append("\n----------------------------------------------------------------\n");
            
            log.info("{}", buffer);
            System.err.println(buffer);
            
            manager.start();
            builder.directory(work);
            builder.redirectErrorStream(true);
            
            Process process = builder.start();
            
            manager.tail(process, "tsc");
            process.waitFor();
            
            if(outputDir.isDirectory()) {
               File[] outputFiles = outputDir.listFiles();
               
               for(File file : outputFiles) {
                  String name = file.getName();
                  
                  if(file.isFile() && name.endsWith(".js")) {
                     processor.process(file); // minify the source
                  }
               }
            }
         }
      }
   }
   
   private static class CompilerListener implements ConsoleListener {
      
      private final TraceLogger logger;
      private final Log log;
      
      public CompilerListener() {
         this.log = new ConsoleLog();
         this.logger = new LogLogger(log, LogLevel.DEBUG);
      }

      @Override
      public void onUpdate(String process, String text) {
         try {
            String line = text.trim();
            
            if(line.contains("error")) {
               StringBuilder builder = new StringBuilder();
               
               builder.append("\n------------------------------------------------------------------------------\n");
               builder.append(line);
               builder.append("\n------------------------------------------------------------------------------\n");
               
               System.err.println(builder);
            }
            logger.info(process + ": " + line);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
      
      @Override
      public void onUpdate(String process, String text, Throwable cause) {
         try {
            String line = text.trim();
            
            if(line.contains("error")) {
               StringBuilder builder = new StringBuilder();
               
               builder.append("\n------------------------------------------------------------------------------\n");
               builder.append(line);
               builder.append("\n------------------------------------------------------------------------------\n");
               
               System.err.println(builder);
            }
            logger.info(process + ": " + line, cause);
         }catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}
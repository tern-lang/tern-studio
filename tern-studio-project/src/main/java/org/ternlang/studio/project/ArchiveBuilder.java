package org.ternlang.studio.project;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;
import org.ternlang.core.type.extend.FileExtension;
import org.ternlang.studio.agent.local.LocalJarProcess;
import org.ternlang.studio.agent.local.LocalProcess;
import org.ternlang.studio.project.generate.ClassPathConfigFile;
import org.ternlang.studio.project.generate.ClassPathFileGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Slf4j
public class ArchiveBuilder {
   
   private static final ArchivePath[] RUNTIME_PACKAGES = new ArchivePath[]{
      new ArchivePath("org/ternlang/studio/agent", true),
      new ArchivePath("org/ternlang/compile", true),
      new ArchivePath("org/ternlang/common", true),
      new ArchivePath("org/ternlang/platform", true),
      new ArchivePath("org/ternlang/cglib", true),
      new ArchivePath("org/ternlang/asm", true),
      new ArchivePath("org/ternlang/dx", true),
      new ArchivePath("org/ternlang/parse", true),
      new ArchivePath("org/ternlang/core", true),
      new ArchivePath("org/ternlang/tree", true),
      new ArchivePath("import.txt", false),
      new ArchivePath("grammar.txt", false)
   };
   
   private final PathMatchingResourcePatternResolver resolver;
   private final ClassPathFileGenerator generator;
   private final FileExtension extension;
   private final ProjectContext context;
   private final Project project;

   public ArchiveBuilder(Project project, ProjectContext context) {
      this.resolver = new PathMatchingResourcePatternResolver(); 
      this.generator = new ClassPathFileGenerator();
      this.extension = new FileExtension();
      this.project = project;
      this.context = context;
   }
   
   public File exportArchive(String mainScript) {
      String name = project.getName();
      String tempDir = System.getProperty("java.io.tmpdir");
      
      if(mainScript != null) {
         String realPath = project.getRealPath(mainScript);
         File rootPath = project.getBasePath();
         File mainScriptFile = new File(rootPath, realPath);
         
         if(!mainScriptFile.exists()) {
            throw new IllegalArgumentException("Resource " + mainScript + " does not exist");
         }
         if(mainScriptFile.isDirectory()) {
            throw new IllegalArgumentException("Resource " + mainScript + " is a directory");
         }
      }
      File destDir = new File(tempDir, name);
      File outputFile = new File(tempDir, name + ".jar");
      
      try { 
         collectResources(destDir);
         extractRuntime(destDir);
         extractClassPath(destDir);
         archiveDirectory(destDir, outputFile, mainScript);

         log.info("Archive {} created for {}", outputFile, name);
         return outputFile;
      }catch(Exception e) {
         log.info("Could not export {}", name);
         throw new IllegalStateException("Could not export " + name, e);
      }
   }

   private void archiveDirectory(File sourceDir, File outputFile, String mainScript) throws Exception {
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      
      if(!StringUtils.isBlank(mainScript)) {
         Attributes.Name mainScriptKey = new Attributes.Name(LocalJarProcess.MAIN_SCRIPT);
         
         manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, LocalJarProcess.class.getName());  
         manifest.getMainAttributes().put(mainScriptKey, mainScript);  
      } else {
         manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, LocalProcess.class.getName()); 
      }
      OutputStream stream = new FileOutputStream(outputFile);
      JarOutputStream out = new JarOutputStream(stream, manifest);
      Set<String> done = new HashSet<String>();
      
      try {
         List<File> files = extension.findFiles(sourceDir, ".*");
         String sourcePath = sourceDir.getCanonicalPath();
         int length = sourcePath.length();
         
         for(File entry : files) {
            if(entry.isFile()) {
               String entryPath = entry.getCanonicalPath();
               String relativePath = entryPath.substring(length).replace(File.separatorChar, '/');
               
               if(done.add(relativePath) && !relativePath.endsWith("MANIFEST.MF")) {
                  if(relativePath.startsWith("/")) {
                     relativePath = relativePath.substring(1); // /org/domain/Type.class -> org/domain/Type.class
                  }
                  JarEntry jarEntry = new JarEntry(relativePath);
                  byte[] data = FileUtils.readFileToByteArray(entry);
                   
                  log.info("Compressing {}", relativePath);
                  out.putNextEntry(jarEntry);
                  out.write(data);
               }
            }
         }
      } finally {
         out.close();
         stream.close();
      }
   }
   
   private void collectResources(File destDir) throws Exception {
      Map<String, File> files = context.getFiles();
      Set<Entry<String, File>> entries = files.entrySet();
      
      for(Entry<String, File> entry : entries) {
         String path = entry.getKey();
         File file = entry.getValue();
         
         if(file.isFile()) {
            byte[] content = FileUtils.readFileToByteArray(file);
            
            try {
               File outputFile = new File(destDir, path);
               
               if(!outputFile.getParentFile().exists()) {
                  outputFile.getParentFile().mkdirs();
               }
               log.info("Collecting {}", path);
               FileUtils.writeByteArrayToFile(outputFile, content);
            } catch(Exception e) {
               log.info("Could not collect {}", path);
            }
         }
      }
   }

   private void extractRuntime(File destDir) throws Exception{
      for(ArchivePath path : RUNTIME_PACKAGES) {
         String prefix = path.getPath();
         String pattern = path.getPattern();
         Resource[] resources = resolver.getResources(CLASSPATH_ALL_URL_PREFIX + pattern);
          
         for(Resource resource : resources) {
            String target = resource.getURI().toString();
            int index = target.indexOf(prefix);
            
            if(index != -1) {
               String relativePath = target.substring(index);
               File file = new File(destDir, relativePath);
               
               if(!file.getParentFile().exists()) {
                  file.getParentFile().mkdirs();
               }
               InputStream input = resource.getInputStream();
               
               try {
                  log.info("Extracting {}", relativePath);
                  byte[] data = IOUtils.toByteArray(input);
                  FileUtils.writeByteArrayToFile(file, data);
               }finally {
                  input.close();
               }
            }
         }
      }
   }
   
   private void extractClassPath(File destDir) throws Exception{
      File file = generator.getConfigFilePath(project);
      String content = FileUtils.readFileToString(file);
      ClassPathConfigFile configFile = generator.parseConfig(project, content);
      List<File> files = configFile.getFiles();
      
      extractClassPath(destDir, files);
   }
   
   private void extractClassPath(final File destDir, final List<File> files) throws Exception {
      int threads = Runtime.getRuntime().availableProcessors();
      ExecutorService executor = Executors.newFixedThreadPool(threads);
      
      for(File file : files) {
         if(file.isFile() && file.getName().endsWith(".jar")) {
            ArchiveJarExtractor extractor = new ArchiveJarExtractor(destDir, file);
            executor.execute(extractor);
         }
      }
      executor.shutdown();
      executor.awaitTermination(1, TimeUnit.HOURS);
   }
   
   @AllArgsConstructor
   private static class ArchiveJarExtractor implements Runnable {
      
      private final File destDir;
      private final File file;
      
      @Override
      public void run() {
         try {
            ZipFile zipFile = new ZipFile(file); 
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            
            while (zipEntries.hasMoreElements()) {
               ZipEntry zipEntry = zipEntries.nextElement();

               if(!zipEntry.isDirectory()) {
                  String fileName = zipEntry.getName();
                  File outputPath = new File(destDir, fileName);

                  if (!outputPath.exists()) {
                     outputPath.getParentFile().mkdirs();
                  }
                  InputStream input = zipFile.getInputStream(zipEntry);

                  try {
                     log.info("Extracting {}", fileName);
                     byte[] data = IOUtils.toByteArray(input);
                     FileUtils.writeByteArrayToFile(outputPath, data);
                  } finally {
                     input.close();
                  }
               }
            }
            zipFile.close();
         } catch(Exception e) {
            log.info("Could not extract {}", file);
         }
      }
   }
   
   @AllArgsConstructor
   private static class ArchivePath {
      
      private final String prefix;
      private final boolean classes;
      
      public String getPattern(){
         if(classes) {
            return prefix + "/**/*.class";
         }
         return prefix;
      }
      
      public String getPath(){
         return prefix;
      }
   }
}
package org.ternlang.studio.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.http.Path;
import org.ternlang.studio.common.FileDirectory;

public class FileSystem {

   private final FileDirectory directory;
   
   public FileSystem(FileDirectory directory) {
      this.directory = directory;
   }
   
   public File getFile(Path path) {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.tern
      File rootPath = directory.getBasePath();
      String realPath = projectPath.replace('/', File.separatorChar);
      return new File(rootPath, realPath);
   }
   
   public File getFile(String path) {
      File rootPath = directory.getBasePath();
      String realPath = path.replace('/', File.separatorChar);
      return new File(rootPath, realPath);
   }
   
   public void writeAsString(String path, String resource) throws Exception {
      byte[] octets = resource.getBytes("UTF-8");
      writeAsByteArray(path, octets);
   }
   
   public void writeAsByteArray(String path, byte[] resource) throws Exception {
      File rootPath = directory.getBasePath();
      FilePersister.writeAsByteArray(rootPath, path, resource);
   }
   
   public String readAsString(String path) throws Exception {
      File rootPath = directory.getBasePath();
      return FilePersister.readAsString(rootPath, path);
   }
   
   public byte[] readAsByteArray(String path) throws Exception {
      File rootPath = directory.getBasePath();
      return FilePersister.readAsByteArray(rootPath, path);
   }
   
   public FileData readFile(Path path) throws Exception {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.tern
      return readFile(projectPath);
   }
   
   public FileData readFile(String path) throws Exception {
      long time = System.currentTimeMillis();
      File rootPath = directory.getBasePath();
      String realPath = path.replace('/', File.separatorChar);
      File projectFile = new File(rootPath, realPath);
      
      if(projectFile.exists()) {
         return new FileData(this, path, projectFile, time);
      }          
      return new FileData(this, path, null, time);
   }
   
   public FileData readFile(File file) throws Exception {
      long time = System.currentTimeMillis();
      File rootPath = directory.getBasePath();
      String basePath = rootPath.getCanonicalPath();
      String absolutePath = file.getCanonicalPath();
      String relativePath = absolutePath.replace(basePath, "").replace(File.separatorChar, '/');
      
      if(file.exists()) {
         return new FileData(this, relativePath, file, time);
      }          
      return new FileData(this, relativePath, null, time);
   }
   
   public List<FileData> readFiles(File file) throws Exception {
      File[] list = file.listFiles();
      
      if(list != null) {
         List<FileData> result = new ArrayList<FileData>(list.length);
               
         for(int i = 0; i < list.length; i++) {
            FileData fileData = readFile(list[i]);
            result.add(fileData);
         }
         return Collections.unmodifiableList(result);
      } 
      return Collections.emptyList();
         
   }
   
}
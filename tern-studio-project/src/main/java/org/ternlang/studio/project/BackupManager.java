package org.ternlang.studio.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.common.DateFormatter;
import org.ternlang.studio.project.config.WorkspaceConfiguration;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BackupManager {
   
   private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";
   private static final String DATE_PATTERN = "^.*\\.\\d\\d\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d\\d$";
   private static final long BACKUP_EXPIRY = 14 * 24 * 60 * 60 * 1000; // 14 days
   private static final int BACKUP_COUNT = 4; // keep at least 4 files
   
   private final Workspace workspace;
   
   public synchronized void backupFile(File file, String project) {
      if(file.exists()) {
         if(file.isFile()) {
            if(acceptFile(file, project)) {
               File backupFile = createBackupFile(file, project);
               File backupDirectory = backupFile.getParentFile();
               
               if(!backupDirectory.exists()) {
                  backupDirectory.mkdirs();
               }
               long modificationTime = file.lastModified();
               
               cleanBackups(file, project);
               copyFile(file, backupFile);
               updateFileCreationTime(backupFile, modificationTime);
            }
         } else {
            File[] files = file.listFiles();
            
            for(File entry : files) {
               backupFile(entry, project);
            }
         }
      }
   }
   
   private synchronized void updateFileCreationTime(File file, long creationTime) {
      try {
         Path path = Paths.get(file.toURI());
         BasicFileAttributeView attributes = Files.getFileAttributeView(path, BasicFileAttributeView.class);
         FileTime time = FileTime.fromMillis(creationTime);
         attributes.setTimes(time, time, time);
      } catch(Exception e) {
         log.info("Could not find backup from " + file, e);
      }
   }
   
   private synchronized File createBackupFile(File file, String project) {
      long time = file.lastModified();
      File backupRoot = HomeDirectory.getPath(WorkspaceConfiguration.BACKUP_PATH);
      Project proj = workspace.getByName(project);
      
      if(proj == null) {
         throw new IllegalArgumentException("Project " + project + " does not exist");
      }
      File root = proj.getBasePath();
      String extension = DateFormatter.format(DATE_FORMAT, time);
      String relative = relative(root, file);
      String timestampFile = String.format("%s/%s.%s", project, relative, extension);

      if(!backupRoot.exists()) {
         backupRoot.mkdirs();
      }
      return new File(backupRoot, timestampFile);
   }
   
   private synchronized boolean acceptFile(File file, String project) {
      if(file.isFile() && file.exists()) {
         File latestBackup = findLatestBackup(file, project);
         
         if(latestBackup != null) {
            byte[] backupDigest = digestFile(latestBackup);
            byte[] fileDigest = digestFile(file);
            
            return !MessageDigest.isEqual(backupDigest, fileDigest);
         }
         return true;
      }
      return false;
   }
   
   private synchronized void cleanBackups(File file, String project) {
      List<BackupFile> backupFiles = findAllBackups(file, project);
      int backupCount = backupFiles.size();
      
      if(backupCount > BACKUP_COUNT) { // keep at least 4 files
         for(BackupFile backupFile : backupFiles) {
            if(backupFile.getFile().exists()) {
               long lastModified = backupFile.getFile().lastModified();
               long time = System.currentTimeMillis();
               
               if(lastModified + BACKUP_EXPIRY < time) {
                  deleteFile(backupFile.getFile());
               }
            }
         }
      }
   }
   
   private synchronized File findLatestBackup(File file, String project) {
      try {
         List<BackupFile> backupFiles = findAllBackups(file, project);
         Iterator<BackupFile> backupIterator = backupFiles.iterator();
         
         if(backupIterator.hasNext()) {
            return backupIterator.next().getFile();
         }
      } catch(Exception e) {
         log.info("Could not find backup from " + file, e);
      }
      return null;
   }
   
   public synchronized List<BackupFile> findAllBackups(File file, String project) {
      List<BackupFile> backupHistory = new ArrayList<BackupFile>();
      Map<Long, BackupFile> timeStampFiles = new TreeMap<Long, BackupFile>();
      
      try {
         File backupFile = createBackupFile(file, project);
         File backupDirectory = backupFile.getParentFile();
         File[] list = backupDirectory.listFiles();
         
         if(list != null) {
            Project proj = workspace.getByName(project);
            
            if(proj == null) {
               throw new IllegalArgumentException("Project " + project + " does not exist");
            }
            File root = proj.getBasePath();
            String rootPath = root.getCanonicalPath();
            String matchName = file.getName();
            
            for(File entry : list) {
               String name = entry.getName();
               
               if(name.matches(DATE_PATTERN) && name.startsWith(matchName) && entry.exists()) {
                  int index = name.lastIndexOf(".");
                  int length = name.length();
                  String timeStamp = name.substring(index + 1, length);
                  Date date = DateFormatter.parse(DATE_FORMAT, timeStamp);
                  long time = date.getTime();
                  String fullFile = file.getCanonicalPath();
                  String relativeFile = fullFile.replace(rootPath, "").replace(File.separatorChar, '/');
                  String relativePath = relativeFile.startsWith("/") ? relativeFile : ("/" + relativeFile);
                  BackupFile backupData = new BackupFile(entry, relativePath, date, timeStamp, project);
                  
                  timeStampFiles.put(time, backupData);
               }
            }
            Set<Long> timeStamps = timeStampFiles.keySet();
            
            for(Long timeStamp : timeStamps) {
               BackupFile timeStampFile = timeStampFiles.get(timeStamp);
               backupHistory.add(timeStampFile);
            }
            Collections.reverse(backupHistory);
            return backupHistory;
         }
      } catch(Exception e) {
         log.info("Could not find backup from " + file, e);
      }
      return backupHistory;
   }
   
   public synchronized void copyFile(File from, File to) {
      try {
         FileInputStream input = new FileInputStream(from);
         FileOutputStream output = new FileOutputStream(to);
         byte[] buffer = new byte[1024];
         int count = 0;
         
         while((count = input.read(buffer))!=-1){
            output.write(buffer, 0, count);
         }
         input.close();
         output.close();
      } catch(Exception e) {
         log.info("Could not backup " + from + " to " + to);
      }
   }
   
   public synchronized void deleteFile(File file) {
      try {
         if(file.exists()) {
            if(file.isDirectory()) {
               File[] files = file.listFiles();
               
               for(File entry : files) {
                  if(entry.isDirectory()) {
                     deleteFile(entry);
                  } else {
                     if(entry.exists()) {
                        entry.delete();
                     }
                  }
               }
            } else {
               file.delete();
            }
         }
      } catch(Exception e) {
         log.info("Could not delete " + file);
      }
   }
   
   public synchronized byte[] digestFile(File file) {
      try {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         FileInputStream input = new FileInputStream(file);
         byte[] buffer = new byte[1024];
         int count = 0;
         
         while((count = input.read(buffer))!=-1){
            digest.update(buffer, 0, count);
         }
         input.close();
         return digest.digest();
      } catch(Exception e) {
         log.info("Could not get MD5 digest of " + file);
      }
      return new byte[]{};
   }
   
   public synchronized void saveFile(File file, String content) {
      try {
         FileOutputStream out = new FileOutputStream(file);
         OutputStreamWriter encoder = new OutputStreamWriter(out, "UTF-8");
         
         encoder.write(content);
         encoder.close();
      } catch(Exception e) {
         log.info("Could not save " + file);
      }
   }
   
   public synchronized void saveFile(File file, byte[] content) {
      try {
         FileOutputStream out = new FileOutputStream(file);
         
         out.write(content);
         out.close();
      } catch(Exception e) {
         log.info("Could not save " + file);
      }
   }
   
   private synchronized String relative(File root, File file) {
      return root.toURI().relativize(file.toURI()).getPath();
   }
}
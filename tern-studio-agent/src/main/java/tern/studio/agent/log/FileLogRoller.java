package tern.studio.agent.log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogRoller {
   
   public static final String DEFAULT_FORMAT = "yyyyMMddHHmmss"; 

   private final DateFormat format;
   
   public FileLogRoller() {
      this(DEFAULT_FORMAT);
   }
   
   public FileLogRoller(String format) {
      this.format = new SimpleDateFormat(format);
   }

   public boolean rollFile(File file) {
      if (file.exists() && file.isFile()) {
         long lastModified = file.lastModified();
         String timeStamp = format.format(lastModified);
         String original = file.getName();
         File directory = file.getParentFile();
         String name = String.format("%s-%s", original, timeStamp);

         return moveFile(original, name, directory);
      }
      return false;
   }

   private boolean moveFile(String from, String to, File directory) {
      File source = new File(directory, from);
      File destination = new File(directory, to);

      return source.renameTo(destination);
   }

   public boolean alreadyRolled(File file) {
      try {
         String name = file.getName();
         String[] fileParts = name.split("-");
         String datePart = fileParts[fileParts.length -1];
         Date date = format.parse(datePart);
         long currentTime = System.currentTimeMillis();
         long time = date.getTime();
         
         if(time < currentTime) {
            return true;
         }
      } catch(Exception e) {
         return false;
      }          
      return false;
   }
}
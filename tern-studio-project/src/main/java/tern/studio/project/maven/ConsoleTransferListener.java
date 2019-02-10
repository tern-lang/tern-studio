package tern.studio.project.maven;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.aether.transfer.TransferListener;
import org.sonatype.aether.transfer.TransferResource;

@Slf4j
public class ConsoleTransferListener implements TransferListener {

   private final Map<TransferResource, Long> downloads; 

   public ConsoleTransferListener() {
      this.downloads = new ConcurrentHashMap<TransferResource, Long>();
   }

   @Override
   public void transferStarted(TransferEvent event) {
      log.info("Transfer started");
   }
   
   @Override
   public void transferInitiated(TransferEvent event) {
      String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading";
      String repository = event.getResource().getRepositoryUrl();
      String name = event.getResource().getResourceName();
      
      log.info(message + ": " + repository + name);
   }

   @Override
   public void transferProgressed(TransferEvent event) {
      TransferResource resource = event.getResource();
      downloads.put(resource, Long.valueOf(event.getTransferredBytes()));

      for (Map.Entry<TransferResource, Long> entry : downloads.entrySet()) {
         TransferResource progress = entry.getKey();
         String repository = progress.getRepositoryUrl();
         String name = progress.getResourceName();
         long total = progress.getContentLength();
         long complete = entry.getValue().longValue();

         log.debug(repository + name + ": " + getStatus(complete, total));;
      }
   }

   private String getStatus(long complete, long total) {
      if (total >= 1024) {
         return toKB(complete) + "/" + toKB(total) + " KB ";
      } else if (total >= 0) {
         return complete + "/" + total + " B ";
      } else if (complete >= 1024) {
         return toKB(complete) + " KB ";
      } else {
         return complete + " B ";
      }
   }

   @Override
   public void transferSucceeded(TransferEvent event) {
      transferCompleted(event);

      TransferResource resource = event.getResource();
      long contentLength = event.getTransferredBytes();
      
      if (contentLength >= 0) {
         String type = (event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded");
         String len = contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B";

         String throughput = "";
         long duration = System.currentTimeMillis() - resource.getTransferStartTime();
         if (duration > 0) {
            DecimalFormat format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
            double kbPerSec = (contentLength / 1024.0) / (duration / 1000.0);
            throughput = " at " + format.format(kbPerSec) + " KB/sec";
         }
         String location = resource.getRepositoryUrl();
         String name = resource.getResourceName();
         
         log.info(type + ": " + location + name + " (" + len + throughput + ")");
      }
   }

   @Override
   public void transferFailed(TransferEvent event) {
      Exception exception = event.getException();
      transferCompleted(event);
      log.info("Transfer failed", exception);
   }

   private void transferCompleted(TransferEvent event) {
      downloads.remove(event.getResource());
   }

   @Override
   public void transferCorrupted(TransferEvent event) {
      Exception exception = event.getException();
      log.info("Transfer corrupted", exception);
   }
   
   private long toKB(long bytes) {
      return (bytes + 1023) / 1024;
   }
}
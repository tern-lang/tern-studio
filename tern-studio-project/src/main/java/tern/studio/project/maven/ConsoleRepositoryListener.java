package tern.studio.project.maven;

import lombok.extern.slf4j.Slf4j;

import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.RepositoryListener;

@Slf4j
public class ConsoleRepositoryListener implements RepositoryListener {

   public ConsoleRepositoryListener() {
      super();
   }

   @Override
   public void artifactDeployed(RepositoryEvent event) {
      log.trace("Deployed " + event.getArtifact() + " to " + event.getRepository());
   }

   @Override
   public void artifactDeploying(RepositoryEvent event) {
      log.trace("Deploying " + event.getArtifact() + " to " + event.getRepository());
   }

   @Override
   public void artifactDescriptorInvalid(RepositoryEvent event) {
      log.trace("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException().getMessage());
   }

   @Override
   public void artifactDescriptorMissing(RepositoryEvent event) {
      log.trace("Missing artifact descriptor for " + event.getArtifact());
   }

   @Override
   public void artifactInstalled(RepositoryEvent event) {
      log.trace("Installed " + event.getArtifact() + " to " + event.getFile());
   }

   @Override
   public void artifactInstalling(RepositoryEvent event) {
      log.trace("Installing " + event.getArtifact() + " to " + event.getFile());
   }

   @Override
   public void artifactResolved(RepositoryEvent event) {
      log.info("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactDownloading(RepositoryEvent event) {
      log.trace("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactDownloaded(RepositoryEvent event) {
      log.info("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactResolving(RepositoryEvent event) {
      log.trace("Resolving artifact " + event.getArtifact());
   }

   @Override
   public void metadataDeployed(RepositoryEvent event) {
      log.trace("Deployed " + event.getMetadata() + " to " + event.getRepository());
   }

   @Override
   public void metadataDeploying(RepositoryEvent event) {
      log.trace("Deploying " + event.getMetadata() + " to " + event.getRepository());
   }

   @Override
   public void metadataInstalled(RepositoryEvent event) {
      log.trace("Installed " + event.getMetadata() + " to " + event.getFile());
   }

   @Override
   public void metadataInstalling(RepositoryEvent event) {
      log.trace("Installing " + event.getMetadata() + " to " + event.getFile());
   }

   @Override
   public void metadataInvalid(RepositoryEvent event) {
      log.trace("Invalid metadata " + event.getMetadata());
   }

   @Override
   public void metadataResolved(RepositoryEvent event) {
      log.trace("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataResolving(RepositoryEvent event) {
      log.trace("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataDownloading(RepositoryEvent event) {
      log.trace("Metadata downloading " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataDownloaded(RepositoryEvent event) {
      log.trace("Metadata downloaded " + event.getMetadata() + " from " + event.getRepository());
   }

}
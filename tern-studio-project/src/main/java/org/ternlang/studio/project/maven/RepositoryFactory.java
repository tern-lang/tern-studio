package org.ternlang.studio.project.maven;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;

public class RepositoryFactory {
   
   public RepositoryFactory() {
      super();
   }

   public RepositorySystem newRepositorySystem() {
      DefaultServiceLocator locator = new DefaultServiceLocator();
      locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
      locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
      locator.setServices(WagonProvider.class, new ManualWagonProvider());

      return locator.getService(RepositorySystem.class);
   }

   public RepositorySystemSession newRepositorySystemSession(RepositorySystem system, String path) {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      LocalRepository localRepo = new LocalRepository(path);

      session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));
      session.setTransferListener(new ConsoleTransferListener());
      session.setRepositoryListener(new ConsoleRepositoryListener());

      return session;
   }

   public RemoteRepository newRemoteRepository(String name, String type, String location) {
      return new RemoteRepository(name, type, location);
   }
}
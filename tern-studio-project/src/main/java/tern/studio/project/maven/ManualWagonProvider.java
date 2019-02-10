package tern.studio.project.maven;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagonAuthenticator;
import org.sonatype.aether.connector.wagon.WagonProvider;

public class ManualWagonProvider implements WagonProvider {
   
   private static final String SCHEME = "http";

   @Override
   public Wagon lookup(String roleHint) throws Exception {
      if (SCHEME.equals(roleHint)) {
         LightweightHttpWagonAuthenticator authenticator = new LightweightHttpWagonAuthenticator();
         LightweightHttpWagon wagon = new LightweightHttpWagon();
         
         wagon.setAuthenticator(authenticator);
         return wagon;
      }
      return null;
   }

   @Override
   public void release(Wagon wagon) {

   }

}
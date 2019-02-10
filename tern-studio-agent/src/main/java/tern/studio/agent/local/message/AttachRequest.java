package tern.studio.agent.local.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URI;

public class AttachRequest implements Externalizable{

   private String project;
   private String host;
   private int port;
   
   public AttachRequest(){
      this(null, null, 0);
   }
   
   public AttachRequest(String project, String host, int port) {
      this.project = project;
      this.host = host;
      this.port = port;
   }
   
   public String getProject(){
      return project;
   }
   
   public URI getTarget(){
      try {
         return new URI("http://" + host +  ":" + port);
      } catch(Exception e) {
         throw new IllegalStateException("Could not build connection", e);
      }
   }

   @Override
   public void writeExternal(ObjectOutput output) throws IOException {
      output.writeUTF(project);
      output.writeUTF(host);
      output.writeInt(port);
   }

   @Override
   public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
      project = input.readUTF();
      host = input.readUTF();  
      port = input.readInt();
   }
}

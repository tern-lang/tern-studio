package org.ternlang.studio.agent.local.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DetachResponse implements Externalizable{

   private String process;
   
   public DetachResponse(){
      this(null);
   }
   
   public DetachResponse(String process) {
      this.process = process;
   }
   
   public String getProcess(){
      return process;
   }
   
   @Override
   public void writeExternal(ObjectOutput output) throws IOException {
      output.writeUTF(process);
   }

   @Override
   public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
      process = input.readUTF();
   }
}

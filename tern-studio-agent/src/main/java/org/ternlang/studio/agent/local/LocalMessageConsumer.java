package org.ternlang.studio.agent.local;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.ternlang.studio.agent.local.message.AttachRequest;
import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.agent.local.message.DetachRequest;
import org.ternlang.studio.agent.local.message.DetachResponse;

public class LocalMessageConsumer {

   private final LocalProcessController listener;
   
   public LocalMessageConsumer(LocalProcessController listener){
      this.listener = listener;
   }
   
   public void consume(Socket socket) {
      try {
         InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream();
         ObjectInput input = new ObjectInputStream(in);
         ObjectOutput output = new ObjectOutputStream(out);
         Object value = input.readObject();
         
         if(AttachRequest.class.isInstance(value)) {
            String process = listener.attachRequest((AttachRequest)value);
            AttachResponse response = new AttachResponse(process);
            
            try {
               output.writeObject(response);
            }finally {
               output.close();
            }
         }
         if(DetachRequest.class.isInstance(value)) {
            String process = listener.detachRequest((DetachRequest)value);
            DetachResponse response = new DetachResponse(process);
            
            try {
               output.writeObject(response);
            }finally {
               output.close();
            }
         }
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}

package org.ternlang.studio.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

import com.google.gson.Gson;

public class JsonWriter implements BodyWriter<Object> {

   private final Gson gson;
   
   public JsonWriter() {
      this.gson = new Gson();
   }
   
   @Override
   public boolean accept(Context context, Object result) throws Exception {
      Response response = context.getResponse();
      ContentType type = response.getContentType();
      
      if(type != null && result != null) {
         String value = type.getType();
         
         return value.equals("application/json") ||
                 value.equals("text/json");
      }
      return false;
   }

   @Override
   public void write(Context context, Object result) throws Exception {
      Response response = context.getResponse();
      PrintStream output = response.getPrintStream();
      String text = gson.toJson(result);
      
      output.print(text);
   }

}

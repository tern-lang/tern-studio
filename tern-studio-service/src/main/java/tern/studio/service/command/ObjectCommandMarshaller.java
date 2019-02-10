package tern.studio.service.command;

import com.google.gson.Gson;

public abstract class ObjectCommandMarshaller<T extends Command> implements CommandMarshaller<T> {

   private final CommandType type;
   private final Gson gson;
   
   public ObjectCommandMarshaller(CommandType type) {
      this.gson = new Gson();
      this.type = type;
   }

   @Override
   public T toCommand(String text) {
      int offset = text.indexOf(':');
      String json = text.substring(offset + 1);
      return (T)gson.fromJson(json, type.command);
   }

   @Override
   public String fromCommand(T command) {
      String json = gson.toJson(command);
      return type + ":" + json;
   }
}
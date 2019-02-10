package tern.studio.service.command;

public interface CommandMarshaller<T extends Command> {
   T toCommand(String text);
   String fromCommand(T command);
}
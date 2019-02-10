package tern.studio.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCommand implements Command {

   private String resource;
   private String project;
   private String source;
   private boolean directory;
   private boolean create;
}
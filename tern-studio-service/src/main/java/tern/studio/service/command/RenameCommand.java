package tern.studio.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenameCommand implements Command {

   private Boolean dragAndDrop;
   private String project;
   private String from;
   private String to;
}
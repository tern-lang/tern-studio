package tern.studio.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayUpdateCommand implements Command {

   private String project;
   private String themeName;
   private String fontName;
   private int fontSize;
}
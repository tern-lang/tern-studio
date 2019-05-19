package org.ternlang.studio.core.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadCommand implements Command {

   private Boolean dragAndDrop;
   private String project;
   private String name;
   private String to;
   private String data;
}
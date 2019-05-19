package org.ternlang.studio.core.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCommand implements Command {

   private String project;
   private String description;
   private String resource;
   private int line;
   private long time;
}
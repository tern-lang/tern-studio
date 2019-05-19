package org.ternlang.studio.core.command;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachCommand implements Command {

   protected Map<String, Map<Integer, Boolean>> breakpoints;
   protected String project;
   private String process;
   private boolean focus; 
}
package tern.studio.service.command;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCommand implements Command {

   private Map<String, Map<Integer, Boolean>> breakpoints;
   private List<String> arguments;
   private String project;
   private String resource;
   private String system;
   private String source;
   private boolean debug;
}
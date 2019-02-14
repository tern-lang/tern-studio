package org.ternlang.studio.service.command;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScopeCommand implements Command {
   
   private Map<String, Map<String, String>> evaluation;
   private Map<String, Map<String, String>> variables;
   private String process;
   private String instruction;
   private String resource;
   private String source;
   private String status;
   private String thread;
   private String stack;
   private int change;
   private int depth;
   private int line;
   private int key;
}
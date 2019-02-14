package org.ternlang.studio.service.command;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateCommand implements Command {

   private Set<String> expand;
   private String expression;
   private String thread;
   private boolean refresh;
}
package org.ternlang.studio.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepCommand implements Command {

   public static enum StepType {
      RUN,
      STEP_IN,
      STEP_OVER,
      STEP_OUT;
   }
   
   private String thread;
   private StepType type;
}
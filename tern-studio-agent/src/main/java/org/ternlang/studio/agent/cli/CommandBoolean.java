package org.ternlang.studio.agent.cli;

public enum CommandBoolean {
   TRUE,
   FALSE,
   NONE;

   public boolean isTrue() {
      return this == TRUE;
   }

   public boolean isFalse() {
      return this == FALSE;
   }

   public boolean isNone() {
      return this == NONE;
   }
}

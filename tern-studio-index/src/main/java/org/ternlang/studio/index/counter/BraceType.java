package org.ternlang.studio.index.counter;

public enum BraceType {
   ARRAY("[", "]"),
   COMPOUND("{", "}"),
   NORMAL("(", ")");
   
   private final String open;
   private final String close;
   
   private BraceType(String open, String close) {
      this.open = open;
      this.close = close;
   }
}
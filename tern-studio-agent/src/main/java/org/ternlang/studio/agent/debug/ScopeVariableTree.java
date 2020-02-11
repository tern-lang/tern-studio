package org.ternlang.studio.agent.debug;

import org.ternlang.agent.message.common.VariableTree;

import java.util.Collections;
import java.util.Map;

public class ScopeVariableTree {
   
   public static final ScopeVariableTree EMPTY = new ScopeVariableTree.Builder(-1)
      .withEvaluation(Collections.EMPTY_MAP)
      .withLocal(Collections.EMPTY_MAP)
      .build();

   private final Map<String, Map<String, String>> evaluation;
   private final Map<String, Map<String, String>> local;
   private final int change;
   
   private ScopeVariableTree(Builder builder) {
      this.evaluation = Collections.unmodifiableMap(builder.evaluation);
      this.local = Collections.unmodifiableMap(builder.local);
      this.change = builder.change;
   }

   public VariableTree getTree() {
      return ScopeVariableConverter.convert(this);
   }
   
   public Map<String, Map<String, String>> getLocal() {
      return local;
   }
   
   public Map<String, Map<String, String>> getEvaluation() {
      return evaluation;
   }
   
   public int getChange() {
      return change;
   }

   public static class Builder {
      
      private Map<String, Map<String, String>> evaluation;
      private Map<String, Map<String, String>> local;
      private int change;
      
      public Builder(int change){
         this.change = change;
      }

      public Builder withEvaluation(Map<String, Map<String, String>> evaluation) {
         this.evaluation = evaluation;
         return this;
      }

      public Builder withLocal(Map<String, Map<String, String>> local) {
         this.local = local;
         return this;
      }

      public Builder withChange(int change) {
         this.change = change;
         return this;
      }
      
      public ScopeVariableTree build() {
         return new ScopeVariableTree(this);
      }
   }
}
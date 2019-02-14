package org.ternlang.studio.agent.debug;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.core.Context;
import org.ternlang.core.ResourceManager;
import org.ternlang.core.function.Function;
import org.ternlang.core.scope.Scope;

public class ScopeExtractor implements ScopeBrowser {

   private final AtomicReference<String> evaluate;
   private final ScopeNodeEvaluator evaluator;
   private final ScopeNodeTraverser traverser;
   private final AtomicBoolean execute;
   private final AtomicInteger counter;
   private final Set<String> watch;
   private final Set<String> local;
   private final Context context;
   private final String path;
   
   public ScopeExtractor(Context context, Scope scope, Function function, String path) {
      this.traverser = new ScopeNodeTraverser(context, scope);
      this.evaluator = new ScopeNodeEvaluator(context, scope, function);
      this.evaluate = new AtomicReference<String>();
      this.watch = new CopyOnWriteArraySet<String>();
      this.local = new CopyOnWriteArraySet<String>();
      this.execute = new AtomicBoolean();
      this.counter = new AtomicInteger();
      this.context = context;
      this.path = path;
   }
   
   public ScopeContext build(boolean grabSource, boolean expandVariables) {
      int change = counter.get();   
      boolean refresh = execute.getAndSet(false);
      String expression = evaluate.get();
      ResourceManager manager = context.getManager();  
      String source = null;
      
      if(grabSource) {
         source = manager.getString(path);
      }
      if(expandVariables){
         Map<String, Map<String, String>> variables = traverser.expand(local);
         Map<String, Map<String, String>> evaluation = evaluator.expand(watch, expression, refresh);
        
         ScopeVariableTree tree = new ScopeVariableTree.Builder(change)
            .withLocal(variables)
            .withEvaluation(evaluation)
            .build();
         
         return new ScopeContext(tree, source);
      }
      ScopeVariableTree tree = new ScopeVariableTree.Builder(change)
         .withLocal(Collections.EMPTY_MAP)
         .withEvaluation(Collections.EMPTY_MAP)
         .build();
   
      return new ScopeContext(tree, source);
   }
   
   @Override
   public void browse(Set<String> expand) {
      local.clear();
      local.addAll(expand);
      counter.getAndIncrement();
   }
   
   @Override
   public void evaluate(Set<String> expand, String expression) {
      evaluate(expand, expression, false);
   }

   @Override
   public void evaluate(Set<String> expand, String expression, boolean refresh) {
      watch.clear();
      watch.addAll(expand);
      evaluate.set(expression);
      execute.set(refresh); // should we execute same expression again
      counter.getAndIncrement();
   }
  
}
package org.ternlang.studio.resource.boot;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.ternlang.studio.resource.action.ActionAssembler;
import org.ternlang.studio.resource.action.ActionMatcher;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.annotation.ComponentListener;
import org.ternlang.studio.resource.action.build.ComponentBuilder;
import org.ternlang.studio.resource.action.build.ConstructorScanner;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MapSystem;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.ValueExtractor;

import lombok.SneakyThrows;

public class DependencySystemBuilder {

   @SneakyThrows
   public static DependencySystem create(String prefix) {
      List<Extractor> extractors = new LinkedList<Extractor>();
      DependencyScanner dependencyScanner = new PackageScanner(prefix);
      
      extractors.add(new ValueExtractor());
      
      Queue<Class> queue = dependencyScanner.scan();
      
      DependencySystem dependencySystem = new MapSystem();
      ConstructorScanner scanner = new ConstructorScanner(dependencySystem, extractors);
      Context context = new HashContext(null, null);

      dependencySystem.register(dependencySystem);
      ActionMatcher matcher = ActionAssembler.assemble(dependencySystem);
      dependencySystem.register(matcher);
      
      while(!queue.isEmpty()) {
         Class type = queue.poll();
         List<ComponentBuilder> builders = scanner.createBuilders(type);
         Iterator<ComponentBuilder> iterator = builders.iterator();
         
         while(iterator.hasNext()) {
            try {
               ComponentBuilder builder = iterator.next();
               builder.build(context);
               break;
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      }
      dependencySystem.resolveAll(ComponentListener.class)
         .forEach(listener -> listener.onReady());
      return dependencySystem;
   }
}

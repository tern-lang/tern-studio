package org.ternlang.studio.message.idl.tree;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.tree.Qualifier;

public class Import {

   private final AtomicReference<Predicate<String>> match;
   private final Qualifier qualifier;
   private final Path path;

   public Import(Qualifier qualifier, Path path) {
      this.match = new AtomicReference<Predicate<String>>();
      this.qualifier = qualifier;
      this.path = path;
   }

   public void define(Scope scope, Domain domain) throws Exception {
      String target = qualifier.getTarget();

      if(target == null) {
         match.set(name -> true);
      } else {
         match.set(name -> name.equals(target));
      }
   }

   public void process(Scope scope, Domain domain) throws Exception {
      String location = qualifier.getLocation();
      Package module = domain.getPackage(location);
      ScopeState state = scope.getState();
      
      if(module == null) {
         throw new IllegalStateException("Could not find namespace " + location);
      }
      List<Entity> entities = module.getEntities();
      Predicate<String> filter = match.get();
      
      if(filter == null) {
         throw new IllegalStateException("Import not defined");
      }
      entities.forEach(entity -> {
         String name = entity.getName();

         if(filter.test(name)) {
            Value value = Value.getTransient(entity);
            state.addValue(name, value); // use for static analysis
         }
      });
   }
}

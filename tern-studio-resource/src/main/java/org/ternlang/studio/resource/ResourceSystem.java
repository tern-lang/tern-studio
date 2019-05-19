package org.ternlang.studio.resource;

import java.util.List;

import org.ternlang.studio.resource.action.ActionMatcher;
import org.ternlang.studio.resource.action.annotation.Component;

@Component
public class ResourceSystem {

   private final List<ResourceMatcher> matchers;
   private final ActionMatcher matcher;
   
   public ResourceSystem(ActionMatcher matcher, List<ResourceMatcher> matchers) {
      this.matchers = matchers;
      this.matcher = matcher;
   }

   public ResourceMatcher create() {
      return (request, response) -> {
         Resource resource = matcher.match(request, response);
         
         if(resource == null) {
            for (ResourceMatcher next : matchers) {
               if(next != matcher) {
                  Resource matched = next.match(request, response);
         
                  if (matched != null) {
                     return matched;
                  }
               }
            }
         }
         return resource;
      };
   }
}
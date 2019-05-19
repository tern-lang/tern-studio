package org.ternlang.studio.resource;

import java.util.List;

import org.ternlang.studio.resource.action.annotation.Component;

@Component
public class ResourceSystem {

   private final RegularExpressionMatcher matcher;
   private final List<ResourceMatcher> matchers;
   
   public ResourceSystem(RegularExpressionMatcher matcher, List<ResourceMatcher> matchers) {
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
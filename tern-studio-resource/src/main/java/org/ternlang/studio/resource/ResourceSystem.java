package org.ternlang.studio.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class ResourceSystem {

   private final Optional<List<ResourceMatcher>> matchers;
   private final RegularExpressionMatcher matcher;
   
   public ResourceSystem(RegularExpressionMatcher matcher, Optional<List<ResourceMatcher>> matchers) {
      this.matchers = matchers;
      this.matcher = matcher;
   }

   public ResourceMatcher create() {
      return (request, response) -> {
         Resource resource = matcher.match(request, response);
         
         if(resource == null) {
            if(matchers.isPresent()) {
               List<ResourceMatcher> list = matchers.get();
               
               for (ResourceMatcher next : list) {
                  if(next != matcher) {
                     Resource matched = next.match(request, response);
            
                     if (matched != null) {
                        return matched;
                     }
                  }
               }
            }
         }
         return resource;
      };
   }

}
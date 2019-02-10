package tern.studio.common.resource;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.display.DisplayResourceMatcher;
import org.springframework.stereotype.Component;

@Component
public class CombinationMatcher {

   private final List<ResourceMatcher> matchers;
   
   public CombinationMatcher(RegularExpressionMatcher regexMatcher, DisplayResourceMatcher displayMatcher) {
      this.matchers = Arrays.asList(regexMatcher, displayMatcher);
   }

   public Resource match(Request request, Response response) throws Exception {
      for (ResourceMatcher matcher : matchers) {
         Resource resource = matcher.match(request, response);

         if (resource != null) {
            return resource;
         }
      }
      return null;
   }

}
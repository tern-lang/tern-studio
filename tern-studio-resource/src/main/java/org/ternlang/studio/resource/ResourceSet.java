package org.ternlang.studio.resource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@org.ternlang.studio.resource.action.annotation.Component
@Component
public class ResourceSet {

   private final AtomicReference<List<Resource>> sorted;
   private final Comparator<Resource> comparator;
   private final List<Resource> resources;
   
   public ResourceSet(List<Resource> resources) {
      this.sorted = new AtomicReference<List<Resource>>(Collections.EMPTY_LIST);
      this.comparator = new ResourcePathComparator();
      this.resources = resources;
   }
   
   public List<Resource> getResources() {
      List<Resource> result = sorted.get();
      
      if(result.isEmpty()) {
         if(!resources.isEmpty()) {
            Collections.sort(resources, comparator);
         }
         sorted.set(resources);
         return Collections.unmodifiableList(resources);
      }
      return Collections.unmodifiableList(result);
   }
   
   private static class ResourcePathComparator implements Comparator<Resource> {

      @Override
      public int compare(Resource left, Resource right) {
         Class<?> leftType = left.getClass();
         Class<?> rightType = right.getClass();
         ResourcePath leftPath = leftType.getAnnotation(ResourcePath.class);
         ResourcePath rightPath = rightType.getAnnotation(ResourcePath.class);

         if (leftPath == null) {
            throw new IllegalStateException("Could not find annotation on " + leftType);
         }
         if (rightPath == null) {
            throw new IllegalStateException("Could not find annotation on " + rightType);
         }
         int leftLength = leftPath.value().length();
         int rightLength = rightPath.value().length();
         
         return Integer.compare(rightLength, leftLength); // shortest last
      }
      
   }
}

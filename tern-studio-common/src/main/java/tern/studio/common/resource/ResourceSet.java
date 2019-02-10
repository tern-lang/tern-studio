package tern.studio.common.resource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class ResourceSet {

   private final AtomicReference<List<Resource>> sorted;
   private final Optional<List<Resource>> resources;
   private final Comparator<Resource> comparator;
   
   public ResourceSet(Optional<List<Resource>> resources) {
      this.sorted = new AtomicReference<List<Resource>>(Collections.EMPTY_LIST);
      this.comparator = new ResourcePathComparator();
      this.resources = resources;
   }
   
   public List<Resource> getResources() {
      List<Resource> result = sorted.get();
      
      if(resources.isPresent() && result.isEmpty()) {
         List<Resource> list = resources.get();
         
         if(!list.isEmpty()) {
            Collections.sort(list, comparator);
         }
         sorted.set(list);
         return Collections.unmodifiableList(list);
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

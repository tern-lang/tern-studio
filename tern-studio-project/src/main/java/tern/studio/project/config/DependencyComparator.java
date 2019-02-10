package tern.studio.project.config;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

public class DependencyComparator implements Comparator<Dependency> {
   
   private final int multiplier;
   
   public DependencyComparator() {
      this(false);
   }
   
   public DependencyComparator(boolean reverse) {
      this.multiplier = reverse ? -1 : 1;
   }

   @Override
   public int compare(Dependency left, Dependency right) {
      String leftKey = left.getDependencyKey();
      String rightKey = right.getDependencyKey();
      int compare = leftKey.compareTo(rightKey);
      
      if(compare == 0) {
         String leftVersion = left.getVersion();
         String rightVersion = right.getVersion();
         
         return compareVersion(leftVersion, rightVersion);
      }
      return compare * multiplier;
   }

  
   public int compareVersion(String left, String right) {
      String[] leftParts = left.split("\\.");
      String[] rightParts = right.split("\\.");
      
      for(int i = 0; i < 4; i++) {
         if(leftParts.length > i && rightParts.length > i) {
            String leftPart = leftParts[i];
            String rightPart = rightParts[i];
            
            if(!StringUtils.isNumeric(leftPart) || !StringUtils.isNumeric(rightPart) ) {
               return leftPart.compareTo(rightPart); 
            }
            int leftValue = Integer.parseInt(leftPart);
            int rightValue = Integer.parseInt(rightPart);
            int compare = Integer.compare(leftValue, rightValue);
            
            if(compare != 0) {
               return compare * multiplier;
            }
         }
      }
      return Integer.compare(leftParts.length, rightParts.length) * multiplier;
      
   }
}

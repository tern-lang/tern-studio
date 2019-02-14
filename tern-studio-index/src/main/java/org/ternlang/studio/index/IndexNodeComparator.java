package org.ternlang.studio.index;

import java.util.Comparator;

public class IndexNodeComparator implements Comparator<IndexNode> {

   private final boolean reverse;
   
   public IndexNodeComparator() {
      this(false);
   }
   
   public IndexNodeComparator(boolean reverse) {
      this.reverse = reverse;
   }
   
   @Override
   public int compare(IndexNode left, IndexNode right) {
      int leftLine = left.getLine();
      int rightLine = right.getLine();
      int comparison = Integer.compare(leftLine, rightLine);
      
      if(comparison == 0) {
         String leftName = left.getName();
         String rightName = right.getName();
         
         if(left != null && right != null) {
            comparison = leftName.compareTo(rightName);
         }
      }
      if(comparison == 0) {
         IndexType leftType = left.getType();
         IndexType rightType = right.getType();
         
         comparison = leftType.compareTo(rightType);
      }
      return reverse ? -comparison : comparison;
   }

}

package org.ternlang.studio.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndexNodeSorter {

   public static List<IndexNode> sort(Collection<IndexNode> nodes, String resource) {
      List<IndexNode> copy = new ArrayList<IndexNode>(nodes);
      RelevanceComparator comparator = new RelevanceComparator(resource);
      
      Collections.sort(copy, comparator);
      
      return Collections.unmodifiableList(copy);
   }
   
   private static class RelevanceComparator implements Comparator<IndexNode> {
      
      private final String resource;
      
      public RelevanceComparator(String resource) {
         this.resource = resource;
      }

      @Override
      public int compare(IndexNode left, IndexNode right) {
         int leftScore = score(left);
         int rightScore = score(right);         
         int score = Integer.compare(leftScore, rightScore);
         
         if(score == 0) {
            String leftName = left.getFullName();
            String rightName = right.getFullName();
            
            return String.CASE_INSENSITIVE_ORDER.compare(leftName, rightName);
         }
         return score;
      }
      
      public int score(IndexNode node) {
         String fullName = node.getFullName();
         String location = node.getResource();
         
         if(location.equals(resource)) {
            return 0;
         }
         if(fullName.startsWith("lang.")) {
            return 1;
         }
         if(fullName.startsWith("util.")) {
            return 2;
         }    
         if(fullName.startsWith("io.")) {
            return 3;
         }
         if(fullName.startsWith("net.")) {
            return 4;
         }
         if(fullName.startsWith("java.")) {
            return 5;
         }    
         if(fullName.startsWith("javax.")) {
            return 6;
         }             
         return 7;
      }
      
   }
}

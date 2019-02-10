package tern.studio.service.tree;

import java.io.File;
import java.util.Comparator;

public class TreeFileComparator implements Comparator<File>{

   @Override
   public int compare(File left, File right) {
      String leftPath = left.getAbsolutePath();
      String rightPath = right.getAbsolutePath();
      
      if(left.isDirectory() != right.isDirectory()) {
         if(left.isDirectory()) {
            return -1;
         }
         if(right.isDirectory()) {
            return 1;
         }
      }
      return leftPath.compareTo(rightPath);
   }

}
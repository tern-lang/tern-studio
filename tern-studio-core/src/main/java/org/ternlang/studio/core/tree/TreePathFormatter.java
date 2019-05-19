package org.ternlang.studio.core.tree;

import java.util.HashSet;
import java.util.Set;

public class TreePathFormatter {

   public static Set<String> formatPath(String project, Set<String> expands) {
      Set<String> results = new HashSet<String>();
      
      for(String expand : expands) {
         String result = formatPath(project, expand);
         String[] list = result.split("/");
         
         if(list.length > 1) {
            StringBuilder builder = new StringBuilder();
            
            for(int i = 0; i < list.length; i++) {
               String segment = list[i];
               
               builder.append("/");
               builder.append(segment);
      
               String path = builder.toString();
               
               results.add(path);
            }
         }
         results.add(result);
      }
      return results;
   }
   
   public static String formatPath(final String project, final String expand) {
      String expandPath = null;
      
      if(expand != null) {
         expandPath = expand;

         if(expandPath.startsWith("/")) {
            expandPath = expandPath.substring(1); 
         } 
         if(expandPath.endsWith("/")) {
            int length = expandPath.length();
            expandPath = expand.substring(0, length - 1);
         }
         String primaryPrefix = String.format("%s%s", TreeConstants.ROOT, project);
         
         if(!expand.startsWith(primaryPrefix)){
            expandPath = TreeConstants.ROOT + project + "/" + expandPath;
         } else {
            expandPath = "/" + expandPath;
         }
      }
      return expandPath;
   }
}
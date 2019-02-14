package org.ternlang.studio.common.find.text;

import java.util.List;

import org.ternlang.studio.common.find.MatchType;

public class TextMatchTask implements Runnable {
   
   private final TextMatchListener listener;
   private final TextMatchFinder finder;
   private final TextMatchQuery query;
   private final TextFile file;
   
   public TextMatchTask(TextMatchFinder finder, TextMatchListener listener, TextMatchQuery query, TextFile file) {
      this.listener = listener;
      this.finder = finder;
      this.query = query;
      this.file = file;
   }
   
   @Override
   public void run() {
      try {
         boolean sensitive = query.isCaseSensitive();
         String expression = query.getQuery();
         MatchType type = query.getType();
         
         if(query.isEnableReplace()) {
            String replace = query.getReplace();
            List<TextMatch> matches = finder.replaceText(file, type, expression, replace, sensitive);
            listener.onMatch(file, matches);
         } else {
            List<TextMatch> matches = finder.findText(file, type, expression, sensitive);
            listener.onMatch(file, matches);
         }
      }catch(Exception cause) {
         listener.onError(file, cause);
      }
   }
}
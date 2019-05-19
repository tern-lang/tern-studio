package org.ternlang.studio.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ternlang.studio.common.console.ConsoleListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessListener implements ConsoleListener {
   
   private final Pattern pattern;
   
   public ProcessListener() {
      this.pattern = Pattern.compile("^([ |\\t]+).*", Pattern.DOTALL);
   }

   @Override
   public void onUpdate(String process, String text) {
      try {
         Matcher matcher = pattern.matcher(text);
         String trim = text.trim();
         
         if(matcher.matches()) {
            String indent = matcher.group(1);
            
            log.info(process + ": " + indent + trim);
         } else {
            log.info(process + ": " + trim);
         }
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   @Override
   public void onUpdate(String process, String text, Throwable cause) {
      try {
         Matcher matcher = pattern.matcher(text);
         String trim = text.trim();
         
         if(matcher.matches()) {
            String indent = matcher.group(1);
            
            log.info(process + ": " + indent + trim, cause);
         } else {
            log.info(process + ": " + trim, cause);
         }
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}
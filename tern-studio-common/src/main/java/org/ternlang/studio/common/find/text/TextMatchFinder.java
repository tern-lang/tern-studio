package org.ternlang.studio.common.find.text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.common.FileReader;
import org.ternlang.studio.common.find.MatchEvaluator;
import org.ternlang.studio.common.find.MatchType;

@Slf4j
public class TextMatchFinder {
   
   public TextMatchFinder() {
      super();
   }

   public List<TextMatch> findText(TextFile textFile, MatchType type, String expression, boolean sensitive) {
      File file = textFile.getFile();
      String project = textFile.getProject();
      String resource = textFile.getPath();
      
      try {
         List<TextMatch> matches = new ArrayList<TextMatch>();
         Iterator<String> lines = FileReader.readLines(file);
         MatchEvaluator matcher = MatchEvaluator.of(type, expression, sensitive);
         int index = 1;
         
         while(lines.hasNext()) {
            String line = lines.next();
            String text = matcher.match(line);
            
            if(text != null) {
               TextMatch match = new TextMatch(project, resource, text, index); // lines start at line 1
               matches.add(match);
            }
            index++;
         }
         return matches;
      }catch(Exception e) {
         log.debug("Could not read file", e);
      }
      return Collections.emptyList();
   }
   
   public List<TextMatch> replaceText(TextFile textFile, MatchType type, String expression, String replace, boolean sensitive) {
      File file = textFile.getFile();
      String project = textFile.getProject();
      String resource = textFile.getPath();
      
      try {
         StringBuilder builder = new StringBuilder();
         List<TextMatch> matches = new ArrayList<TextMatch>();
         Iterator<String> lines = FileReader.readLines(file);
         MatchEvaluator matcher = MatchEvaluator.of(type, expression, sensitive);
         int index = 1;
         
         while(lines.hasNext()) {
            String line = lines.next();
            String text = matcher.replace(line, replace);
            
            if(text != null) {
               TextMatch match = new TextMatch(project, resource, text, index); // lines start at line 1
               matches.add(match);
               builder.append(text);
            } else {
               builder.append(line);
            }
            builder.append("\n");
            index++;
         }
         FileOutputStream writer = new FileOutputStream(file);
         
         try {
            String newText = builder.toString();
            byte[] newData = newText.getBytes("UTF-8");
            
            writer.write(newData);
            writer.flush();
         }finally {
            writer.close();
         }
         return matches;
      }catch(Exception e) {
         log.debug("Could not read file", e);
      }
      return Collections.emptyList();      
   }
}
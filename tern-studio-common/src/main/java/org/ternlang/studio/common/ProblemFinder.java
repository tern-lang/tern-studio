package org.ternlang.studio.common;

import static org.ternlang.core.Reserved.GRAMMAR_FILE;
import static org.ternlang.core.Reserved.SCRIPT_EXTENSION;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ternlang.parse.SyntaxCompiler;
import org.ternlang.parse.SyntaxNode;
import org.ternlang.parse.SyntaxParser;

public class ProblemFinder {

   private final SyntaxCompiler compiler;
   
   public ProblemFinder() {
      this.compiler = new SyntaxCompiler(GRAMMAR_FILE);
   }
   
   public Problem parse(String project, String resource, String source) {
      try {
         String name = resource.toLowerCase();
         
         if(name.endsWith(SCRIPT_EXTENSION)) {
            SyntaxParser parser = compiler.compile();
            SyntaxNode node = parser.parse(resource, source, "script");
            node.getNodes();
         }
      }catch(Exception cause) {
         ProblemType[] types = ProblemType.values();
         
         for(ProblemType type : types) {
            Problem problem = type.extract(project, resource, cause);
            
            if(problem != null) {
               return problem;
            }
         }
      }
      return null;
   }
   
   private static enum ProblemType {
      NORMAL_ERROR {
         @Override
         public Problem extract(String project, String resource, Exception cause) {
            String message = cause.getMessage();
            Pattern pattern = Pattern.compile("(.*)\\s+in\\s+(.*)\\s+at\\s+line\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            
            if(matcher.matches()) {
               String description = matcher.group(1);
               String match = matcher.group(3);
               int line = Integer.parseInt(match);
               
               return new Problem(project, resource, description, line);
            }
            return null;
         }
      },
      SOURCE_EMPTY_ERROR {
         @Override
         public Problem extract(String project, String resource, Exception cause) {
            String message = cause.getMessage();
            Pattern pattern = Pattern.compile("(Source\\s+text\\s+is\\s+empty).*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            
            if(matcher.matches()) {
               String description = matcher.group(1);               
               return new Problem(project, resource, description, 1);
            }
            return null;
         }
      },
      UNKNOWN_ERROR {
         @Override
         public Problem extract(String project, String resource, Exception cause) {
            String message = cause.getMessage();
            return new Problem(project, resource, message, 1);
         }
      };
      
      abstract Problem extract(String project, String resource, Exception cause);      

   }
}
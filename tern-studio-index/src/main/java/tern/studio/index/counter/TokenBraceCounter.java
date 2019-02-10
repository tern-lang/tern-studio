package tern.studio.index.counter;

import java.util.ArrayList;
import java.util.List;

import tern.parse.GrammarIndexer;
import tern.parse.Line;
import tern.parse.SourceCode;
import tern.parse.SourceProcessor;
import tern.parse.Token;
import tern.parse.TokenIndexer;

public class TokenBraceCounter implements BraceCounter {
   
   private final SourceProcessor sourceProcessor;
   private final GrammarIndexer grammarIndexer;
   private final List<Token> tokens;
   private final String resource;
   private final String source;
   
   public TokenBraceCounter(GrammarIndexer grammarIndexer, SourceProcessor sourceProcessor, String resource, String source) {
      this.tokens = new ArrayList<Token>();
      this.sourceProcessor = sourceProcessor;
      this.grammarIndexer = grammarIndexer;
      this.resource = resource;
      this.source = source;
   }
   
   @Override
   public int getDepth(int line) {
      List<Token> tokens = getTokens();
      
      if(!tokens.isEmpty()) {
         BraceStack stack = new BraceStack();
         
         for(Token token : tokens) {
            Line position = token.getLine();
            String path = position.getResource();
            
            try{
               stack.update(token);
            }catch(Exception e){
               throw new IllegalStateException("Unbalanced braces in " + path + " at line "+ line, e);
            }
         }
         return stack.depth(line);
      }
      return 0;
   }
   
   private List<Token> getTokens() {
      if(tokens.isEmpty()) {
         SourceCode code = sourceProcessor.process(source);
         char[] original = code.getOriginal();
         char[] compress = code.getSource();
         short[] lines = code.getLines();
         short[]types = code.getTypes();
         int count = code.getCount();
         TokenIndexer tokenIndexer = new TokenIndexer(grammarIndexer, resource, original, compress, lines, types, count);
         tokenIndexer.index(tokens);
      }
      return tokens;
   }
}
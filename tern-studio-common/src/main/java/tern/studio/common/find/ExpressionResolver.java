package tern.studio.common.find;

import static java.lang.Character.toLowerCase;

import tern.common.Cache;
import tern.common.LeastRecentlyUsedCache;

/**
 * This is used to match <code>String</code>'s with the first pattern
 * that <code>String</code> matches. A pattern consists of characters
 * with either the '*' or '?' characters as wild characters. The '*'
 * character is completely wild meaning that is will match nothing or
 * a long sequence of characters. The '?' character matches a single
 * character.
 * <p>
 * If the '?' character immediately follows the '*' character then the
 * match is made as any sequence of characters up to the first match 
 * of the next character. For example "/*?/index.jsp" will match all 
 * files preceeded by only a single path. So "/pub/index.jsp" will
 * match, however "/pub/bin/index.jsp" will not, as it has two paths.
 * So, in effect the '*?' sequence will match anything or nothing up
 * to the first occurence of the next character in the pattern.
 * <p>
 * A design goal of the <code>Resolver</code> was to make it capable
 * of  high performance in a multithreaded environment. In order to
 * achieve a high performance the <code>Resolver</code> can cache the
 * resolutions it makes so that if the same text is given to the
 * <code>Resolver.resolve</code> method a cached result can be retrived
 * quickly which will decrease the length of time a thread occupies the
 * synchronized method. The cache used is a <code>CacheList</code>.
 *
 * @author Niall Gallagher
 */
public class ExpressionResolver {
   
   private final Cache<String, String> cache;
   private final char[] expression;
   
   public ExpressionResolver(String expression) {
      this.cache = new LeastRecentlyUsedCache<String, String>(500);
      this.expression = expression.toCharArray();
   }

   public synchronized String match(String source) {
      String result = cache.fetch(source);
      
      if(result == null) {
         char[] str = source.toCharArray();
         
         for(int i = 0; i < str.length; i++) {
            result = match(str, i, i, expression, 0);
            
            if(result != null) {
               cache.cache(source, result);
               return result;
            }
         }
         return null;
      }
      return result;
   }

   public static String match(char[] str, int off, int start, char[] wild, int pos){
      while(pos < wild.length && off < str.length){ /* examine chars */
         if(wild[pos] == '*'){
            while(wild[pos] == '*'){ /* totally wild */
               if(++pos >= wild.length) /* if finished */
                  return new String(str, start, str.length - start);
            }
            if(wild[pos] == '?') { /* *? is special */
               if(++pos >= wild.length)                    
                  return new String(str, start, off - start);
            }
            for(; off < str.length; off++){ /* find next matching char */
               if(str[off] == wild[pos] || wild[pos] == '?'){ /* match */
                  if(wild[pos - 1] != '?'){
                     String result = match(str, off, start, wild, pos);
                     
                     if(result != null) {
                        return result;
                     }
                  } else {
                     break;                          
                  }
               }
            }
            if(str.length == off)
               return null;
         }
         if(toLowerCase(str[off++]) != toLowerCase(wild[pos++])){
            if(wild[pos-1] != '?')
               return null; /* if not equal */
         }
      }
      if(wild.length == pos){ /* if wild is finished */
          return new String(str, start, off - start); /* is str finished */
      }
      while(wild[pos] == '*'){ /* ends in all stars */
         if(++pos >= wild.length) /* if finished */
            return new String(str, start, str.length - start);
      }
      return null;
   }
}
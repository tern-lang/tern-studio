package org.ternlang.studio.common.json.object;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ternlang.studio.common.json.document.SourceSlice;

class SymbolTable<T> implements Iterable<T> {
   
   private static final int[] POSITION = new int[256];
   private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-@";
   private static final int WIDTH = ALPHABET.length();
   
   static {
      for(int i = 0; i < POSITION.length; i++) {            
         POSITION[i] = ALPHABET.indexOf((char)i);
      }
   }
   
   private final SourceSlice slice;
   private final Set<T> values;
   private final Node root;
   
   public SymbolTable() {
      this.root = new Node(this, (char)0, 0);
      this.slice = new SourceSlice();
      this.values = new HashSet<T>();
   }
   
   public boolean isEmpty() {
      return values.isEmpty();
   }
   
   @Override
   public Iterator<T> iterator() {
      return values.iterator();
   }
   
   public T match(CharSequence key) {
      int length = key.length();
      Node node = root;
      
      for(int i = 0; i < length; i++) {
         char index = key.charAt(i);
         
         if(index > 255) {
            throw new IllegalArgumentException("Value is not a valid identifier");
         }
         if(node.lazy != null) {
            if(node.contains(key)) {
               return (T)node.lazy;
            }
         }
         node = node.get(index);
         
         if(node == null) {
            return null;
         }
      }
      return (T)node.real;
   }
   
   public T match(char[] source, int off, int length) {
      slice.with(source, off, length);
      return match(slice);
   }
   
   public void index(T value, CharSequence text) {
      root.index(value, text);
   }
   
   private void add(Object value) {
      values.add((T)value);
   }

   private static class Node {
      
      public StringBuilder builder;
      public SymbolTable table;
      public Node[] children;
      public Object lazy;
      public Object real;
      public char index;
      public int depth;
      public int count;
      
      public Node(SymbolTable table, char index, int depth) {
         this.builder = new StringBuilder(256);
         this.children = new Node[WIDTH];
         this.index = index;
         this.table = table;
         this.depth = depth;
      }
      
      public Node get(char ch) {
         if(ch <= 255 && ch >= 0) {
            int index = POSITION[ch];
            
            if(index == -1) {
               throw new IllegalStateException("Character " + ch + " is not valid");
            }
            return (Node)children[index];
         }
         return null;
      }
      
      public boolean contains(CharSequence text) {
         if(lazy != null) {
            int require = text.length();
            int actual = builder.length();
            
            if(require == actual) {
               for(int i = depth; i < actual; i++) {
                  char left = text.charAt(i);
                  char right = builder.charAt(i);
                  
                  if(left != right) {
                     return false;
                  }
               }
               return true;
            }
         }
         return false;
      }
      
      public void index(Object object, CharSequence text) {
         index(object, text, 0);
      }
      
      private void index(Object object, CharSequence text, int from) {
         int length = text.length();
         
         if(from > length) {
            throw new IllegalStateException("Position exceeds length");
         }
         if(from == length) {
            table.add(object);
            real = object;
            count++;
         } else {
            if(count == 0 && depth != 0) {
               if(lazy == null) {
                  builder.append(text);
                  table.add(object);
                  lazy = object;
               } else {
                  insert(lazy, builder, from);
                  builder.setLength(0);
                  lazy = null;
                  insert(object, text, from);
               }
            } else {
               insert(object, text, from);
            }
         }
      }
      
      private void insert(Object object, CharSequence text, int from) {
         char ch = text.charAt(from);
         
         if(ch > 255) {
            throw new IllegalArgumentException("Value '" + ch + "' is not a valid identifier");
         }
         int index = POSITION[ch];
         
         if(index == -1) {
            throw new IllegalArgumentException("Value '" + ch + "' is not a valid identifier"); 
         }
         Node node = (Node)children[index];
         
         if(node == null) {
            node = children[index] = new Node(table, ch, from + 1);
         }
         count++;
         node.index(object, text, from + 1);         
      }   
   }
}
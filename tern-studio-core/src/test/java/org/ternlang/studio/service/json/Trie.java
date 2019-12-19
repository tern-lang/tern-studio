package org.ternlang.studio.service.json;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Trie<T> implements Iterable<T> {
   
   private static final int[] POSITION = new int[256];
   private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-@";
   private static final int WIDTH = ALPHABET.length();
   
   static {
      for(int i = 0; i < POSITION.length; i++) {            
         POSITION[i] = ALPHABET.indexOf((char)i);
      }
   }
   
   private final Set<T> values = new HashSet<T>();
   private final Trie.Node root = new Node(this);
   
   public boolean isEmpty() {
      return values.isEmpty();
   }
   
   @Override
   public Iterator<T> iterator() {
      return values.iterator();
   }
   
   public T match(CharSequence key) {
      int length = key.length();
      Trie.Node node = root;
      
      for(int i = 0; i < length; i++) {
         char index = key.charAt(i);
         
         if(index > 255) {
            throw new IllegalArgumentException("Value is not a valid identifier");
         }
         node = node.get(index);
         
         if(node == null) {
            return null;
         }
      }
      return (T)node.value;
   }
   
   public T match(char[] source, int off, int length) {
      Trie.Node node = root;
      
      for(int i = 0; i < length; i++) {
         char ch = source[i + off];
         
         if(ch > 255 || ch < 0) {
            throw new IllegalArgumentException("Value is not a valid identifier");
         }
         node = node.get(ch);
         
         if(node == null) {
            return null;
         }
      }
      return (T)node.value;
   }
   
   public void index(T value, String text) {
      root.insert(value, text);
   }
   
   private static class Node {
      
      private Trie.Node[] children = new Trie.Node[WIDTH];
      private Trie parent;
      private Object value;
      
      public Node(Trie parent) {
         this.parent = parent;
      }

      public Trie.Node get(char ch) {
         if(ch <= 255 && ch >= 0) {
            int index = POSITION[ch];
            
            if(index == -1) {
               throw new IllegalStateException("Character " + ch + " is not valid");
            }
            return (Trie.Node)children[index];
         }
         return null;
      }
      
      public void insert(Object object, String text) {
         insert(object, text, 0);
      }
      
      private void insert(Object object, String text, int from) {
         int length = text.length();
         
         if(from < length) {               
            char next = text.charAt(from);
            
            if(next > 255) {
               throw new IllegalArgumentException("Value is not a valid identifier");
            }
            int index = POSITION[next];
            Trie.Node node = (Trie.Node)children[index];
            
            if(node == null) {
               node = children[index] = new Node(parent);
            }
            node.insert(object, text, from + 1);              
         } else {
            value = object;
            parent.values.add(value);
         }
      }         
   }
}
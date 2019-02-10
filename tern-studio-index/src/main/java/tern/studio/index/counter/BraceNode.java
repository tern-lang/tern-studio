package tern.studio.index.counter;

import java.util.ArrayList;
import java.util.List;

import tern.common.ArrayStack;
import tern.common.Stack;
import tern.parse.Line;

public class BraceNode {
   
   private List<BraceNode> children;
   private Stack<BraceNode> stack;
   private BraceNode parent;
   private BraceType type;
   private Line start;
   private Line finish;
   private int depth;
   
   public BraceNode(BraceNode parent, BraceType type) {
      this.children = new ArrayList<BraceNode>();
      this.stack = new ArrayStack<BraceNode>();
      this.parent = parent;
      this.type = type;
   }
   
   public BraceType getType() {
      return type;
   }
   
   public BraceNode getParent(){
      return parent;
   }
   
   public BraceNode open(BraceType type, Line line, int depth) {
      BraceNode node = new BraceNode(this, type);
      
      stack.push(node);
      node.start = line;
      node.depth = depth;
      return node;
   }
   
   public int close(BraceType type, Line line) {
      BraceNode node = stack.pop();

      if(node == null) {
         throw new IllegalStateException("Unbalanced braces");
      }
      if(node.type != type) {
         throw new IllegalStateException("Unbalanced braces");
      }
      if(node.type == BraceType.COMPOUND) {
         children.add(node);
      }
      node.finish = line;
      return stack.size();
   }
   
   public int depth(int line) {
      if(enclose(line)) {
         for(BraceNode node : children) {
            int result = node.depth(line);
            
            if(result != -1) {
               return result;
            }
         }
         return depth;
      }
      return -1;
   }
   
   private boolean enclose(int line) {
      if(start == null && finish == null) {
         return true;
      }
      int begin = start.getNumber();
      int end = finish.getNumber();
      
      return line >= begin && line <= end; 
   }
   
   public int size() {
      return stack.size();
   }
   
   @Override
   public String toString() {
      return String.format("%s -> %s", type, stack);
   }
}
package tern.studio.agent.debug;

public class ScopeContext {

   private final ScopeVariableTree tree;
   private final String source;
   
   public ScopeContext(ScopeVariableTree tree, String source) {
      this.source = source;
      this.tree = tree;
   }
   
   public ScopeVariableTree getTree(){
      return tree;
   }
   
   public String getSource(){
      return source;
   }
}

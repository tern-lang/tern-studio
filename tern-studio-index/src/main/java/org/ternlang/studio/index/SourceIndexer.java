package org.ternlang.studio.index;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.common.ArrayStack;
import org.ternlang.common.Stack;
import org.ternlang.compile.assemble.OperationBuilder;
import org.ternlang.compile.assemble.OperationTraverser;
import org.ternlang.core.Context;
import org.ternlang.core.module.FilePathConverter;
import org.ternlang.core.module.Path;
import org.ternlang.parse.Grammar;
import org.ternlang.parse.GrammarCompiler;
import org.ternlang.parse.GrammarDefinition;
import org.ternlang.parse.GrammarIndexer;
import org.ternlang.parse.GrammarReader;
import org.ternlang.parse.GrammarResolver;
import org.ternlang.parse.SourceProcessor;
import org.ternlang.parse.SyntaxNode;
import org.ternlang.studio.index.compile.IndexInstructionBuilder;
import org.ternlang.studio.index.compile.IndexInstructionResolver;
import org.ternlang.studio.index.counter.TokenBraceCounter;
import org.ternlang.tree.OperationResolver;

public class SourceIndexer {
   
   private static final String GRAMMAR_FILE = "grammar.txt";

   private final GrammarIndexer grammarIndexer;
   private final Map<String, Grammar> grammars;      
   private final GrammarResolver grammarResolver;
   private final GrammarCompiler grammarCompiler;  
   private final SourceProcessor sourceProcessor;
   private final FilePathConverter converter;
   private final PathTranslator translator;
   private final IndexDatabase database;
   private final SourceSanatizer sanatizer;
   private final GrammarReader reader;
   private final Executor executor;
   private final Context context;
   private final File root;

   public SourceIndexer(PathTranslator translator, IndexDatabase database, Context context, Executor executor, File root) {
      this(translator, database, context, executor, root, GRAMMAR_FILE);
   }
   
   public SourceIndexer(PathTranslator translator, IndexDatabase database, Context context, Executor executor, File root, String file) {
      this.grammarIndexer = new GrammarIndexer();
      this.grammars = new LinkedHashMap<String, Grammar>();      
      this.grammarResolver = new GrammarResolver(grammars);
      this.grammarCompiler = new GrammarCompiler(grammarResolver, grammarIndexer);  
      this.sourceProcessor = new SourceProcessor(100);
      this.reader = new GrammarReader(GRAMMAR_FILE);
      this.sanatizer = new SourceSanatizer(grammarResolver, grammarIndexer, grammarCompiler);
      this.converter = new FilePathConverter();
      this.translator = translator;
      this.database = database;
      this.executor = executor;
      this.context = context;
      this.root = root;
      
      for(GrammarDefinition definition : reader){
         String name = definition.getName();
         String value = definition.getDefinition();
         Grammar grammar = grammarCompiler.process(name, value);
         
         grammars.put(name, grammar);
      }
   }
   
   public SourceFile index(String resource, String source) throws Exception {
      String script = translator.getScriptPath(root, resource);
      File file = new File(root, resource);
      NodeBuilder listener = new NodeBuilder(database, resource);
      OperationBuilder builder = new IndexInstructionBuilder(listener, context, executor);
      OperationResolver resolver = new IndexInstructionResolver(context);
      OperationTraverser traverser = new OperationTraverser(builder, resolver);
      TokenBraceCounter counter = new TokenBraceCounter(grammarIndexer, sourceProcessor, script, source);
      SyntaxNode node = sanatizer.sanatize(script, source);
      Path path = converter.createPath(script);
      Object result = traverser.create(node, path);
      IndexNode top = listener.build();

      return new IndexSearcher(database, counter, top, file, resource, script);
   }   

   private static class NodeBuilder implements IndexListener {
      
      private final Stack<SourceFileNode> stack;
      private final IndexDatabase database;
      private final String resource;
      
      public NodeBuilder(IndexDatabase database, String resource) {
         this.stack = new ArrayStack<SourceFileNode>();
         this.database = database;
         this.resource = resource;
      }
      
      public IndexNode build() {
         IndexNode top = stack.pop();
         
         if(!stack.isEmpty()) {
            throw new IllegalStateException("Syntax error in " + resource);
         }
         return top;
      }

      @Override
      public void update(Index index) {
         final SourceFileNode node = new SourceFileNode(database, index, resource);
         final AtomicBoolean constructors = new AtomicBoolean();
         final AtomicBoolean merges = new AtomicBoolean();
         final IndexType type = index.getType();
         final int line = index.getLine();
         
         while(!stack.isEmpty()) {
            SourceFileNode top = stack.peek();
            Set<IndexType> parents = top.getParentTypes();
            int offset = top.getLine();
            
            if(offset < line) {
               break;
            }  
            if(parents.contains(type)) {
               if(top.getType().isConstructor()) {
                  constructors.set(true); // if == 0 then add a no arg constructor
               }
               if(node.getType().isFunction() && top.getType().isCompound() && !merges.get()) {
                  Set<IndexNode> nodes = top.getNodes();
                  
                  top.setParent(node);
                  node.getNodes().addAll(nodes);
                  merges.set(true);
               } else {
                  top.setParent(node);
                  node.getNodes().add(top);
               }
               stack.pop();      
            } else {
               break;
            }
         }
         if(type.isClass()) {
            if(!constructors.get()) {
               Index constructorIndex = createDefaultConstructor(index);
               SourceFileNode constructor = new SourceFileNode(database, constructorIndex, resource);
               
               constructor.setParent(node);
               node.getNodes().add(constructor);
            }
         }
         stack.push(node);
      }
      
      private Index createDefaultConstructor(Index index) {
         String module = index.getModule();
         String name = index.getName();
         Path path = index.getPath();
         int line = index.getLine();
         
         return new IndexResult(IndexType.CONSTRUCTOR, null, null, module, name + "()", path, line);
      }
      
   }
}

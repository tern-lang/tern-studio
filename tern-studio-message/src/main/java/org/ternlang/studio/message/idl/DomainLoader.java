package org.ternlang.studio.message.idl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.store.Store;
import org.ternlang.compile.StoreContext;
import org.ternlang.compile.assemble.Assembler;
import org.ternlang.compile.assemble.ModelScopeBuilder;
import org.ternlang.compile.assemble.OperationAssembler;
import org.ternlang.core.Context;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.EmptyModel;
import org.ternlang.core.scope.Model;
import org.ternlang.core.scope.Scope;
import org.ternlang.parse.SyntaxCompiler;
import org.ternlang.parse.SyntaxNode;
import org.ternlang.parse.SyntaxParser;
import org.ternlang.studio.message.idl.tree.Schema;

public class DomainLoader {

   private static final String INSTRUCTIONS = "idl.instruction";
   private static final String GRAMMAR = "idl.grammar";
   private static final String EXPRESSION = "schema";
   private static final String SCOPE = "default";

   public static Domain load(Iterator<URL> resources) throws Exception {
      List<Schema> schemas = new ArrayList<Schema>();
      Domain domain = new Domain();
      Model model = new EmptyModel();
      Store store = new ClassPathStore();
      Context context = new StoreContext(store);
      Executor executor = new ScheduledThreadPoolExecutor(1);
      Assembler assembler = new OperationAssembler(context, executor, INSTRUCTIONS);
      SyntaxCompiler compiler = new SyntaxCompiler(GRAMMAR);
      ModelScopeBuilder merger = new ModelScopeBuilder(context);
      Scope scope = merger.create(model, SCOPE);
      SyntaxParser analyzer = compiler.compile();

      while(resources.hasNext()) {
         URL resource = resources.next();
         DomainArtifact artifact = read(resource);
         String source = artifact.getSource();
         Path path = artifact.getPath();
         String location = path.getPath();
         SyntaxNode node = analyzer.parse(location, source, EXPRESSION);
         Schema schema = assembler.assemble(node, path);

         schema.define(scope, domain);
         schemas.add(schema);
      }
      for(Schema schema : schemas) {
         Scope child = scope.getChild(); // get a private scope
         schema.process(child, domain);
      }
      return domain;
   }

   private static DomainArtifact read(URL resource) throws Exception {
      InputStream stream = resource.openStream();
      String location = resource.toString();

      try {
         Path path = new Path(location);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] chunk = new byte[1024];
         int count = 0;

         while((count = stream.read(chunk)) != -1) {
            buffer.write(chunk, 0, count);
         }
         String source = buffer.toString("UTF-8");
         return new DomainArtifact(path, source);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read " + resource, e);
      }
   }

   private static class DomainArtifact {

      private final Path path;
      private final String source;

      public DomainArtifact(Path path, String source) {
         this.path = path;
         this.source = source;
      }
      
      public String getSource() {
         return source;
      }
      
      public Path getPath() {
         return path;
      }
   }
}

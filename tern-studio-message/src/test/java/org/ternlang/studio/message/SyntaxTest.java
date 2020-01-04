package org.ternlang.studio.message;


import java.io.File;
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
import org.ternlang.core.type.extend.FileExtension;
import org.ternlang.parse.SyntaxCompiler;
import org.ternlang.parse.SyntaxNode;
import org.ternlang.parse.SyntaxParser;

public class SyntaxTest {

   public static <T> T execute(File file) throws Exception {
      FileExtension extension = new FileExtension();
      String source = extension.readText(file);
      Path path = new Path("/idl.txt");
      Model model = new EmptyModel();
      Store store = new ClassPathStore();
      Context context = new StoreContext(store);
      Executor executor = new ScheduledThreadPoolExecutor(5);
      Assembler assembler = new OperationAssembler(context, executor, "idl.instruction");
      SyntaxCompiler compiler = new SyntaxCompiler("idl.grammar");
      ModelScopeBuilder merger = new ModelScopeBuilder(context);
      Scope scope = merger.create(model, "default");
      SyntaxParser analyzer = compiler.compile();

      analyzer.parse("/idl.txt", "int32[10]", "array");
      analyzer.parse("/idl.txt", "int32[10][2]", "array");
      analyzer.parse("/idl.txt", "enum ProcessMode{REMOTE, SERVICE,SCRIPT, TASK}", "enum-definition");
      analyzer.parse("/idl.txt", "union Event{BeginEvent, RegisterEvent }", "union-definition");
      analyzer.parse("/idl.txt", "int32[10]", "struct-constraint");
      analyzer.parse("/idl.txt", "struct BeginEvent {process: char[10]; duration: int64;}", "struct-definition");
      analyzer.parse("/idl.txt", "struct BeginEvent {process: char[10]; duration: int64;}", "schema");
      
      SyntaxNode node = analyzer.parse(file.getCanonicalPath(), source, "schema");
      System.err.println(SyntaxPrinter.print(analyzer, source, "schema")); 
      
      Object result = assembler.assemble(node, path);
      System.err.println(result);
      
      return null;
   }
   
   public static void main(String[] args) throws Exception {
      String root = "C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio-message";
      SyntaxTest.execute(new File(root, "\\src\\main\\resources\\example\\agent-events.idl"));
   }
}

package org.ternlang.studio.agent.debug;

import static org.ternlang.studio.agent.debug.ScopeVariableTree.EMPTY;

import java.util.concurrent.atomic.AtomicInteger;

import org.ternlang.core.trace.TraceType;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.event.ScopeEvent;

public class ScopeEventBuilder {

   private final ScopeVariableTree blank;
   private final ScopeExtractor extractor;
   private final AtomicInteger counter;
   private final TraceType type;
   private final String process;
   private final String resource;
   private final String thread;
   private final String stack;
   private final int line;
   private final int depth;
   private final int count;
   
   public ScopeEventBuilder(ScopeExtractor extractor, TraceType type, String process, String thread, String stack, String resource, int line, int depth, int count) {
      this.counter = new AtomicInteger();
      this.blank = EMPTY;
      this.extractor = extractor;
      this.process = process;
      this.thread = thread;
      this.resource = resource;
      this.stack = stack;
      this.line = line;
      this.depth = depth;
      this.count = count;
      this.type = type;
   }
   
   public ScopeEvent suspendEvent(ProcessMode mode) {  
      boolean remote = mode.isRemoteAttachment();
      int count = counter.getAndIncrement();
      ScopeContext context = extractor.build(remote, count > 0 || !remote); // this is totally rubbish
      ScopeVariableTree variables = context.getTree();
      String source = context.getSource();
      String name = type.name();      
 
      return new ScopeEvent.Builder(process)
         .withVariables(variables)
         .withThread(thread)
         .withStack(stack)
         .withInstruction(name)
         .withStatus(ThreadStatus.SUSPENDED)
         .withResource(resource)
         .withSource(source)
         .withLine(line)
         .withDepth(depth)
         .withKey(count)
         .build();
   }
   
   public ScopeEvent resumeEvent(ProcessMode mode) {      
      String name = type.name();

      return new ScopeEvent.Builder(process)
         .withVariables(blank)
         .withThread(thread)
         .withStack(stack)
         .withInstruction(name)
         .withStatus(ThreadStatus.RUNNING)
         .withResource(resource)
         .withLine(line)
         .withDepth(depth)
         .withKey(count)
         .build();
   }
}
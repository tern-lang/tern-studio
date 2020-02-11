package org.ternlang.studio.agent.debug;

import org.ternlang.core.trace.TraceType;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.event.ProcessEventChannel;

import java.util.concurrent.atomic.AtomicInteger;

import static org.ternlang.studio.agent.debug.ScopeVariableTree.EMPTY;

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
   
   public void startEvent(ProcessEventChannel channel, ProcessMode mode) throws Exception {
      boolean remote = mode.isRemoteAttachment();
      int count = counter.getAndIncrement();
      ScopeContext context = extractor.blank(remote); // a blank context prevents suspending on toString() 
      ScopeVariableTree variables = context.getTree();
      String source = context.getSource();
      String name = type.name();      
 
      channel.begin()
         .scope()
         .process(process)
         .variables(variables.getTree())
         .thread(thread)
         .stack(stack)
         .instruction(name)
         .status(org.ternlang.agent.message.common.ThreadStatus.SUSPENDED)
         .resource(resource)
         .source(source)
         .line(line)
         .depth(depth)
         .key(count);

      channel.send();
   }
   
   public void suspendEvent(ProcessEventChannel channel, ProcessMode mode) throws Exception {
      boolean remote = mode.isRemoteAttachment();
      int count = counter.getAndIncrement();
      ScopeContext context = extractor.build(remote, count > 0 || !remote); // this is totally rubbish
      ScopeVariableTree variables = context.getTree();
      String source = context.getSource();
      String name = type.name();      

      channel.begin()
         .scope()
         .process(process)
         .variables(variables.getTree())
         .thread(thread)
         .stack(stack)
         .instruction(name)
         .status(org.ternlang.agent.message.common.ThreadStatus.SUSPENDED)
         .resource(resource)
         .source(source)
         .line(line)
         .depth(depth)
         .key(count);

      channel.send();
   }
   
   public void resumeEvent(ProcessEventChannel channel, ProcessMode mode) throws Exception {
      String name = type.name();

      channel.begin()
         .scope()
         .process(process)
         .variables(blank.getTree())
         .thread(thread)
         .stack(stack)
         .instruction(name)
         .status(org.ternlang.agent.message.common.ThreadStatus.RUNNING)
         .resource(resource)
         .line(line)
         .depth(depth)
         .key(count);

      channel.send();
   }
}
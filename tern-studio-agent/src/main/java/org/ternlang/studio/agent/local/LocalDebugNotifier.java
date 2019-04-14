package org.ternlang.studio.agent.local;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.trace.Trace;
import org.ternlang.core.trace.TraceInterceptor;
import org.ternlang.core.trace.TraceListener;
import org.ternlang.core.trace.TraceType;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.debug.TraceAdapter;
import org.ternlang.studio.agent.local.message.AttachRequest;

public class LocalDebugNotifier {

   private final LocalProcessController controller;
   private final ProcessContext context;
   private final URI notify;
   
   public LocalDebugNotifier(LocalProcessController controller, ProcessContext context, URI notify) {
      this.controller = controller;
      this.context = context;
      this.notify = notify;
   }
   
   public void register() {
      if(notify != null) {
         TraceInterceptor interceptor = context.getInterceptor();
         String host = notify.getHost();
         String path = notify.getPath();
         int port = notify.getPort();
         int index = path.lastIndexOf("/");
         int total = path.length();
         
         if(index == -1 || index == total) {
            throw new IllegalArgumentException("Notify URL '" + notify + "' does not specify a project");
         }
         String project = path.substring(index + 1);
         int size = project.length();
         
         if(size <= 0) {
            throw new IllegalArgumentException("Notify URL '" + notify + "' does not specify a valid project");
         }
         AttachRequest request =  new AttachRequest(project, host, port);
         TraceListener trigger = new AttachTrigger(controller, context, request);
         
         System.err.println("Remote debugger " + notify);
         interceptor.register(trigger);
      }
   }
   
   private static class AttachTrigger extends TraceAdapter {
      
      private final LocalProcessController controller;
      private final ProcessContext context;
      private final AttachRequest request;
      private final AtomicBoolean active;
      
      public AttachTrigger(LocalProcessController controller, ProcessContext context, AttachRequest request) {
         this.active = new AtomicBoolean();
         this.controller = controller;
         this.context = context;
         this.request = request;
      }
      
      @Override
      public void traceBefore(Scope scope, Trace trace) {
         TraceType type = trace.getType();
         
         if(type == TraceType.DEBUG) {
            if(active.compareAndSet(false, true)) {
               TraceInterceptor interceptor = context.getInterceptor();
               
               controller.attachRequest(request);
               interceptor.traceBefore(scope, trace); // suspend once attached
            }
         }
      }
   }
}

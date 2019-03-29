package org.ternlang.studio.agent.worker;

import java.net.URI;

import org.ternlang.studio.agent.ProcessAgent;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.core.TerminateListener;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.agent.worker.store.WorkerStore;

public class WorkerProcessExecutor {
   
   private final WorkerNameGenerator generator;
   
   public WorkerProcessExecutor() {
      this.generator = new WorkerNameGenerator();
   }

   public void execute(WorkerCommandLine line) throws Exception {
      URI download = line.getDownloadURL();
      ProcessMode mode = line.getMode();
      String process = line.getName();
      LogLevel level = line.getLogLevel();
      
      if(process == null) {
         process = generator.getName();
      }
      WorkerStore store = new WorkerStore(download);
      Runnable listener = new TerminateListener(mode);
      ProcessContext context = new ProcessContext(mode, store, process);
      ProcessAgent agent = new ProcessAgent(context, level);
      
      agent.start(download, listener);
   }
}

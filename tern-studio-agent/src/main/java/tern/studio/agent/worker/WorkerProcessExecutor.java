package tern.studio.agent.worker;

import java.net.URI;

import tern.studio.agent.ProcessAgent;
import tern.studio.agent.ProcessContext;
import tern.studio.agent.ProcessMode;
import tern.studio.agent.core.TerminateListener;
import tern.studio.agent.log.LogLevel;
import tern.studio.agent.worker.store.WorkerStore;

public class WorkerProcessExecutor {

   public void execute(WorkerCommandLine line) throws Exception {
      URI download = line.getDownloadURL();
      ProcessMode mode = line.getMode();
      String process = line.getName();
      LogLevel level = line.getLogLevel();
      
      WorkerStore store = new WorkerStore(download);
      Runnable listener = new TerminateListener(mode);
      ProcessContext context = new ProcessContext(mode, store, process);
      ProcessAgent agent = new ProcessAgent(context, level);
      
      agent.start(download, listener);
   }
}

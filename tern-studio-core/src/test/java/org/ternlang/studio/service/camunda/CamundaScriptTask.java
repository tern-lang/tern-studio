package org.ternlang.studio.service.camunda;

import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ternlang.core.scope.MapModel;
import org.ternlang.studio.agent.ProcessAgent;
import org.ternlang.studio.agent.ProcessClient;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.agent.worker.store.WorkerStore;

public class CamundaScriptTask {
   
   private static final String RESOURCE = "/task.tern";
   private static final String PROJECT = "camunda";
   private static final String URL = "http://%s:4457/resource/";

   public static void main(String[] list) throws Exception {
      CamundaScriptTask task = new CamundaScriptTask();
      DelegateExecution context = new DelegateExecution("ERTF-DBRE-HERH-ERYE", "rfq.bpmn");
      
      task.onScriptTask(context);
      Thread.sleep(100000000);
   }
   
   public void onScriptTask(DelegateExecution execution) throws Exception {
      String address = InetAddress.getLocalHost().getHostAddress();
      URI location = URI.create(String.format(URL, address));
      Map<String, Object> state = new HashMap<String, Object>();      
      MapModel model = new MapModel(state);
      WorkerStore store = new WorkerStore(location);
      ProcessContext context = new ProcessContext(
            ProcessMode.TASK,
            store,
            "Camunda 2.0 - " + execution.getProcessInstanceId());
      ProcessAgent agent = new ProcessAgent(context, LogLevel.DEBUG);
      Runnable task = new Runnable() {
         @Override
         public void run() {
            System.err.println("Disconnected");
         }         
      };
      state.put("execution", execution);
      ProcessClient service = agent.start(location, task, model);

      createBreakpoints(service);
      service.beginExecute(PROJECT, RESOURCE, System.getProperty("java.class.path"), null, true);
      service.waitUntilFinish(6000000); // wait for script to finish
   }

   public void createBreakpoints(ProcessClient service) {
      String source = service.loadScript(PROJECT, RESOURCE);
      Pattern pattern = Pattern.compile(".*\\/\\/\\s*suspend.*");
      String[] list = source.split("\\r?\\n");

      for(int i = 0; i < list.length; i++) {
         String line = list[i];
         Matcher matcher = pattern.matcher(line);

         if(matcher.matches()) {
            service.createBreakpoint(RESOURCE, i+1);
         }
      }
   }

   private static class DelegateExecution {
      
      private final String processInstanceId;
      private final String processId;
      
      public DelegateExecution(String processInstanceId, String processId) {
         this.processInstanceId = processInstanceId;
         this.processId = processId;
      }

      public String getProcessInstanceId() {
         return processInstanceId;
      }

      public String getProcessId() {
         return processId;
      }
      
   }
}

package org.ternlang.studio.service.camunda;

import org.ternlang.studio.agent.local.LocalProcess;

public class CamundaLocalProcess {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
            "--url=http://192.168.56.1:4457/resource/camunda",
            "--notify=http://192.168.56.1:4457/project/camunda",
            "task2.tern",
            "foo",
            "blah");
   }
}

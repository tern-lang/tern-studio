package org.ternlang.studio.agent.local;

public class RayTraceProcessLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
              "--cp=../tern-studio/work/demo/misc/src",
              "--s=ray_tracer_no_constraints.tern",
              "--v=true");
   }
}

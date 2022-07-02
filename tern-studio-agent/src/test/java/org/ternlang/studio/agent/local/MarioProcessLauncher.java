package org.ternlang.studio.agent.local;

public class MarioProcessLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
          "--cp=../tern-demo/games/mario/src;../tern-demo/games/mario/assets/",
          "--s=mario/MarioGame.tern",
          "--w=false", // suspend
          "--p=7799",
          "--v=true",
          "foo",
          "blah");

      Thread.sleep(100000);
   }
}

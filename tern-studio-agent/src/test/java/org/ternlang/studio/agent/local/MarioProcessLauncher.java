package org.ternlang.studio.agent.local;

public class MarioProcessLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
          "--cp=../snap-studio/work/demo/games/src;../snap-studio/work/demo/games/assets/",
          "--s=mario/MarioGame.tern",
          "--w=false", // suspend
          "--p=7799",
          "--v=true",
          "foo",
          "blah");
   }
}

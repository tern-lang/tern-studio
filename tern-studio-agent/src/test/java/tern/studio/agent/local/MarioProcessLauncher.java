package tern.studio.agent.local;

public class MarioProcessLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
          "--cp=../tern-studio/work/demo/games/src;../tern-studio/work/demo/games/assets/",
          "--s=mario/MarioGame.tern",
          "--w=false", // suspend
          "--p=7799",
          "--v=true",
          "foo",
          "blah");
   }
}

package tern.studio.agent.local;

public class Render3DProcessLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
              "--cp=../snap-studio/work/demo/physics/src;../snap-studio/work/demo/physics/assets",
            "--s=wireframe/render3d.tern",
            "--v=true",
            "/wireframe/hughes_500.spec");
   }
}

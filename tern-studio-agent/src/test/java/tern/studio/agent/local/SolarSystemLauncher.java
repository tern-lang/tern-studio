package tern.studio.agent.local;

public class SolarSystemLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
              "--cp=../snap-studio/work/demo/physics/src",
              "--s=solarsystem/SolarSystem.tern",
              "--v=true");
   }
}

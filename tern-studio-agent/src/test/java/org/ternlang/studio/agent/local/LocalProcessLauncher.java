package org.ternlang.studio.agent.local;

import org.ternlang.studio.agent.local.LocalProcess;

public class LocalProcessLauncher {

   public static void main(String[] list) throws Exception {
//      ScriptService.main(
//            new String[] {
//            "--url=https://github.com/ternlang/snap-develop/raw/master/snap-develop/work/games",
//            "--script=mario.MarioGame",
//            "--verbose"});
      
//      CommandLineInterpreter.main(
//            new String[] {
//            "--e=println(x)",
//            "--x=10"});
      
//      CommandLineInterpreter.main(
//            new String[] {
//            "--cp=c:/Work/development/ternlang/snap-release/../snap-develop/snap-studio/work/demo/physics/src;c:/Work/development/ternlang/snap-release/../snap-develop/snap-studio/work/demo/physics/assets/",
//            "--s=wireframe/render3d.tern",
//            "--v=true"});     
      
      LocalProcess.main(
            new String[] {
                 "--cp=../tern-demo/games/mario/src;../tern-demo/games/mario/assets/",
            "--s=mario/MarioGame.tern",
            "--w=true", // suspend
            "--p=7799",
            "--v=true", 
            "foo", 
            "blah"});

      
//      LocalProcess.main(
//            new String[] {
//            "--cp=../snap-studio/work/demo/misc/src",      
//            "--s=text_test.tern",
//            "--e=main(args)",
//            "--p=7799",
//            "--v=true", 
//            "foo", 
//            "blah"});  
      
//      LocalProcess.main(
//            new String[] {
//            "--script=foo.tern",
//            "--verbose=true", 
//            "foo", 
//            "blah"});        
   }
}

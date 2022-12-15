package org.ternlang.studio.agent.local;

public class ScriptLauncher {

   public static void main(String[] list) throws Exception {
      LocalProcess.launch(
           "--cp=C:\\Work\\development\\tern-lang\\tern-demo\\demo\\misc\\src",
           "--s=text_test2.tern",
           "foo",
           "blah");
   }
}

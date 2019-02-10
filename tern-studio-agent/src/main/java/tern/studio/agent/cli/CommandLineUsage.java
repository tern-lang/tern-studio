package tern.studio.agent.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class CommandLineUsage {
   
   public static void usage(List<? extends CommandOption> options, String warning) {
      StringWriter builder = new StringWriter();
      PrintWriter writer = new PrintWriter(builder);
      
      if(warning != null) {
         writer.println(warning);
         writer.println();
      }
      writer.println("Usage:");
      writer.println();
      
      int name = 0;
      int code = 0;
      int pad = 3;
      
      for(CommandOption option : options) {
         if(option.getCode().length() > code) {
            code = option.getCode().length();
         }
         if(option.getName().length() > name) {
            name = option.getName().length();
         }
      }
      for(CommandOption option : options) {
         writer.print("--");
         writer.print(option.getCode());
         
         for(int i = option.getCode().length(); i < code + pad; i++){
            writer.print(" ");
         }
         writer.print("--");
         writer.print(option.getName());
         
         for(int i = option.getName().length(); i < name + pad; i++){
            writer.print(" ");
         }
         writer.print(option.getDescription());
         writer.println();
      }
      writer.println();
      writer.flush();
      writer.close();
      System.err.println(builder);
      System.err.flush();
      System.exit(0);
   }
}

package org.ternlang.studio.agent.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class CommandFile {

   private final String[] paths;

   public CommandFile(String... paths) {
      this.paths = paths;
   }

   public Deque<String> combine(String[] arguments) {
      List<String> pairs = new ArrayList<String>();

      for (String path : paths) {
         File file = new File(".", path).exists() ? new File(".", path) : new File(path);

         if (file.exists() && pairs.isEmpty()) {
            try {
               InputStream source = new FileInputStream(file);
               InputStreamReader reader = new InputStreamReader(source, "UTF-8");
               LineNumberReader iterator = new LineNumberReader(reader);

               try {
                  while (true) {
                     String line = iterator.readLine();

                     if (line == null) {
                        break;
                     }
                     String token = line.trim();

                     if (!token.startsWith(";") && !token.startsWith("[")) {
                        int index = token.indexOf("=");
                        int length = token.length();

                        if (index != -1 && index < length) {
                           String key = token.substring(0, index).trim();
                           String value = token.substring(index + 1, length).trim();
                           String argument = String.format("--%s", key);

                           pairs.add(argument);
                           pairs.add(value);
                        }
                     }
                  }
               } catch (Exception e) {
                  iterator.close();
               }
            } catch (Exception e) {
            }
         }
      }
      if (!pairs.isEmpty()) {
         for (String argument : arguments) {
            pairs.add(argument);
         }
         return new ArrayDeque<>(pairs);
      }
      return new ArrayDeque<>(Arrays.asList(arguments));
   }
}

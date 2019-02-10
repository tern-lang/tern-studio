package tern.studio.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

public class ConsoleReader {

   private final LineNumberReader parser;
   private final StringBuilder builder;
   private final InputStream buffer;
   private final Reader reader;

   public ConsoleReader(InputStream source) {
      this.buffer = new BufferedInputStream(source);
      this.reader = new InputStreamReader(buffer);
      this.parser = new LineNumberReader(reader);
      this.builder = new StringBuilder();
   }

   public String readAll() throws IOException {
      while (true) {
         String line = parser.readLine();

         if (line != null) {
            builder.append("\r\n");
            builder.append(line);
         } else {
            break;
         }
      }
      return builder.toString();
   }

   public String readLine() throws IOException {
      return parser.readLine();
   }
}
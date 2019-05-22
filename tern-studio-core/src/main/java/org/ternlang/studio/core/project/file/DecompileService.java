package org.ternlang.studio.core.project.file;

import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.project.decompile.Decompiler;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Component
@AllArgsConstructor
public class DecompileService {
   
   private final Workspace workspace;
   
   @SneakyThrows
   public FileResult decompile(String projectName, String jarFile, String className) {
      Decompiler decompiler = workspace.getDecompiler();
      String source = decompiler.decompile(jarFile, className);
      byte[] data = source.getBytes();
      long time = System.currentTimeMillis();
      
      return new FileResult("text/plain", data, time);
   }
}
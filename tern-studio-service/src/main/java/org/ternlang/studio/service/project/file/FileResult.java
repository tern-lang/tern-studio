package org.ternlang.studio.service.project.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResult {

   private final String type;
   private final byte[] data;
   private long lastModified;
}

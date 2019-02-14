package org.ternlang.studio.common.resource.display;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisplayContent {
   
   private final String path;
   private final String type;
   private final String encoding;
   private final byte[] data;
   private final long duration;
   private final double compression;
}
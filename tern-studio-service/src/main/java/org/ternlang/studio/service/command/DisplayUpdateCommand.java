package org.ternlang.studio.service.command;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayUpdateCommand implements Command {

   private Map<String, String> availableFonts;
   private String project;
   private String themeName;
   private String fontName;
   private int fontSize;
}
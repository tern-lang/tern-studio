package org.ternlang.studio.common.display;

import org.ternlang.service.annotation.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DisplayThemeService {
   
   private final DisplayPersister persister;

   public DisplayDefinition theme() {
      return persister.readDefinition();
   }

}
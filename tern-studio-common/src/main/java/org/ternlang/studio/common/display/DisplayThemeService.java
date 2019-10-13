package org.ternlang.studio.common.display;

import org.simpleframework.module.annotation.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DisplayThemeService {
   
   private final DisplayPersister persister;

   public DisplayDefinition theme(String session) {
      return persister.readDefinition(session);
   }

}
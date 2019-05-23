package org.ternlang.studio;

import org.ternlang.studio.core.StudioCommandLine;
import org.ternlang.ui.OperatingSystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StudioService {

   private final StudioCommandLine commandLine;
   private final OperatingSystem operatingSystem;
   private final String mainClass;
   private final String version;
   private final boolean previouslyDeployed;
}

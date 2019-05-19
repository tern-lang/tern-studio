package org.ternlang.studio.core.command;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrowseCommand implements Command {

   private Set<String> expand;
   private String thread;
}
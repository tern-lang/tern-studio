package org.ternlang.studio.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessDefinition {
   private final Process process;
   private final String name;
}
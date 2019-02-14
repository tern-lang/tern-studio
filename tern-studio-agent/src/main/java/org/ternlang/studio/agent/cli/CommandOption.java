package org.ternlang.studio.agent.cli;

import java.util.regex.Pattern;

public interface CommandOption {
   String getCode();
   String getName();
   String getDescription();
   Object getDefault();
   Pattern getPattern();
   Class getType();
}

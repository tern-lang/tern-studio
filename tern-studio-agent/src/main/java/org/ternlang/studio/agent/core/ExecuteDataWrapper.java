package org.ternlang.studio.agent.core;

import org.ternlang.agent.message.common.ExecuteData;
import org.ternlang.agent.message.common.ExecuteDataResultCode;
import org.ternlang.message.primitive.CharArray;
import org.ternlang.message.primitive.StringCharArray;
import org.ternlang.message.validate.ResultCode;

public class ExecuteDataWrapper implements ExecuteData {

   private final CharArray dependencies;
   private final CharArray resource;
   private final CharArray process;
   private final CharArray project;
   private final boolean debug;

   public ExecuteDataWrapper(String process, String project, String resource, String dependencies, boolean debug) {
      if (process == null) {
         throw new IllegalArgumentException("Process must not be null");
      }
      this.process = new StringCharArray(process);
      this.dependencies = new StringCharArray(dependencies == null ? "" : dependencies);
      this.resource = new StringCharArray(resource == null ? "" : resource);
      this.project = new StringCharArray(project == null ? "" : project);
      this.debug = debug;
   }

   @Override
   public CharArray dependencies() {
      return dependencies;
   }

   @Override
   public CharArray process() {
      return process;
   }

   @Override
   public CharArray resource() {
      return resource;
   }

   @Override
   public CharArray project() {
      return project;
   }

   @Override
   public boolean debug() {
      return debug;
   }

   @Override
   public ResultCode validate() {
      return ExecuteDataResultCode.OK;
   }
}

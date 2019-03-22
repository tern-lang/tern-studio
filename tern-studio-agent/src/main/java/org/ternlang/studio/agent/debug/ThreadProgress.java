package org.ternlang.studio.agent.debug;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.core.module.Path;
import org.ternlang.core.trace.Trace;
import org.ternlang.core.trace.TraceType;

public class ThreadProgress {

   private final AtomicReference<ResumeType> resume;
   private final AtomicReference<Trace> current;
   private final BreakpointMatcher matcher;
   private final AtomicInteger match;
   private final AtomicInteger depth;
   
   public ThreadProgress(BreakpointMatcher matcher) {
      this.resume = new AtomicReference<ResumeType>();
      this.current = new AtomicReference<Trace>();
      this.match = new AtomicInteger();
      this.depth = new AtomicInteger();
      this.matcher = matcher;
   }
   
   public int currentDepth() {
      return depth.get();
   }
   
   public void beforeInstruction(TraceType trace) {
      if(trace == TraceType.CONSTRUCT) {
         depth.getAndIncrement();
      } else if(trace == TraceType.INVOKE) {
         depth.getAndIncrement();
      } else if(trace == TraceType.NATIVE) {
         depth.getAndIncrement();
      }
   }
   
   public void afterInstruction(TraceType trace) {
      if(trace == TraceType.CONSTRUCT) {
         depth.getAndDecrement();
      } else if(trace == TraceType.INVOKE) {
         depth.getAndDecrement();
      } else if(trace == TraceType.NATIVE) {
         depth.getAndDecrement();
      }
   }
   
   public void resume(ResumeType type) {
      int value = depth.get();
      
      if(type == ResumeType.RUN) {
         match.set(-1);
      } else if(type == ResumeType.STEP_IN) {
         match.set(value + 1);
      } else if(type == ResumeType.STEP_OUT) {
         match.set(value - 1);
      } else if(type == ResumeType.STEP_OVER) {
         match.set(value);
      }  
      resume.set(type);
   }
   
   public boolean isSuspendBefore(Trace trace) {
      if(isMatchBefore(trace)) {
         current.set(trace);
         return true;
      }
      return false;
   }
   
   private boolean isMatchBefore(Trace trace) {
      TraceType traceType = trace.getType();
      Path path = trace.getPath();
      String resource = path.getPath();
      int line = trace.getLine();
      
      if(matcher.isBreakpoint(resource, line)){
         return true;
      }
      if(traceType == TraceType.DEBUG) {
         return true;
      }
      if(traceType == TraceType.NORMAL) {
         ResumeType resumeType = resume.get();
         int require = match.get();
         int actual = depth.get();
         
         if(resumeType != null) {
            if(resumeType == ResumeType.RUN) {
               return false;
            } else if(resumeType == ResumeType.STEP_IN) {
               return true; // always step in
            } else if(resumeType == ResumeType.STEP_OUT) {
               return actual <= require;
            } else if(resumeType == ResumeType.STEP_OVER) {
               return actual <= require;
            } 
            return require == actual; // this causes problems if line is 0
         }
      }
      return false;
   }
   
   
   public boolean isSuspendAfter(Trace trace) {
      if(isMatchAfter(trace) && isLineChange(trace)) {
         current.set(null);
         return true;
      }
      return false;
   }
   
   private boolean isMatchAfter(Trace trace) {
      TraceType traceType = trace.getType();
      
      if(traceType == TraceType.NORMAL) {
         ResumeType resumeType = resume.get();
         int require = match.get();
         int actual = depth.get();
         
         if(resumeType != null) {
            if(resumeType == ResumeType.RUN) {
               return false;
            } else if(resumeType == ResumeType.STEP_IN) {
               return true; // always step in
            } else if(resumeType == ResumeType.STEP_OUT) {
               return actual <= require;
            } else if(resumeType == ResumeType.STEP_OVER) {
               return actual <= require;
            } 
            return require == actual;
         }
      }
      return false;
   }
   
   private boolean isLineChange(Trace trace) {
      Trace previous = current.get();
      
      if(previous != null) {
         int previousLine = previous.getLine();
         int thisLine = trace.getLine();
         
         if(thisLine > 0) {
            Path previousPath = previous.getPath();
            Path thisPath = trace.getPath();
            
            if(previousLine != thisLine) {
               return true;
            }
            String previousResource = previousPath.getPath();
            String thisResource = thisPath.getPath();
            
            if(!thisResource.equals(previousResource)) {
               return true;
            }
         }
      }
      return false;
   }
   
   public void clear() {
      resume.set(null);
      match.set(-1);
   }
}
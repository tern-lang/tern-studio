package org.ternlang.studio.agent.debug;

import junit.framework.TestCase;

public class SuspendMatcherTest extends TestCase {

   public void testSuspend() throws Exception {
      BreakpointMap breakpoints = new BreakpointMap();

      breakpoints.add("/test.tern", 12);
      breakpoints.add("/test.tern", 7);
      breakpoints.remove("/test.tern", 8);

      breakpoints.add("/path/large.tern", 210);
      breakpoints.add("/path/large.tern", 66);
      breakpoints.add("/path/large.tern", 7);

      BreakpointMatcher matcher = new BreakpointMatcher();

      matcher.update(BreakpointConverter.convert(breakpoints));
      assertTrue(matcher.isBreakpoint("/test.tern", 7));
      assertFalse(matcher.isBreakpoint("/test.tern", 77));
      assertFalse(matcher.isBreakpoint("/test.tern", 1334));
      assertTrue(matcher.isBreakpoint("/test.tern", 12));
      assertFalse(matcher.isBreakpoint("/test.tern", 8));
      assertTrue(matcher.isBreakpoint("/path/large.tern", 7));
   }
}

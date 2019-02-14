package org.ternlang.studio.service;

public interface ProcessRemoteController {
   boolean start(String process); // move from waiting to running, used by agent
   boolean stop(String process); // stop the process
   boolean detach(String process); // stop pinging
   boolean ping(String process, long time); // ping the process
}
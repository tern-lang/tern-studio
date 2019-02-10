package tern.studio.agent.core;

public interface ExecuteState { 
   ExecuteData getData();
   ExecuteStatus getStatus();
   String getProcess();
   String getSystem();
   String getPid();
}

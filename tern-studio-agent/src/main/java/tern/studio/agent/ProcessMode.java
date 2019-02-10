package tern.studio.agent;

public enum ProcessMode {
    REMOTE(false, false), // background long running
    SERVICE(false, false), // background long running
    SCRIPT(true, false), // terminates when script ends
    TASK(false, true); // stops ping when script ends

    private final boolean terminate;
    private final boolean detach;

    private ProcessMode(boolean terminate, boolean detach) {
       this.terminate = terminate;
       this.detach = detach;
    }
    
    public boolean isRemoteAttachment(){
       return this == REMOTE;
    }

    public boolean isDetachRequired() {
       return detach;
    }

    public boolean isTerminateRequired(){
        return terminate;
    }
    
    public static ProcessMode resolveMode(String token) {
       ProcessMode[] modes = ProcessMode.values();
       
       for(ProcessMode mode : modes) {
          String name = mode.name();
          
          if(name.equalsIgnoreCase(token)) {
             return mode;
          }
       }
       return SCRIPT;
    }
}
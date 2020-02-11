package org.ternlang.studio.agent.core;

import org.ternlang.agent.message.common.ExecuteData;

public interface ExecuteState {
   ExecuteData getData();
   ExecuteStatus getStatus();
   String getProcess();
   String getSystem();
   String getPid();
}

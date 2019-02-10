package tern.studio.index;

import java.io.File;
import java.util.Map;

public interface SourceFile {
   File getFile();
   String getScriptPath();
   String getRealPath();
   IndexNode getRootNode();
   IndexNode getNodeAtLine(int line);
   Map<String, IndexNode> getNodesInScope(int line);
   Map<String, IndexNode> getTypeNodes();
}

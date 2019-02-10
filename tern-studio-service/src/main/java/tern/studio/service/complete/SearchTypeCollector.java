package tern.studio.service.complete;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import tern.studio.index.IndexDatabase;
import tern.studio.index.IndexNode;
import tern.studio.index.IndexType;
import tern.studio.project.Project;

public class SearchTypeCollector {

   public static Map<String, SearchTypeResult> search(Project project, String expression) throws Exception {
      IndexDatabase database = project.getIndexDatabase();
      
      if(database != null) {
         Map<String, IndexNode> nodes = database.getTypeNodesMatching(expression, true);
         Set<Entry<String, IndexNode>> entries = nodes.entrySet();
         
         if(!entries.isEmpty()) {
            Map<String, SearchTypeResult> data = new LinkedHashMap<String, SearchTypeResult>();
            
            for(Entry<String, IndexNode> entry : entries) {
               try {
                  IndexNode node = entry.getValue();
                  IndexType type = node.getType();
                  String name = node.getName();
                  String fullName = node.getFullName();
   
                  if(type == IndexType.IMPORT) {
                     Map<String, IndexNode> types = project.getIndexDatabase().getTypeNodes();
                     IndexNode imported = types.get(fullName);
                     
                     if(imported != null) {
                        name = imported.getName();
                        type = imported.getType();
                     } else {
                        type = IndexType.CLASS; // hack job
                     }
                  }
                  String category = type.getName();
                  String path = node.getResource();
                  String absolute = node.getAbsolutePath();
                  String module = node.getModule();
                  
                  data.put(name +":" + path, new SearchTypeResult(name, module, category, path,  absolute));
               } catch(Throwable e) {}
            }
            return data;
         }
      }
      return Collections.emptyMap();
   }
}

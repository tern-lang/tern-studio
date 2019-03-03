package org.ternlang.studio.index.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;
import org.ternlang.studio.agent.core.ClassPathUpdater;
import org.ternlang.studio.index.IndexNode;

@Slf4j
public class ProjectClassPath {

   private static final Cache<String, List<IndexNode>> PROJECT_CLASSPATHS =
           new LeastRecentlyUsedCache<String, List<IndexNode>>(50);

   public static List<IndexNode> getProjectClassPath(List<File> files){
      String key = files.stream()
              .filter(Objects::nonNull)
              .map(file -> file.getAbsolutePath())
              .sorted()
              .collect(Collectors.joining(","));
      List<IndexNode> nodes = PROJECT_CLASSPATHS.fetch(key);

      if(nodes == null) {
         try {
            ClassLoader loader = ClassPathUpdater.createClassLoader(files, true);
            List<IndexNode> list = new ArrayList<IndexNode>();
            Map<String, IndexNode> map = new HashMap<String, IndexNode>();
            ClassIndexNodePath path = new MapIndexNodePath(map);
            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
            Iterator<ClassInfo> infos = new ClassGraph()
                    .enableAllInfo()
                    .disableDirScanning()
                    .addClassLoader(loader)
                    .scan()
                    .getAllClasses()
                    .iterator();

            while (infos.hasNext()) {
               ClassInfo info = infos.next();

               if(info.isPublic()) {
                  ClassIndexNode node = new ClassIndexNode(path, info);
                  String type = info.getName();
                  String name = info.getSimpleName();

                  map.put(type, node);
                  map.put(name, node);
                  list.add(node);
               }
            }
            list.addAll(SystemClassPath.getDefaultNodesByType().values());
            nodes = Collections.unmodifiableList(list);
            PROJECT_CLASSPATHS.cache(key, nodes);
         } catch(Exception e) {
            log.info("Could not load classes", e);
         }
      }
      return nodes;
   }

   @AllArgsConstructor
   private static class MapIndexNodePath implements ClassIndexNodePath {

      private final Map<String, IndexNode> nodes;

      @Override
      public IndexNode getNode(String name) {
         IndexNode node = nodes.get(name);

         if(node == null) {
            return SystemClassPath.getSystemNodesByType().get(name);
         }
         return node;
      }
   }
}

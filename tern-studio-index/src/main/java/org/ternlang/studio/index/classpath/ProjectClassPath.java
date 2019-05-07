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
      String libraryPath = files.stream()
              .filter(Objects::nonNull)
              .map(file -> file.getAbsolutePath())
              .sorted()
              .collect(Collectors.joining(File.pathSeparator));
      List<IndexNode> nodes = PROJECT_CLASSPATHS.fetch(libraryPath);

      if(nodes == null) {
         try {
            ClassLoader loader = ClassPathUpdater.createClassLoader(files, true);
            List<IndexNode> list = new ArrayList<IndexNode>();
            Map<String, IndexNode> map = new HashMap<String, IndexNode>();
            ClassIndexNodePath path = new MapIndexNodePath(map);
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
                  ClassIndexNode real = new ClassIndexNode(path, info);
                  CacheIndexNode node = new CacheIndexNode(real);
                  String library = node.getResource();

                  if(libraryPath.contains(library)) { // only include paths in the classpath
                     String type = node.getTypeName();
                     String name = node.getName();

                     map.put(type, node);
                     map.put(name, node);
                     list.add(node);
                  }
               }
            }
            list.addAll(SystemClassPath.getDefaultNodesByName().values());
            nodes = Collections.unmodifiableList(list);
            PROJECT_CLASSPATHS.cache(libraryPath, nodes);
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
         String normal = name.replace('$', '.');
         IndexNode node = nodes.get(normal);

         if(node == null) {
            return SystemClassPath.getSystemNodesByType().get(normal);
         }
         return node;
      }
   }
}

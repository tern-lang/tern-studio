package org.ternlang.studio.index.classpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ternlang.core.Reserved;
import org.ternlang.core.link.ImportPathResolver;
import org.ternlang.core.link.ImportPathSource;
import org.ternlang.studio.index.IndexNode;

@Slf4j
public class SystemClassPath {

   private static final Set<IndexNode> SYSTEM_NODES = new CopyOnWriteArraySet<IndexNode>();
   private static final Map<String, IndexNode> SYSTEM_NODES_BY_TYPE = new ConcurrentHashMap<String, IndexNode>();
   private static final Map<String, IndexNode> DEFAULT_NODES = new ConcurrentHashMap<String, IndexNode>();
   private static final Map<String, IndexNode> DEFAULT_NODES_BY_TYPE = new ConcurrentHashMap<String, IndexNode>();
   private static final Map<String, IndexNode> DEFAULT_NODES_BY_NAME = new ConcurrentHashMap<String, IndexNode>();
   private static final String[][] PRIMITIVE_TYPES = new String[][] {
      new String[]{"java.lang.Integer", "int"},
      new String[]{"java.lang.Byte", "byte"},
      new String[]{"java.lang.Character", "char"},
      new String[]{"java.lang.Short", "short"},
      new String[]{"java.lang.Long", "long"},
      new String[]{"java.lang.Double", "double"},
      new String[]{"java.lang.Float", "float"},
      new String[]{"java.lang.Boolean", "boolean"},
      new String[]{"java.lang.Object", "void"},
   };

   static {
      buildSystemClassPath();
   }

   public static Set<IndexNode> getSystemNodes() {
      return Collections.unmodifiableSet(SYSTEM_NODES);
   }

   public static Map<String, IndexNode> getSystemNodesByType() {
      return Collections.unmodifiableMap(SYSTEM_NODES_BY_TYPE);
   }

   public static Map<String, IndexNode> getDefaultNodes() {
      return Collections.unmodifiableMap(DEFAULT_NODES);
   }

   public static Map<String, IndexNode> getDefaultNodesByType() {
      return Collections.unmodifiableMap(DEFAULT_NODES_BY_TYPE);
   }

   public static Map<String, IndexNode> getDefaultNodesByName() {
      return Collections.unmodifiableMap(DEFAULT_NODES_BY_NAME);
   }

   private static void buildSystemClassPath() {
      List<IndexNode> nodes = new ArrayList<IndexNode>();
      Set<String> defaults = new ImportPathSource(Reserved.IMPORT_FILE).getPath().getDefaults();
      ImportPathResolver resolver = new ImportPathResolver(Reserved.IMPORT_FILE);
      Map<String, IndexNode> map = new HashMap<String, IndexNode>();
      ClassIndexNodePath path = new MapIndexNodePath(map);
      Iterator<ClassInfo> iterator = new ClassGraph()
         .enableAllInfo()
         .disableDirScanning()
         .enableSystemJarsAndModules()
         .whitelistLibOrExtJars("rt.jar")
         .whitelistPackages("java.*", "javax.*", "sun.*", "oracle.*")
         .scan()
         .getAllClasses()
         .iterator();

      while (iterator.hasNext()) {
         ClassInfo info = iterator.next();

         if(info.isPublic()) {
            ClassIndexNode node = new ClassIndexNode(path, info);
            String type = info.getName().replace("$", ".");
            String name = info.getSimpleName();

            map.put(type, node);
            map.put(name, node);
            nodes.add(node);
         }
      }
      for(IndexNode node : nodes) {
         String type = node.getTypeName().replace("$", ".");
         String alias = resolver.resolveName(type);

         if(alias != null && !type.equals(alias)) {
            String name = node.getName();

            DEFAULT_NODES_BY_NAME.put(name, node);
            DEFAULT_NODES_BY_TYPE.put(type, node);
            DEFAULT_NODES_BY_TYPE.put(alias, node);
            SYSTEM_NODES_BY_TYPE.put(alias, node);
         }
      }
      for(String[] pair : PRIMITIVE_TYPES) {
         String name = pair[0];
         String primitive = pair[1];
         IndexNode node = map.get(name);

         if(node != null) {
            map.put(primitive, node);
            DEFAULT_NODES_BY_NAME.put(primitive, node);
            DEFAULT_NODES_BY_TYPE.put(primitive, node);
         }
      }
      DEFAULT_NODES.putAll(DEFAULT_NODES_BY_TYPE);
      DEFAULT_NODES.putAll(DEFAULT_NODES_BY_NAME);
      SYSTEM_NODES_BY_TYPE.putAll(map);
      SYSTEM_NODES.addAll(nodes);
   }

   @AllArgsConstructor
   private static class MapIndexNodePath implements ClassIndexNodePath {

      private final Map<String, IndexNode> nodes;

      @Override
      public IndexNode getNode(String name) {
         String normal = name.replace('$', '.');
         return nodes.get(normal);
      }
   }
}

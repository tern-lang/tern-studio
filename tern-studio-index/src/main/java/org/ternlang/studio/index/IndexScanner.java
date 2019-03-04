package org.ternlang.studio.index;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.core.Context;
import org.ternlang.studio.common.FileAction;
import org.ternlang.studio.common.FileProcessor;
import org.ternlang.studio.common.FileReader;
import org.ternlang.studio.index.classpath.ClassPathSearcher;
import org.ternlang.studio.index.classpath.SystemClassPath;

@Slf4j
public class IndexScanner implements IndexDatabase {

   private final Map<String, Map<String, IndexNode>> members;
   private final AtomicReference<IndexFileCache> reference;
   private final FileProcessor<SourceFile> processor;
   private final FileAction<SourceFile> action;
   private final PathTranslator translator;
   private final ClassPathSearcher searcher;
   private final SourceIndexer indexer;
   private final String project;
   private final File root;
   
   public IndexScanner(List<File> path, Context context, Executor executor, File root, String project, String... prefixes) {
      this.members = new ConcurrentHashMap<String, Map<String, IndexNode>>();
      this.reference = new AtomicReference<IndexFileCache>();
      this.searcher = new ClassPathSearcher(path);
      this.translator = new PathTranslator(prefixes);
      this.indexer = new SourceIndexer(translator, this, context, executor, root);
      this.action = new CompileAction(indexer, root);
      this.processor = new FileProcessor<SourceFile>(action, executor);
      this.project = project;
      this.root = root;
   }

   @Override
   public Map<String, SourceFile> getFiles() throws Exception {
      IndexFileCache cache = reference.get();
      
      if(cache == null || cache.isExpired()) {
         String path = root.getCanonicalPath();
         Set<SourceFile> results = processor.process(project, path + "/**.tern"); // build all resources
   
         if(!results.isEmpty()) {
            Map<String, SourceFile> matches = new HashMap<String, SourceFile>();
            
            for(SourceFile file : results) {
               String resource = file.getRealPath();
               matches.put(resource, file);
            }
            cache = new IndexFileCache(matches);
         } else {
            cache = new IndexFileCache(Collections.EMPTY_MAP);
         }
         reference.set(cache);
      }
      return cache.getFiles();
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      Map<String, SourceFile> files = getFiles();
      Collection<SourceFile> list = files.values();
      
      if(!files.isEmpty()) {
         Map<String, IndexNode> matches = new HashMap<String, IndexNode>();
         
         for(SourceFile file : list) {
            Map<String, IndexNode> nodes = file.getTypeNodes();
            Set<Entry<String, IndexNode>> entries = nodes.entrySet();
            
            for(Entry<String, IndexNode> entry : entries) {
               IndexNode node = entry.getValue();
               String name = node.getFullName();
               
               if(name != null) {
                  IndexType type = node.getType();
                  
                  if(!type.isImport()) {
                     matches.put(name, node);
                  }
               }
            }
         }
         matches.putAll(searcher.getTypeNodes()); // add project types and bootstrap types
         return Collections.unmodifiableMap(matches);
      }
      return searcher.getTypeNodes();
   }

   @Override
   public IndexNode getTypeNode(String typeName) throws Exception {
      Map<String, IndexNode> nodes = getTypeNodes();
      
      if(!nodes.isEmpty()) {
         IndexNode node = nodes.get(typeName);
         
         if(node == null) {
            node = SystemClassPath.getSystemNodesByType().get(typeName);
         }
         return node;
      }
      return null;
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression) throws Exception {
      return getTypeNodesMatching(expression, false);
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression, boolean ignoreCase) throws Exception {
      Map<String, IndexNode> nodes = getTypeNodes();
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      Pattern pattern = Pattern.compile(expression, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
      
      if(!nodes.isEmpty()) {
         Map<String, IndexNode> matches = new TreeMap<String, IndexNode>(String.CASE_INSENSITIVE_ORDER);
         
         for(Entry<String, IndexNode> entry : entries) {
            IndexNode node = entry.getValue();
            String name = node.getName();
            
            if(name != null) {
               String fullName = node.getFullName();
               IndexType type = node.getType();
               Matcher matcher = pattern.matcher(name);
               
               if(matcher.matches() && !type.isImport()) {
                  matches.put(fullName, node);
               }
            }
         }
         return matches;
      }
      return Collections.emptyMap();
   }

   @Override
   public IndexNode getDefaultImport(String module, String name) throws Exception {
      IndexNode node = getTypeNode(module + "." + name);
      
      if(node == null) {
         return getTypeNode(name);
      }
      return node;
   }

   @Override
   public SourceFile getFile(String resource, String source) throws Exception {
      return indexer.index(resource, source);
   }
   
   @Override
   public Map<String, IndexNode> getImportsInScope(IndexNode node) throws Exception {
      Map<String, IndexNode> defaultImports = SystemClassPath.getDefaultNodesByName();
      Map<String, IndexNode> scope = new HashMap<String, IndexNode>(defaultImports);
      
      while(node != null) {
         Set<IndexNode> nodes = node.getNodes();
         
         for(IndexNode entry : nodes) {
            String name = entry.getName();
            IndexType type = entry.getType();
            
            if(type.isImport()) {
               scope.put(name, entry);
            }
         }
         node = node.getParent();
      }
      return scope;
   }

   public Map<String, IndexNode> getNodesInScope(IndexNode node) throws Exception {
      Map<String, IndexNode> defaultImports = SystemClassPath.getDefaultNodesByName();
      Map<String, IndexNode> scope = new HashMap<String, IndexNode>(defaultImports);
      Map<String, IndexNode> additional = new HashMap<String, IndexNode>();
      Set<IndexNode> enclosing = new HashSet<IndexNode>();
      String module = node.getModule();
      
      while(node != null) {
         Set<IndexNode> nodes = node.getNodes();
         
         for(IndexNode entry : nodes) {
            String name = entry.getName();
            IndexType type = entry.getType();
            Map<String, IndexNode> children = getChildNodes(entry);
            
            if(!type.isSuper()) {
               Set<Entry<String, IndexNode>> pairs = children.entrySet();
               
               additional.putIfAbsent(name, entry);
               
               for(Entry<String, IndexNode> pair : pairs) {
                  String key = pair.getKey();
                  IndexNode value = pair.getValue();
                  
                  additional.putIfAbsent(key, value);
               }
            }
         }
         IndexType type = node.getType();
         
         if(type.isType()) {
            module = node.getModule();
            enclosing.add(node);
         }
         node = node.getParent();
      }
      Map<String, IndexNode> packageNodes = getNodesInSamePackage(module); // nodes in same package
      Map<String, IndexNode> inherited = getMemberNodes(enclosing); // get enclosing methods
      
      scope.putAll(packageNodes);
      scope.putAll(inherited);
      scope.putAll(additional);
      
      return scope;
   }
   
   private Map<String, IndexNode> getNodesInSamePackage(String module) throws Exception {
      Map<String, SourceFile> files = getFiles();
      Collection<SourceFile> list = files.values();
      
      if(!files.isEmpty()) {
         Map<String, IndexNode> matches = new HashMap<String, IndexNode>();
         
         for(SourceFile file : list) {
            Map<String, IndexNode> nodes = file.getTypeNodes();
            Set<Entry<String, IndexNode>> entries = nodes.entrySet();
            
            for(Entry<String, IndexNode> entry : entries) {
               IndexNode node = entry.getValue();
               String name = node.getName();
               
               if(name != null) {
                  String nodeModule = node.getModule();
                  IndexType type = node.getType();
                  
                  if(type.isType() && nodeModule.equals(module)) {
                     matches.put(name, node);
                  }
               }
            }
         }
         return Collections.unmodifiableMap(matches);
      }
      return Collections.emptyMap();
   }
   
   private Map<String, IndexNode> getMemberNodes(Set<IndexNode> enclosing) {
      Map<String, IndexNode> result = new HashMap<String, IndexNode>();
      
      try{
         for(IndexNode entry : enclosing) {
            Map<String, IndexNode> members = getMemberNodes(entry);
            result.putAll(members);
         }
      }catch(Throwable cause) {
         log.info("Could not get members", cause);
      }
      return result;
   }
   
   public Map<String, IndexNode> getMemberNodes(IndexNode node) {
      String fullName = node.getFullName();
      String absolutePath = node.getAbsolutePath();
      String typeKey = String.format("%s:%s", fullName, absolutePath);
      Map<String, IndexNode> nodes = members.get(typeKey);
      
      if(nodes == null) {
         Map<String, IndexNode> memberNodes = new LinkedHashMap<String, IndexNode>();
         Map<String, IndexNode> hierarchy = new LinkedHashMap<String, IndexNode>();
         
         try{
            collectHierarchy(node, hierarchy);
            
            for(IndexNode entry : hierarchy.values()) {
               Set<IndexNode> children = entry.getNodes();
               
               for(IndexNode child : children) {
                  if(child.isPublic()) {
                     IndexType type = child.getType();
                     
                     if(type.isFunction() || type.isProperty()) {
                        String name = child.getName();
                        
                        if(!memberNodes.containsKey(name)) {
                           memberNodes.put(name, child);
                        }
                     }
                  }
               }
            }
         }catch(Throwable cause) {
            log.info("Could not get members", cause);
         }
         memberNodes = Collections.unmodifiableMap(memberNodes);
         
         if(node.isNative()) {
            members.put(typeKey, memberNodes);
         }
         return memberNodes;
      }
      return nodes;
   }
   
   private void collectHierarchy(IndexNode node, Map<String, IndexNode> done) {
      try{
         String fullName = node.getFullName();
         
         if(done.put(fullName, node) == null) {
            Set<IndexNode> superAndInterfaces = getSuperTypeAndInterfaces(node);
            
            for(IndexNode baseNode : superAndInterfaces) {
               String baseNodeName = baseNode.getFullName();
               IndexNode realNode = getTypeNode(baseNodeName);
               
               if(realNode == null) {
                  realNode = baseNode;
               }
               collectHierarchy(realNode, done);
            }
         }
      }catch(Throwable cause) {
         log.info("Could not get hierarchy", cause);
      }
   }
   
   private static Set<IndexNode> getSuperTypeAndInterfaces(IndexNode node) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try {
         Set<IndexNode> children = node.getNodes();
         
         for(IndexNode child : children) {
            IndexType type = child.getType();
            
            if(type.isSuper()) {
               nodes.add(child);
            }
         }
         return nodes; 
      }catch(Throwable cause) {
         log.info("Could not get super types", cause);
      }
      return nodes;
   }
   
   private static Map<String, IndexNode> getChildNodes(IndexNode node) {
      String name = node.getName();
      IndexType type = node.getType();
      
      if(type.isType()) {
         return getChildNodes(node, name);
      }
      return Collections.emptyMap();
   }
   
   private static Map<String, IndexNode> getChildNodes(IndexNode node, String prefix) {
      Set<IndexNode> nodes = node.getNodes(); 
      
      if(!nodes.isEmpty()) {
         Map<String, IndexNode> scope = new HashMap<String, IndexNode>();

         for(IndexNode entry : nodes) {
            IndexType type = entry.getType();
            String name = entry.getName();
            
            if(type.isConstructor()) {
               if(prefix == null) {
                  scope.put(name, entry);
               } else if(!name.startsWith(prefix)) {
                  scope.put(prefix + "." + name, entry); // i.e new SomeClass.InnerClass()
               } else {
                  scope.put(name, entry); // new SomeClass()
               }
            } else if(type.isType()) {
               Map<String, IndexNode> children = getChildNodes(entry, name);
               Collection<IndexNode> entries = children.values();
               
               for(IndexNode child : entries) {
                  String suffix = child.getName();
                  scope.put(prefix + "." + suffix, child);
               }
               scope.put(prefix + "." + name, entry);
            } 
         }
         return scope;
      }
      return Collections.emptyMap();
   }
   
   private static class IndexFileCache {
      
      private final Map<String, SourceFile> files;
      private final long created;
      private final long expiry;
      
      public IndexFileCache(Map<String, SourceFile> files) {
         this(files, 5000);
      }
      
      public IndexFileCache(Map<String, SourceFile> files, long expiry) {
         this.created = System.currentTimeMillis();
         this.files = files;
         this.expiry = expiry;
      }
      
      public Map<String, SourceFile> getFiles() {
         return files;
      }
      
      public boolean isExpired() {
         return (created + expiry) < System.currentTimeMillis();
      }
   }
   
   
   private static class CompileAction implements FileAction<SourceFile> {
   
      private final SourceIndexer indexer;
      private final File root;
      
      public CompileAction(SourceIndexer indexer, File root) {
         this.indexer = indexer;
         this.root = root;
      }
      
      @Override
      public SourceFile execute(String reference, File file) throws Exception {
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         return indexer.index(resourcePath, source);
      }
   }
}

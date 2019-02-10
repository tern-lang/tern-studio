package tern.studio.index.classpath;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import tern.studio.index.IndexNode;
import tern.studio.index.classpath.node.ClassIndexNode;
import tern.studio.index.classpath.node.ConstructorIndexNode;
import tern.studio.index.classpath.node.FieldIndexNode;
import tern.studio.index.classpath.node.MethodIndexNode;
import tern.studio.index.classpath.node.SuperIndexNode;
import tern.studio.index.scan.ClassPathScanner;

public class ClassIndexProcessor {

   private static final Map<Class, IndexNode> CACHED_NODES = new ConcurrentHashMap<Class, IndexNode>();
   private static final Map<Class, ClassFile> CACHED_FILES = new ConcurrentHashMap<Class, ClassFile>();
   
   public static ClassFile getClassFile(Class type) {
      ClassFile file = CACHED_FILES.get(type);
      
      if(file == null) {
         String path = ClassIndexProcessor.getFullPath(type);
         
         try {
            file = ClassPathScanner.createClassFile(path);
            CACHED_FILES.put(type, file);
         }catch(Throwable cause) {
            cause.printStackTrace();
         }
      }
      return file;
   }
   
   public static Set<IndexNode> getChildren(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      nodes.addAll(getSupers(info));
      nodes.addAll(getConstructors(info));
      nodes.addAll(getMethods(info));
      nodes.addAll(getFields(info));
      nodes.addAll(getInnerClasses(info));
      
      return nodes;
   }
   
   public static Set<IndexNode> getSupers(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      Set<Class> done = new HashSet<Class>();
      
      try{
         Class type = info.loadClass();
         Set<Class> hierarchy = getHierarchy(type, done);
         
         hierarchy.remove(type);
         
         for(Class entry : hierarchy) {
            IndexNode node = getSuperIndexNode(entry);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   private static Set<Class> getHierarchy(Class type, Set<Class> types) {
      Set<Class> nodes = new HashSet<Class>();
      
      try{
         if(nodes.add(type)) {
            Set<Class> superAndInterfaces = getSuperTypeAndInterfaces(type);
            
            for(Class baseNode : superAndInterfaces) {
               if(nodes.add(baseNode)) {
                  nodes.addAll(getHierarchy(baseNode, nodes));
               }
            }
            
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   private static Set<Class> getSuperTypeAndInterfaces(Class type) {
      Set<Class> nodes = new HashSet<Class>();
      
      try {
         Class superType = type.getSuperclass();
         Class[] declaredInterfaces = type.getInterfaces();
         
         for(Class interfaceType : declaredInterfaces) {
            nodes.add(interfaceType);
         }
         if(superType != null){
            nodes.add(superType);  
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }

   public static Set<IndexNode> getConstructors(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
         Constructor[] constructors = type.getDeclaredConstructors();
         
         for(Constructor constructor : constructors) {
            IndexNode node = getIndexNode(info, constructor);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   public static Set<IndexNode> getMethods(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
         Method[] methods = type.getDeclaredMethods();
         
         for(Method method : methods) {
            IndexNode node = getIndexNode(info, method);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   public static Set<IndexNode> getFields(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
         Field[] fields = type.getDeclaredFields();
         
         for(Field field : fields) {
            IndexNode node = getIndexNode(info, field);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }
   
   public static Set<IndexNode> getInnerClasses(ClassFile info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try{
         Class type = info.loadClass();
         Class[] children = type.getDeclaredClasses();
         
         for(Class child : children) {
            IndexNode node = getIndexNode(child);
            nodes.add(node);
         }
      }catch(Throwable cause) {
         //cause.printStackTrace();
      }
      return nodes;
   }

   public static String getFullPath(Class type) {
      return type.getName().replace('.', '/') + ".class";
   }
   
   public static IndexNode getIndexNode(Class type) {
      IndexNode node = CACHED_NODES.get(type);
      
      if(node == null) {
         ClassFile info = getClassFile(type);
         node = getIndexNode(info);
         CACHED_NODES.put(type, node);
      }
      return node;
   }
   
   public static IndexNode getSuperIndexNode(Class type) {
      ClassFile info = getClassFile(type);
      return new SuperIndexNode(info);
   }

   public static IndexNode getSuperIndexNode(ClassFile info) {
      return new SuperIndexNode(info);
   }
   
   public static IndexNode getIndexNode(ClassFile info) {
      return new ClassIndexNode(info);
   }

   public static IndexNode getIndexNode(ClassFile file, Constructor constructor) {
      return new ConstructorIndexNode(file, constructor);
   }
   
   public static IndexNode getIndexNode(ClassFile file, Method method) {
      return new MethodIndexNode(file, method);
   }
   
   public static IndexNode getIndexNode(ClassFile file, Field field) {
      return new FieldIndexNode(file, field);
   }

   
}

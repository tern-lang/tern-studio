package tern.studio.index.classpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

public class ClassFileMarshaller {
   
   private static final String LIBRARY_PATH = "libraryPath";
   private static final String LIBRARY = "library";
   private static final String RESOURCE = "resource";
   private static final String FULL_NAME = "class";
   private static final String TYPE_NAME = "name";
   private static final String CATEGORY = "category";
   private static final String ORIGIN = "origin";
   private static final String MODIFIERS = "modifiers";
   private static final String SHORT_NAME = "shortName";
   private static final String MODULE = "module";
   
   private final Map<String, String> interned;
   
   public ClassFileMarshaller() {
      this.interned = new ConcurrentHashMap<String, String>();
   }

   public Map<String, String> toAttributes(ClassFile file) {
      Map<String, String> map = new HashMap<String, String>();
    
      String shortName = file.getShortName();
      String libraryPath = file.getLibraryPath();
      String resource = file.getResource();
      String library = file.getLibrary();
      String fullName = file.getFullName();
      String typeName = file.getTypeName();
      String module = file.getModule();
      ClassCategory type = file.getCategory();
      ClassOrigin origin = file.getOrigin();
      int modifiers = file.getModifiers();
      
      Preconditions.checkNotNull(libraryPath, "Attribute '" + LIBRARY_PATH + "' does not exist");
      Preconditions.checkNotNull(resource, "Attribute '" + RESOURCE + "' does not exist");
      Preconditions.checkNotNull(library, "Attribute '" + LIBRARY + "' does not exist");
      Preconditions.checkNotNull(fullName, "Attribute '" + FULL_NAME + "' does not exist");
      Preconditions.checkNotNull(typeName, "Attribute '" + TYPE_NAME + "' does not exist");
      Preconditions.checkNotNull(module, "Attribute '" + MODULE + "' does not exist");
      Preconditions.checkNotNull(type, "Attribute '" + CATEGORY + "' does not exist");
      Preconditions.checkNotNull(origin, "Attribute '" + ORIGIN + "' does not");
      Preconditions.checkNotNull(shortName, "Attribute '" + SHORT_NAME + "' does not exist");
      
      map.put(LIBRARY_PATH, libraryPath);
      map.put(RESOURCE, resource);
      map.put(LIBRARY, library);
      map.put(FULL_NAME, fullName);
      map.put(TYPE_NAME, typeName);
      map.put(CATEGORY, type.name());
      map.put(ORIGIN, origin.name());
      map.put(MODIFIERS, String.valueOf(modifiers));
      map.put(SHORT_NAME, shortName);
      map.put(MODULE, module);
      
      return Collections.unmodifiableMap(map);
   }
   
   public ClassFile fromAttributes(Map<String, String> map, ClassLoader loader) {
      Map<String, String> interned = intern(map);
      
      String shortName = interned.get(SHORT_NAME);
      String libraryPath = interned.get(LIBRARY_PATH);
      String resource = interned.get(RESOURCE);
      String library = interned.get(LIBRARY);
      String fullName = interned.get(FULL_NAME);
      String typeName = interned.get(TYPE_NAME);
      String module = interned.get(MODULE);
      String category = interned.get(CATEGORY);
      String origin = interned.get(ORIGIN);
      String modifiers = interned.get(MODIFIERS);
      
      Preconditions.checkNotNull(libraryPath, "Attribute '" + LIBRARY_PATH + "' does not exist for: " + interned);
      Preconditions.checkNotNull(resource, "Attribute '" + RESOURCE + "' does not exist for: " + interned);
      Preconditions.checkNotNull(library, "Attribute '" + LIBRARY + "' does not exist for: " + interned);
      Preconditions.checkNotNull(fullName, "Attribute '" + FULL_NAME + "' does not exist for: " + interned);
      Preconditions.checkNotNull(typeName, "Attribute '" + TYPE_NAME + "' does not exist for: " + interned);
      Preconditions.checkNotNull(module, "Attribute '" + MODULE + "' does not exist for: " + interned);
      Preconditions.checkNotNull(category, "Attribute '" + CATEGORY + "' does not exist for: " + interned);
      Preconditions.checkNotNull(origin, "Attribute '" + ORIGIN + "' does not exist for: " + interned);
      Preconditions.checkNotNull(shortName, "Attribute '" + SHORT_NAME + "' does not exist for: " + interned);
      Preconditions.checkNotNull(modifiers, "Attribute '" + MODIFIERS + "' does not exist for: " + interned);
      
      return new MapClassFile(interned, loader);
   }
   
   private Map<String, String> intern(Map<String, String> map) {
      Set<Entry<String, String>> entries = map.entrySet();
      
      if(!entries.isEmpty()) {
         Map<String, String> copy = new HashMap<String, String>();
         
         for(Entry<String, String> entry : entries) {
            String value = entry.getValue();
            String key = entry.getKey();
            String internedValue = interned.get(value);
            String internedKey = interned.get(key);
            
            if(internedValue == null) {
               internedValue = value.intern();
               interned.put(internedValue, internedValue);
            }
            if(internedKey == null) {
               internedKey = key.intern();
               interned.put(internedKey, internedKey);
            }
            copy.put(internedKey, internedValue);
         }
         return Collections.unmodifiableMap(copy);
      }
      return map;
   }
   
   private static class MapClassFile implements ClassFile {

      private final Map<String, String> map;
      private final ClassLoader loader;
      private Class type;

      public MapClassFile(Map<String, String> map, ClassLoader loader) {
         this.loader = loader;
         this.map = map;
      }

      @Override
      public ClassCategory getCategory() {
         String type = getAttribute(CATEGORY);
         return ClassCategory.valueOf(type);
      }

      @Override
      public ClassOrigin getOrigin() {
         String category = getAttribute(ORIGIN);
         return ClassOrigin.valueOf(category);
      }

      @Override
      public int getModifiers() {
         String modifiers = getAttribute(MODIFIERS);
         return Integer.parseInt(modifiers);
      }

      @Override
      public String getLibraryPath() {
         return getAttribute(LIBRARY_PATH);
      }

      @Override
      public String getResource() {
         return getAttribute(RESOURCE);
      }

      @Override
      public String getLibrary() {
         return getAttribute(LIBRARY);
      }

      @Override
      public String getFullName() {
         return getAttribute(FULL_NAME);
      }

      @Override
      public String getTypeName() {
         return getAttribute(TYPE_NAME);
      }

      @Override
      public String getShortName() {
         return getAttribute(SHORT_NAME);
      }

      @Override
      public String getModule() {
         return getAttribute(MODULE);
      }
      
      private String getAttribute(String name) {
         String value = map.get(name);
         
         if(value == null) {
            throw new IllegalStateException("Attribute '" + name + "' does not exist for: " + map);
         }
         return value;
      }

      @Override
      public Class loadClass() {
         try {
            if (type == null) {
               String path = getFullName();
               type = loader.loadClass(path);
            }
         } catch (Throwable e) {
            return null;
         }
         return type;
      }
   }
}

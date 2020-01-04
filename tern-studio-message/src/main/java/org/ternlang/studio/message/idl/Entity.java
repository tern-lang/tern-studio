package org.ternlang.studio.message.idl;

import java.util.ArrayList;
import java.util.List;

public class Entity {
   
   private final List<Property> properties;
   private final EntityType type;
   private final String name;
   private final String module;

   public Entity(EntityType type, String name, String module) {
      this.properties = new ArrayList<Property>();
      this.module = module;
      this.name = name;
      this.type = type;
   }
   
   public String getName() {
      return name;
   }
   
   public String getPackage() {
      return module;
   }
   
   public List<Property> getProperties() {
      return properties;
   }
   
   public EntityType getType() {
      return type;
   }
}

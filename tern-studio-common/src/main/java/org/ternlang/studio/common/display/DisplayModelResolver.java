package org.ternlang.studio.common.display;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.resource.template.TemplateModel;

@Component
public class DisplayModelResolver {
   
   private final DisplayPersister persister;
   private final DisplayThemeLoader loader;
   
   public DisplayModelResolver(DisplayPersister persister, DisplayThemeLoader loader) {
      this.persister = persister;
      this.loader = loader;
   }
   
   public TemplateModel getModel(String session) throws Exception {
      return getModel(session, null);
   }

   public TemplateModel getModel(String session, String name) throws Exception {
      String theme = getTheme(session, name);
      TemplateModel model = loader.getModel(theme);
      Map<String, Object> attributes = model.getAttributes();
      DisplayKey[] keys = DisplayKey.values();
      
      for(DisplayKey key : keys) {
         String required = key.name();
         
         if(!attributes.containsKey(required)) {
            throw new IllegalStateException("Theme '" + name + "' does not define '" + required + "'");
         }
      }
      Map<String, Object> variables = model.getAttributes();
      Map<String, Object> copy = new HashMap<String, Object>(variables);
      return new TemplateModel(copy);
   }
   
   private String getTheme(String session, String theme) throws Exception {
      if(theme == null) {
         DisplayDefinition definition = persister.readDefinition(session);
         return definition.getThemeName();
      }
      return theme;
   }
}
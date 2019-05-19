package org.ternlang.studio.common.display;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.xml.core.Persister;
import org.ternlang.studio.resource.ClassPathReader;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.template.TemplateModel;

@Component
public class DisplayThemeLoader {
   
   private static final String THEME_FOLDER = "context/theme";

   private final Map<String, DisplayTheme> themes;
   private final Persister persister;
   private final String path;
   
   public DisplayThemeLoader() {
      this(THEME_FOLDER);
   }
   
   public DisplayThemeLoader(String path) {
      this.themes = new ConcurrentHashMap<String, DisplayTheme>();
      this.persister = new Persister();
      this.path = path;
   }
   
   private String getThemePath(String name) throws Exception {
      if(path.endsWith("/")) {
         return String.format("%s%s.xml", path, name);
      }
      return String.format("%s/%s.xml", path, name);
   }

   private InputStream getThemeFile(String name) throws Exception {
      String location = getThemePath(name);
      byte[] data = ClassPathReader.findResource(location);
      
      if(data == null) {
         location = location.toLowerCase(); // lowercase by convention
         data = ClassPathReader.findResource(location); 
      }
      if(data == null) {
         throw new IllegalStateException("Could not find theme '" + name + "' in '" + location + "'");
      }
      return new ByteArrayInputStream(data);
   }
   
   public DisplayTheme getTheme(String name) throws Exception {
      DisplayTheme theme = themes.get(name);
      
      if(theme == null) {
         InputStream stream = getThemeFile(name);
         
         if(stream == null) {
            throw new IllegalStateException("Could not find theme '" + name + "'");
         }
         theme = persister.read(DisplayTheme.class, stream);
         name = theme.getName();
         
         themes.put(name,  theme);
         
      }
      return theme;
   }
   
   public TemplateModel getModel(String name) throws Exception {
      return getTheme(name).getModel(this);
   }
}
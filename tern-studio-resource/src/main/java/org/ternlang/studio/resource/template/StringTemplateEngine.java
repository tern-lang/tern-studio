package org.ternlang.studio.resource.template;

import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;

import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;
import org.ternlang.studio.resource.Content;
import org.ternlang.studio.resource.FileResolver;
import org.ternlang.studio.resource.action.annotation.Component;

@Component
public class StringTemplateEngine implements TemplateEngine {
   
   private static final String DEFAULT_PREFIX = "/";
   
   private final Cache<String, Template> cache;
   private final TemplateFinder finder;
   private final PropertyBinder binder;

   public StringTemplateEngine(FileResolver resolver) {
      this.cache = new LeastRecentlyUsedCache<String, Template>(1000);
      this.finder = new TemplateFinder(resolver, DEFAULT_PREFIX);
      this.binder = new PropertyBinder();
   }   

   @Override
   public String renderTemplate(TemplateModel model, String source) throws Exception {      
      TemplateFilter filter = createFilter(model);
      Template template = resolveTemplate(source);
      
      if(template != null) {
         try {
            StringWriter writer = new StringWriter();
            
            if(template != null) {
               template.render(filter, writer);
            }
            return writer.toString();
         } catch(Exception e) {
            throw new IllegalStateException("Could not render template '" + source + "'", e);
         }
      }
      return null;
   }   
   
   @Override
   public boolean validTemplate(String path) throws Exception {
      String file = finder.findPath(path);

      if (file != null) {
         return true;
      }
      return false;
   }   
   
   protected Template resolveTemplate(String source) throws Exception {
      Template template = cache.fetch(source);
      
      if(template == null || template.isStale()) {
         template = createTemplate(source);
         cache.cache(source, template);
      }
      return template;
   }
   
   protected Template createTemplate(String source) throws Exception {
      Content content = finder.findContent(source);
      
      if(content != null) {
         Reader reader = content.getReader();
         long time = System.currentTimeMillis();
         
         try {
            StringBuilder builder = new StringBuilder();
            LineNumberReader iterator = new LineNumberReader(reader);
            
            while(iterator.ready()) {
               String line = iterator.readLine();
               
               if(line == null) {
                  break;
               }
               builder.append(line);
               builder.append("\n");
            }
            String text = builder.toString();
            return new StringTemplate(content, source, text, time);
         }finally {
            reader.close();
         }
      }
      return null;
   }
   
   protected TemplateFilter createFilter(TemplateModel model) throws Exception {
      return new PropertyTemplateFilter(model, binder);
   }
}
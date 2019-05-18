package org.ternlang.studio.resource.action.build;

import static org.simpleframework.http.Protocol.CACHE_CONTROL;
import static org.simpleframework.http.Protocol.CONTENT_DISPOSITION;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.Interpolator;
import org.ternlang.studio.resource.action.annotation.Attachment;
import org.ternlang.studio.resource.action.annotation.CacheControl;
import org.ternlang.studio.resource.action.annotation.Produces;

public class MethodHeader {

   private final Map<String, String> headers;

   public MethodHeader() {
      this.headers = new LinkedHashMap<String, String>();
   }

   public void applyHeader(Context context) {
      Response response = context.getResponse();
      Set<String> names = headers.keySet();

      if (!names.isEmpty()) {
         Interpolator interpolator = new Interpolator(context);

         for (String name : names) {
            String value = headers.get(name);
            String text = interpolator.interpolate(value);

            response.setValue(name, text);
         }
      }
   }

   public void extractHeader(Annotation annotation) {
      if (annotation instanceof Attachment) {
         extractHeader((Attachment) annotation);
      }
      if (annotation instanceof Produces) {
         extractHeader((Produces) annotation);
      }
      if (annotation instanceof CacheControl) {
         extractHeader((CacheControl) annotation);
      }
   }

   private void extractHeader(Produces type) {
      String value = type.value();

      if (!value.isEmpty()) {
         headers.put(CONTENT_TYPE, value);
      }
   }

   private void extractHeader(CacheControl control) {
      String type = control.value();

      if (!type.isEmpty()) {
         headers.put(CACHE_CONTROL, type);
      }
   }

   private void extractHeader(Attachment disposition) {
      String type = disposition.value();

      if (!type.isEmpty()) {
         headers.put(CONTENT_DISPOSITION, "attachment; filename=\"" + type + "\"");
      }
   }
}

package org.ternlang.studio.resource.action;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;

public class Interpolator {

   private final Request request;
   private final Model model;

   public Interpolator(Context context) {
      this.request = context.getRequest();
      this.model = context.getModel();
   }

   public Object interpolate(Object value) {
      if (value instanceof String) {
         return interpolate((String) value);
      }
      return value;
   }

   public String interpolate(String text) {
      if (text != null && text.indexOf('$') != -1) {
         StringBuilder builder = new StringBuilder();

         if (!text.isEmpty()) {
            char[] data = text.toCharArray();

            interpolate(builder, data);
         }
         return builder.toString();
      }
      return text;
   }

   private void interpolate(StringBuilder builder, char[] data) {
      for (int i = 0; i < data.length; i++) {
         if (data[i] == '$') {
            if (i + 1 < data.length && data[i + 1] == '{') {
               int start = i + 2;
               int mark = i;
               int size = 0;

               for (i = start; i < data.length; i++) {
                  char next = data[i];

                  if (next == '}') {
                     size = i - start;
                     break;
                  }
               }
               if (size > 0) {
                  replace(builder, data, start, size);
               } else {
                  builder.append(data, mark, i - mark);
               }
            } else {
               builder.append(data[i]);
            }
         } else {
            builder.append(data[i]);
         }
      }
   }

   private void replace(StringBuilder builder, char[] data, int off, int len) {
      String name = new String(data, off, len);

      if (!name.isEmpty()) {
         String value = token(name);

         if (value != null) {
            builder.append(value);
         } else {
            builder.append("${");
            builder.append(name);
            builder.append("}");
         }
      }
   }

   private String token(String name) {
      if(model != null) {
         Object value = model.getAttribute(name);
         Query query = request.getQuery();
   
         if (value == null) {
            value = query.get(name);
         }
         if (value == null) {
            value = request.getAttribute(name);
         }
         if (value == null) {
            return null;
         }      
         return String.valueOf(value);
      }
      return null;
   }
}

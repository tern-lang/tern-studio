package org.ternlang.studio.common.display;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import java.util.zip.GZIPOutputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.resource.FileResolver;
import org.simpleframework.resource.MediaTypeResolver;
import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;
import org.ternlang.studio.common.SessionCookie;

@Component
public class DisplayContentProcessor {
   
   private static final String ENCODING_TYPE = "gzip";
   private static final String TEXT_TYPE = "text";
   private static final String JAVASCRIPT_TYPE = "application/javascript";

   private final Cache<String, DisplayContent> contentCache;
   private final DisplayInterpolator displayInterpolator;
   private final MediaTypeResolver typeResolver;
   private final FileResolver fileResolver;

   public DisplayContentProcessor(DisplayInterpolator displayInterpolator, FileResolver fileResolver, MediaTypeResolver typeResolver) {
      this.contentCache = new LeastRecentlyUsedCache<String, DisplayContent>(1000);
      this.displayInterpolator = displayInterpolator;
      this.fileResolver = fileResolver;
      this.typeResolver = typeResolver;
   }

   public DisplayContent create(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      DisplayContent content = contentCache.fetch(target); // THIS CACHE DOES NOT WORK WITH REGARDS THEME
      
      //if(content == null) {
         content = compress(request, response);
        // contentCache.cache(target, content);
      //}
      return content;
   }
   
   private DisplayContent compress(Request request, Response response) throws Exception {
      String session = SessionCookie.findOrCreate(request, response);
      Path path = request.getPath();
      String target = path.getPath();
      String type = typeResolver.resolveType(target);
      String accept = request.getValue(Protocol.ACCEPT_ENCODING);
      long start = System.currentTimeMillis();
      
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         Predicate<String> filter = value -> type.startsWith(TEXT_TYPE) || type.startsWith(JAVASCRIPT_TYPE);
         OutputStream output = buffer;
         InputStream input = null;
         String encoding = null;
         double original = 0.0;
         
         if(accept.contains(ENCODING_TYPE) && filter.test(type)) { // only compress text
            input = displayInterpolator.interpolate(session, target); // interpolate all text files based on the selected theme
            output = new GZIPOutputStream(buffer);
            encoding = ENCODING_TYPE;
         } else {
            input = fileResolver.resolveContent(target).getInputStream();
         }
         byte[] block = new byte[8192];
         int count = 0;
         
         while((count = input.read(block)) != -1) {
            output.write(block, 0, count);
            original += count;
         }
         output.close();
         input.close();
         byte[] data = buffer.toByteArray();
         long finish = System.currentTimeMillis();
         double ratio = (data.length / original) * 100.0; // 2 / 10
         int percentage = (int)ratio;
         
         return new DisplayContent(target, type, encoding, data, finish - start, percentage);
      } catch(Exception e) {
         throw new IllegalStateException("Could not compress " + target, e);
      }

   }
}
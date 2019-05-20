package org.ternlang.studio.core.image;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Path("/img/theme")
public class ImageScaleResource {

   private final ImageScaleService service;

   @GET
   @SneakyThrows
   @Path("/.*.png")
   public byte[] scale(Request request, Response response) {
      String target = request.getPath().getPath();
      ImageScaleService.ScaledImage image = service.getImage(target);
      String type = image.getType();
      byte[] data = image.getData();
      int height = image.getHeight();
      int width = image.getWidth();
      
      response.setContentType(type);
      
      if(log.isTraceEnabled()) {
         log.trace(target + " scaled=" + width + "x" + height);
      }
      return data;
   }
}
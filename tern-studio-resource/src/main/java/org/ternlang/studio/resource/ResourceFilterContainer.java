package org.ternlang.studio.resource;

import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFilterContainer implements Container {

   private static final Logger LOG = LoggerFactory.getLogger(ResourceFilterContainer.class);

   private final ResourceMatcher matcher;
   private final ResourceFilter filter;
   private final Resource failure;
   private final Status status;

   public ResourceFilterContainer(ResourceFilter filter, ResourceMatcher matcher) {
      this(filter, matcher, OK);
   }

   public ResourceFilterContainer(ResourceFilter filter, ResourceMatcher matcher, Status status) {
      this(filter, matcher, null, status);
   }

   public ResourceFilterContainer(ResourceFilter filter, ResourceMatcher matcher, Resource failure) {
      this(filter, matcher, failure, OK);
   }

   public ResourceFilterContainer(ResourceFilter filter, ResourceMatcher matcher, Resource failure, Status status) {
      this.failure = failure;
      this.matcher = matcher;
      this.status = status;
      this.filter = filter;
   }

   @Override
   public void handle(Request request, Response response) {      
      try {
         Resource resource = matcher.match(request, response);
         long time = System.currentTimeMillis();

         if(!filter.before(request, response)) {
            response.setDate(DATE, time);
            response.setCode(status.code);
            response.setDescription(status.description);
            resource.handle(request, response);
         }
      } catch (Throwable cause) {
         LOG.info("Error handling resource", cause);

         try {
            if (failure != null) {
               response.reset();
               failure.handle(request, response);
               response.close();
            }
         } catch (Throwable fatal) {
            LOG.info("Could not send an error response", fatal);
         }
      } finally {
         try {      
            if(!filter.after(request, response)) {
               response.close();
            }
         } catch (IOException ignore) {
            LOG.info("Could not close response", ignore);
         }
      }
   }
}
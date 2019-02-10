package tern.studio.common.resource;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface ResourceFilter {
   boolean before(Request request, Response response);
   boolean after(Request request, Response response);
}
package org.ternlang.studio.resource.action;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.action.write.ResponseWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionResource implements Resource {

   private final ResponseWriter router;
   private final Context context;
   private final Action action;

   public ActionResource(ResponseWriter router, Action action, Context context) {
      this.context = context;
      this.action = action;
      this.router = router;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      try {
         Object result = action.execute(context);

         if (!response.isCommitted()) {
            Throwable cause = context.getError();

            if (result != null) {
               context.setResult(result);
               router.write(context, result);
            } else {
               router.write(context, cause);
            }
         }
      } catch (Throwable cause) {
         log.info("Error processing request", cause);

         if (!response.isCommitted()) {
            context.setError(cause);
            router.write(context, cause);
         }
      }
      response.close();
   }
}

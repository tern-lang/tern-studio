package org.ternlang.studio.common;

import java.util.UUID;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class SessionCookie {

   public static final String SESSION_ID = "SESSID";

   public static String findOrCreate(Request request, Response response) {
      Cookie cookie = request.getCookie(SESSION_ID);

      if(cookie == null) {
         String token = UUID.randomUUID().toString();
         response.setCookie(SESSION_ID, token);
         return token;
      }
      return cookie.getValue();
   }
}

package org.ternlang.studio.resource.action.build;

import static org.simpleframework.http.Protocol.COOKIE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.http.Address;
import org.simpleframework.http.ContentType;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Part;
import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.message.MessageHeader;
import org.simpleframework.http.parse.AddressParser;
import org.simpleframework.http.parse.ContentTypeParser;
import org.simpleframework.transport.Certificate;
import org.simpleframework.transport.Channel;

public class MockRequest extends MessageHeader implements Request {

   private final String source;
   private final String target;
   private final String method;
   private final String body;
   private final boolean secure;

   public MockRequest(String method, String target, String body) {
      this(method, target, body, null);
   }

   public MockRequest(String method, String target, String body, String host) {
      this(method, target, body, host, null);
   }

   public MockRequest(String method, String target, String body, String host, boolean secure) {
      this(method, target, body, host, null, secure);
   }

   public MockRequest(String method, String target, String body, String host, String source) {
      this(method, target, body, host, source, false);
   }

   public MockRequest(String method, String target, String body, String host, String source, boolean secure) {
      this.target = target;
      this.method = method;
      this.source = source;
      this.secure = secure;
      this.body = body;
      setValue(Protocol.HOST, host);
   }

   public long getRequestTime() {
      return 0;
   }

   public List<Locale> getLocales() {
      return Collections.emptyList();
   }

   public ContentType getContentType() {
      String contentType = getValue(Protocol.CONTENT_TYPE);
      if (contentType == null) {
         return null;
      }
      return new ContentTypeParser(contentType);
   }

   public long getContentLength() {
      return getLong(Protocol.CONTENT_LENGTH);
   }

   public CharSequence getHeader() {
      return toString();
   }

   public String getMethod() {
      return method;
   }

   public String getTarget() {
      return target;
   }

   public Address getAddress() {
      return new AddressParser(target);
   }

   public Path getPath() {
      return getAddress().getPath();
   }

   public Query getQuery() {
      return getAddress().getQuery();
   }

   public int getMajor() {
      return 1;
   }

   public int getMinor() {
      return 1;
   }

   public boolean isSecure() {
      return secure;
   }

   public boolean isKeepAlive() {
      return false;
   }

   public Certificate getClientCertificate() {
      return null;
   }

   public InetSocketAddress getClientAddress() {
      if (source != null) {
         Pattern pattern = Pattern.compile("(\\d+).(\\d+).(\\d+).(\\d+)");
         Matcher matcher = pattern.matcher(source);

         if (!matcher.matches()) {
            throw new IllegalStateException("Source I.P address is in an incorrect format: " + source);
         }
         byte[] address = new byte[] { Byte.parseByte(matcher.group(1)), Byte.parseByte(matcher.group(2)), Byte.parseByte(matcher.group(3)),
               Byte.parseByte(matcher.group(4)), };
         try {
            return new InetSocketAddress(InetAddress.getByAddress(address), 0);
         } catch (Exception e) {
            throw new IllegalStateException("Could not create address", e);
         }
      }
      return new InetSocketAddress(-1);
   }

   public Map getAttributes() {
      return Collections.emptyMap();
   }

   public Object getAttribute(Object key) {
      return null;
   }
   
   public Channel getChannel() {
      return null;
   }   

   public String getParameter(String name) {
      return getQuery().get(name);
   }

   public Part getPart(String name) {
      return null;
   }

   public List<Part> getParts() {
      return new ArrayList<Part>();
   }

   public String getContent() throws IOException {
      return body;
   }

   public InputStream getInputStream() throws IOException {
      if (body == null) {
         return new ByteArrayInputStream(new byte[] {});
      }
      return new ByteArrayInputStream(body.getBytes("UTF-8"));
   }

   public ReadableByteChannel getByteChannel() throws IOException {
      return Channels.newChannel(getInputStream());
   }

   public String toString() {
      StringBuilder head = new StringBuilder(256);

      head.append(method);
      head.append(' ');
      head.append(target);
      head.append(' ');
      head.append("HTTP/1.1\r\n");

      for (String name : getNames()) {
         String value = getValue(name);
         head.append(name);
         head.append(": ");
         head.append(value);
         head.append("\r\n");
      }
      for (Cookie cookie : getCookies()) {
         head.append(COOKIE);
         head.append(": ");
         head.append(cookie.toClientString());
         head.append("\r\n");
      }
      return head.append("\r\n").toString();
   }
}

package org.ternlang.studio.resource.action.build;

import static org.simpleframework.http.Protocol.CLOSE;
import static org.simpleframework.http.Protocol.CONNECTION;
import static org.simpleframework.http.Protocol.CONTENT_LENGTH;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Protocol.SET_COOKIE;
import static org.simpleframework.http.Protocol.TRANSFER_ENCODING;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.message.MessageHeader;
import org.simpleframework.http.parse.ContentTypeParser;

public class MockResponse extends MessageHeader implements Response {

   private OutputStream body;
   private String description;
   private boolean committed;
   private boolean closed;
   private boolean reset;
   private int code;
   private int major;
   private int minor;

   public MockResponse() {
      this(System.out);
   }

   public MockResponse(OutputStream body) {
      this.body = body;
      this.major = 1;
      this.minor = 1;
   }

   public long getResponseTime() {
      return 0;
   }

   public String getTransferEncoding() {
      return getValue(TRANSFER_ENCODING);
   }

   public ContentType getContentType() {
      String contentType = getValue(CONTENT_TYPE);

      if (contentType == null) {
         return null;
      }
      return new ContentTypeParser(contentType);
   }

   public long getContentLength() {
      return getLong(CONTENT_LENGTH);
   }

   public CharSequence getHeader() {
      return toString();
   }

   public int getCode() {
      return code;
   }

   public void setCode(int code) {
      this.code = code;
   }

   public Status getStatus() {
      return Status.getStatus(code);
   }

   public void setStatus(Status status) {
      setCode(status.code);
      setDescription(status.description);
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String text) {
      this.description = text;
   }

   public int getMajor() {
      return major;
   }

   public void setMajor(int major) {
      this.major = major;
   }

   public int getMinor() {
      return minor;
   }

   public void setMinor(int minor) {
      this.minor = minor;
   }

   public void setContentLength(long length) {
      setLong(CONTENT_LENGTH, length);
   }

   public void setContentType(String type) {
      setValue(CONTENT_TYPE, type);
   }

   public OutputStream getOutputStream() throws IOException {
      return body;
   }

   public OutputStream getOutputStream(int size) throws IOException {
      return body;
   }

   public PrintStream getPrintStream() throws IOException {
      return getPrintStream(0);
   }

   public PrintStream getPrintStream(int size) throws IOException {
      return new PrintStream(body, false, "UTF-8");
   }

   public WritableByteChannel getByteChannel() throws IOException {
      return getByteChannel(0);
   }

   public WritableByteChannel getByteChannel(int size) throws IOException {
      return Channels.newChannel(body);
   }

   public boolean isKeepAlive() {
      String value = getValue(CONNECTION);

      if (value != null) {
         return value.equalsIgnoreCase(CLOSE);
      }
      return minor > 0;
   }

   public boolean isCommitted() {
      return committed;
   }

   public boolean isReset() {
      return reset;
   }

   public boolean isClosed() {
      return closed;
   }

   public void commit() throws IOException {
      committed = true;
   }

   public void reset() throws IOException {
      reset = true;
   }

   public void close() throws IOException {
      closed = true;
   }

   public String toString() {
      StringBuilder head = new StringBuilder(256);

      head.append("HTTP/").append(major);
      head.append('.').append(minor);
      head.append(' ').append(code);
      head.append(' ').append(description);
      head.append("\r\n");

      for (String name : getNames()) {
         for (String value : getAll(name)) {
            head.append(name);
            head.append(": ");
            head.append(value);
            head.append("\r\n");
         }
      }
      for (Cookie cookie : getCookies()) {
         head.append(SET_COOKIE);
         head.append(": ");
         head.append(cookie);
         head.append("\r\n");
      }
      return head.append("\r\n").toString();
   }
}

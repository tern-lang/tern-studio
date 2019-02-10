package tern.studio.agent.event;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class MessageChecker {

   public static long check(byte[] data, int offset, int length) {
      Checksum checksum = new Adler32();
      checksum.update(data, offset, length);
      return checksum.getValue();
   }
}
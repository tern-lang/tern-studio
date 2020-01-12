package org.ternlang.studio.message.primitive;

import junit.framework.TestCase;

import org.ternlang.studio.message.ByteArrayFrame;

public class CharArrayCodecTest extends TestCase {   

   public void testLimits() throws Exception {
      CharArrayCodec codec = new CharArrayCodec(5);
      ByteArrayFrame frame = new ByteArrayFrame();
      
      codec.with(frame, 0, Integer.MAX_VALUE);       
      assertEquals(0, codec.length());
      
      codec.add('1');
      codec.add('2');
      codec.add('3');
      codec.add('4');
      codec.add('5');
      
      boolean failure = false;
      
      try {
         codec.add('6');
      } catch(Exception e) {
         e.printStackTrace();
         failure = true;
      }
      
      assertTrue(failure);
      assertEquals(codec.toString(), "12345");
  }
   
   public void testSetters() throws Exception {
       CharArrayCodec codec = new CharArrayCodec(32);
       ByteArrayFrame frame = new ByteArrayFrame();
       
       codec.with(frame, 0, Integer.MAX_VALUE);       
       assertEquals(0, codec.length());
       
       int i = 0;
       
       codec.set(i++, 'H');
       codec.set(i++, 'e');
       codec.set(i++, 'l');
       codec.set(i++, 'l');
       codec.set(i++, 'o');
       codec.set(i++, ' ');
       codec.set(i++, 'W');
       codec.set(i++, 'o');
       codec.set(i++, 'r');
       codec.set(i++, 'l');
       codec.set(i++, 'd');
       codec.set(i++, '!');
       
       assertEquals(codec.toString(), "Hello World!");
   }

    public void testBasicString() throws Exception {
        CharArrayCodec codec = new CharArrayCodec(32);
        ByteArrayFrame frame = new ByteArrayFrame();
        
        codec.with(frame, 0, Integer.MAX_VALUE);
        
        assertEquals(0, codec.length());
        
        codec.add('H');

        assertEquals(1, codec.length());
        assertEquals('H', codec.charAt(0));
        
        codec.add('e');
        codec.add('l');
        codec.add('l');
        codec.add('o');
        codec.add(' ');
        codec.add('W');
        codec.add('o');
        codec.add('r');
        codec.add('l');
        codec.add('d');
        codec.add('!');
        
        assertEquals(codec.toString(), "Hello World!");
        assertEquals(new String(frame.getByteArray(), 2, codec.length() * 2, "UTF-16"), "Hello World!");
        
        codec.set(0, 'B');
        codec.set(1, 'o');
        
        assertEquals(codec.toString(), "Bollo World!");
    }
}

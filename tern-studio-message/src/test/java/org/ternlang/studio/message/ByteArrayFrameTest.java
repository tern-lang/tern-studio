package org.ternlang.studio.message;

import junit.framework.TestCase;

public class ByteArrayFrameTest extends TestCase {

    public void testByteArrayFrame() throws Exception {
        ByteArrayFrame frame = new ByteArrayFrame();

        frame.setShort(0, Short.MIN_VALUE);
        assertEquals(frame.getShort(0), Short.MIN_VALUE);
        
        frame.setShort(0, Short.MAX_VALUE);
        assertEquals(frame.getShort(0), Short.MAX_VALUE);
        
        frame.setInt(0, Integer.MAX_VALUE);
        assertEquals(frame.getInt(0), Integer.MAX_VALUE);

        frame.setLong(0, 1234567890L);
        assertEquals(frame.getLong(0), 1234567890L);
        
        frame.setDouble(12, 7883.445);
        assertEquals(frame.getDouble(12), 7883.445);
    }
}

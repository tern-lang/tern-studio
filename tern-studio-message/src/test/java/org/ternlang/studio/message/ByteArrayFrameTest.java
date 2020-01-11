package org.ternlang.studio.message;

import junit.framework.TestCase;

public class ByteArrayFrameTest extends TestCase {

    public void testByteArrayFrame() throws Exception {
        ByteArrayFrame frame = new ByteArrayFrame();

        frame.setDouble(12, 7883.445);
        assertEquals(frame.getDouble(12), 7883.445);
    }
}

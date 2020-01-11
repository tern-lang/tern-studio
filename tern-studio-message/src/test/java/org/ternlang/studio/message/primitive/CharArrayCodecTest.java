package org.ternlang.studio.message.primitive;

import junit.framework.TestCase;

public class CharArrayCodecTest extends TestCase {

    public void testBasicString() throws Exception {
        CharArrayCodec codec = new CharArrayCodec(32);

        assertEquals(0, codec.length());
    }
}

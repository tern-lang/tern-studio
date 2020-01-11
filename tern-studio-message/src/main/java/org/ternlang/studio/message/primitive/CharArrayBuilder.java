package org.ternlang.studio.message.primitive;

public interface CharArrayBuilder extends CharArray {
    CharArrayBuilder setLength(int length);
    CharArrayBuilder setChar(int offset, char value);
    CharArrayBuilder setChars(int offset, CharSequence values);
    CharArrayBuilder getChars(int from, char[] array, int offset, int length);
}

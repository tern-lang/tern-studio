package org.ternlang.studio.message.primitive;

public interface CharArrayBuilder extends CharArray {
    CharArrayBuilder length(int length);
    CharArrayBuilder add(char value);
    CharArrayBuilder set(int offset, char value);
    CharArrayBuilder set(int offset, CharSequence values);
    CharArrayBuilder get(int from, char[] array, int offset, int length);
    CharArrayBuilder clear();
}

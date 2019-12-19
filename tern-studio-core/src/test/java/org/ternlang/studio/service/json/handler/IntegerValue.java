package org.ternlang.studio.service.json.handler;

public interface IntegerValue {
   CharSequence toToken();   
   long toLong();
   int toInteger();
}
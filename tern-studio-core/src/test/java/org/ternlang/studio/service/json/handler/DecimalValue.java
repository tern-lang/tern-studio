package org.ternlang.studio.service.json.handler;

public interface DecimalValue {
   CharSequence toToken();   
   double toDouble();
   float toFloat();
}
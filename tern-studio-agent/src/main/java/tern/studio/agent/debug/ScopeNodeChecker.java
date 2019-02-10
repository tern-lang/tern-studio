package tern.studio.agent.debug;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tern.core.convert.PrimitivePromoter;

public class ScopeNodeChecker {

   private final PrimitivePromoter promoter;

   public ScopeNodeChecker() {
      this.promoter = new PrimitivePromoter();
   }

   public boolean isPrimitive(Class actual) {
      Class type = promoter.promote(actual);
      
      if (type == String.class) {
         return true;
      }else if (type == Integer.class) {
         return true;
      }else if (type == Double.class) {
         return true;
      }else if (type == Float.class) {
         return true;
      }else if (type == Long.class) {
         return true;
      }else if (type == BigInteger.class) {
         return true;
      }else if (type == BigDecimal.class) {
         return true;
      }else if (type == AtomicInteger.class) {
         return true;
      }else if (type == AtomicLong.class) {
         return true;
      }else if (type == AtomicBoolean.class) {
         return true;
      }else if (type == Boolean.class) {
         return true;
      }else if (type == Short.class) {
         return true;
      }else if (type == Character.class) {
         return true;
      }else if (type == Byte.class) {
         return true;
      }else if (type == Date.class) {
         return true;
      }else if (type == Locale.class) {
         return true;
      }else {
         Class parent = type.getSuperclass();
         
         if(parent != null) {
            if(parent.isEnum() || type.isEnum()) {
               return true;
            }
         }
      }
      return false;
   }
}
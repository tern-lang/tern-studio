package tern.studio.agent.debug;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import tern.core.Context;
import tern.core.scope.instance.Instance;
import tern.core.type.Type;
import tern.core.convert.proxy.ProxyWrapper;

public class ArrayStringBuilder {
   
   private final Context context;
   private final int limit;
   
   public ArrayStringBuilder(Context context, int limit) {
      this.context = context;
      this.limit = limit;
   }

   public String toString(byte[] array) {
      if (array != null) {
         byte[] copy = new byte[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(int[] array) {
      if (array != null) {
         int[] copy = new int[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(long[] array) {
      if (array != null) {
         long[] copy = new long[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(short[] array) {
      if (array != null) {
         short[] copy = new short[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(double[] array) {
      if (array != null) {
         double[] copy = new double[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(float[] array) {
      if (array != null) {
         float[] copy = new float[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(char[] array) {
      if (array != null) {
         char[] copy = new char[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(boolean[] array) {
      if (array != null) {
         boolean[] copy = new boolean[array.length > limit ? limit : array.length];
         System.arraycopy(array, 0, copy, 0, copy.length);
         return Arrays.toString(copy);
      }
      return "null";
   }

   public String toString(Object[] array) {
      Set done = new HashSet();

      if (array != null) {
         return toString(array, done);
      }
      return "null";
   }

   private String toString(Object[] array, Set<Object[]> done) {
      StringBuilder buffer = new StringBuilder();

      if (array == null) {
         buffer.append("null");
      } else if (array.length == 0) {
         done.add(array);
         buffer.append("[]");
      } else {
         done.add(array);
         buffer.append('[');

         for (int i = 0; i < Math.min(array.length, limit); i++) {
            Object element = array[i];
            int length = buffer.length();
            
            if(length > limit) {
               return buffer.toString();
            }
            if(i > 0) {
               buffer.append(", ");  
            }
            if (element == null) {
               buffer.append("null");
            } else {
               Class<?> entry = element.getClass();

               if (entry.isArray()) {
                  if (entry == byte[].class) {
                     buffer.append(toString((byte[]) element));
                  } else if (entry == short[].class) {
                     buffer.append(toString((short[]) element));
                  } else if (entry == int[].class) {
                     buffer.append(toString((int[]) element));
                  } else if (entry == long[].class) {
                     buffer.append(toString((long[]) element));
                  } else if (entry == char[].class) {
                     buffer.append(toString((char[]) element));
                  } else if (entry == float[].class) {
                     buffer.append(toString((float[]) element));
                  } else if (entry == double[].class) {
                     buffer.append(toString((double[]) element));
                  } else if (entry == boolean[].class) {
                     buffer.append(toString((boolean[]) element));
                  } else { // element is an array of object references
                     if (done.contains(element)) {
                        buffer.append("[...]");
                     } else {
                        buffer.append(toString((Object[]) element, done));
                     }
                  }
               } else { 
                  ProxyWrapper wrapper = context.getWrapper();
                  
                  if(Proxy.class.isInstance(element)) {
                     element = wrapper.fromProxy(element);
                  }
                  if(Instance.class.isInstance(element)) {
                     Instance instance = (Instance)element;
                     Type type = instance.getType();
                     buffer.append(type);
                  } else {
                     buffer.append(element);
                  }
               }
            }
         }
         buffer.append(']');
         done.remove(array);
      }
      return buffer.toString();
   }
}
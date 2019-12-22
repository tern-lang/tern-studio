package org.ternlang.studio.common.json.document;

public abstract class Name {

   protected int hash;

   @Override
   public int hashCode() {
      CharSequence token = toText();
      int length = token.length();
      int local = hash;

      if (local == 0 && length > 0) {
         for (int i = 0; i < length; i++) {
            local = 31 * local + token.charAt(i);
         }
         hash = local;
      }
      return local;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      }
      if (other == this) {
         return true;
      }
      if (other instanceof Name) {
         Name name = (Name) other;
         CharSequence actual = toText();
         CharSequence required = name.toText();

         if (actual.length() != required.length()) {
            return false;
         }
         for (int i = 0; i < actual.length(); i++) {
            if (actual.charAt(i) != required.charAt(i)) {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   @Override
   public String toString() {
      return toText().toString();
   }

   public abstract CharSequence toText();
   public abstract boolean isEmpty();
}
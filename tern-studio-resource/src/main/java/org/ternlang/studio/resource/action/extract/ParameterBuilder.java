package org.ternlang.studio.resource.action.extract;

import org.ternlang.studio.resource.action.Context;

public class ParameterBuilder {

   private final StringConverter converter;
   private final Extractor[] extractors;
   private final Parameter[] parameters;

   public ParameterBuilder() {
      this(new Extractor[] {}, new Parameter[] {});
   }

   public ParameterBuilder(Extractor[] extractors, Parameter[] parameters) {
      this.converter = new StringConverter();
      this.extractors = extractors;
      this.parameters = parameters;
   }

   public Object[] extract(Context context) throws Exception {
      Object[] arguments = new Object[extractors.length];

      for (int i = 0; i < extractors.length; i++) {
         Extractor extractor = extractors[i];
         Parameter parameter = parameters[i];

         if (extractor != null) {
            arguments[i] = extractor.extract(parameter, context);
         }
         if (arguments[i] == null) {
            Class type = parameter.getType();

            if (type.isPrimitive()) {
               arguments[i] = converter.box(type);
            }
         }
      }
      return arguments;
   }

   public float score(Context context) throws Exception {
      float score = 0f;

      for (int i = 0; i < extractors.length; i++) {
         Extractor extractor = extractors[i];
         Parameter parameter = parameters[i];

         if (extractor != null) {
            Object result = extractor.extract(parameter, context);

            if (result == null) {
               if (parameter.isRequired()) {
                  return -1;
               }
            } else {
               score++;
            }
         }
      }
      return adjust(score);
   }

   private float adjust(float score) throws Exception {
      float adjustment = parameters.length / 1000.0f;
      float result = score + 1;

      if (result < 1) {
         return result;
      }
      return result - adjustment;
   }

}

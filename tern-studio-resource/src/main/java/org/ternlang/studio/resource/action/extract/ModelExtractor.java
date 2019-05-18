package org.ternlang.studio.resource.action.extract;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.Model;

public class ModelExtractor implements Extractor<Model> {

   @Override
   public Model extract(Parameter parameter, Context context) {
      Model model = context.getModel();
      Class type = parameter.getType();

      if (type == Model.class) {
         return model;
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();

      if (type == Model.class) {
         return true;
      }
      return false;
   }
}

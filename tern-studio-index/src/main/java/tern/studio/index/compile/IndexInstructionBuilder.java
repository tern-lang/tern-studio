package tern.studio.index.compile;

import java.util.concurrent.Executor;

import tern.compile.assemble.OperationBuilder;
import tern.core.Context;
import tern.core.NoStatement;
import tern.core.type.Type;
import tern.parse.Line;
import tern.studio.index.Index;
import tern.studio.index.IndexListener;

public class IndexInstructionBuilder extends OperationBuilder {

   private final IndexListener listener;
   
   public IndexInstructionBuilder(IndexListener listener, Context context, Executor executor) {
      super(context, executor);
      this.listener = listener;
   }

   @Override
   public Object create(Type type, Object[] arguments, Line line) throws Exception {
      Object result = super.create(type, arguments, line);
      
      if(Index.class.isInstance(result)) {
         Index index = (Index)result;
         Object operation = index.getOperation();
         
         listener.update(index);
         return operation;
      }
      if(Index[].class.isInstance(result)) {
         Index[] indexes = (Index[])result;

         for(Index index : indexes) {
            listener.update(index);
         }
         if(indexes.length > 0) {
            return indexes[0].getOperation();
         }
         return new NoStatement();
      }
      return result;
   }
}

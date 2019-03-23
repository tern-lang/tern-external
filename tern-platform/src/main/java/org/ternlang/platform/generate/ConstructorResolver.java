package org.ternlang.platform.generate;

import static org.ternlang.core.Reserved.TYPE_CONSTRUCTOR;

import java.lang.reflect.Constructor;

import org.ternlang.core.function.ArgumentConverter;
import org.ternlang.core.function.Function;
import org.ternlang.core.function.Signature;
import org.ternlang.core.function.index.FunctionIndexer;
import org.ternlang.core.function.index.FunctionPointer;
import org.ternlang.core.type.Type;

public class ConstructorResolver {
   
   private final FunctionIndexer indexer;
   
   public ConstructorResolver(FunctionIndexer indexer) {
      this.indexer = indexer;
   }

   public ConstructorData resolve(Type type, Object... arguments) {
      try {
         FunctionPointer pointer = indexer.index(type, TYPE_CONSTRUCTOR, arguments);
         Function function = pointer.getFunction();
         Signature signature = function.getSignature();
         ArgumentConverter converter = signature.getConverter();
         Constructor constructor = (Constructor)signature.getSource();
         Object[] list = converter.convert(arguments);
         Class[] types = constructor.getParameterTypes();

         return new ConstructorData(types, list);
      } catch (Exception e) {
         throw new IllegalStateException("Could not match constructor for '" + type + "'", e);
      }
   }
}
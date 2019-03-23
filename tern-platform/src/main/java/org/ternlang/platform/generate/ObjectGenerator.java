package org.ternlang.platform.generate;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import org.ternlang.core.Context;
import org.ternlang.core.function.index.FunctionIndexer;
import org.ternlang.core.module.Module;
import org.ternlang.core.platform.Bridge;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.core.type.TypeLoader;

public class ObjectGenerator {
   
   private final ConstructorResolver resolver;
   private final ClassGenerator generator;
   
   public ObjectGenerator(ClassGenerator generator, FunctionIndexer indexer) {
      this.resolver = new ConstructorResolver(indexer);
      this.generator = generator;
   }

   public BridgeHolder generate(Type real, Class base, Object... arguments) throws Exception {
      BridgeConstructor builder = create(real, base, arguments);
      return new BridgeHolder(builder);
   }
   
   private BridgeConstructor create(Type real, Class base, Object... arguments) throws Exception {
      Class proxy = generator.generate(real, base);
      return new BridgeConstructor(real, proxy, base, arguments);
   }
   
   private class BridgeConstructor implements Callable<Bridge> {
      
      private final Object[] arguments;
      private final Class proxy;
      private final Class base;
      private final Type type;
      
      public BridgeConstructor(Type type, Class proxy, Class base, Object... arguments) {
         this.arguments = arguments;
         this.proxy = proxy;
         this.base = base;
         this.type = type;
      }

      @Override
      public Bridge call() throws Exception {
         Scope scope = type.getScope();
         Module module = scope.getModule();
         Context context = module.getContext();
         TypeLoader loader = context.getLoader();
         Type match = loader.loadType(base);
         ConstructorData data = resolver.resolve(match, arguments);
         Object[] converted = data.getArguments();
         Class[] types = data.getTypes();
         Constructor factory = proxy.getDeclaredConstructor(types);

         return (Bridge)factory.newInstance(converted);
      }
   }
}
package tern.platform.generate;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import tern.core.Context;
import tern.core.function.index.FunctionIndexer;
import tern.core.module.Module;
import tern.core.platform.Bridge;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.core.type.TypeLoader;

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
         ConstructorArguments data = resolver.resolve(scope, match, arguments);
         Object[] converted = data.getArguments();
         Class[] types = data.getTypes();
         Constructor factory = proxy.getDeclaredConstructor(types);
         
         return (Bridge)factory.newInstance(converted);
      }
   }
}
package tern.platform.generate;

import tern.core.convert.proxy.ProxyWrapper;
import tern.core.function.Invocation;
import tern.core.function.index.FunctionIndexer;
import tern.core.scope.Scope;
import tern.core.type.Type;

public class BridgeConstructorBuilder {

   private final BridgeInstanceConverter converter;
   private final BridgeInstanceBuilder builder;
   private final ThreadLocal local;
   
   public BridgeConstructorBuilder(ClassGenerator generator, FunctionIndexer indexer, ProxyWrapper wrapper, ThreadLocal local) {
      this.builder = new BridgeInstanceBuilder(generator, indexer);
      this.converter = new BridgeInstanceConverter(wrapper);
      this.local = local;
   }
   
   public Invocation createSuperConstructor(Type real, Type base) {
      return new BridgeInvocation(real, base);
   }
   
   private class BridgeInvocation implements Invocation {
      
      private final Type real;
      private final Type base;
      
      public BridgeInvocation(Type real, Type base) {
         this.real = real;
         this.base = base;
      }

      @Override
      public Object invoke(Scope scope, Object object, Object... list) throws Exception {
         BridgeInstance instance = builder.createInstance(real, base, list);

         try{
            converter.convert(instance);
            local.set(instance);
            instance.getBridge().setInstance(instance);
         } finally {
            local.set(null);
         }
         return instance;
      }
   }
}
package tern.platform.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import tern.core.convert.proxy.ProxyWrapper;
import tern.core.function.Invocation;
import tern.core.function.index.FunctionIndexer;
import tern.core.platform.Platform;
import tern.core.type.Type;
import tern.platform.InvocationRouter;
import tern.platform.generate.BridgeConstructorBuilder;
import tern.platform.generate.BridgeInstance;

public class StandardPlatform implements Platform {
   
   private final MethodInvocationResolver resolver;
   private final MethodInterceptorHandler handler;
   private final BridgeConstructorBuilder builder;
   private final EnhancerGenerator generator;
   private final InvocationRouter router;
   private final ThreadLocal local;

   public StandardPlatform(FunctionIndexer indexer, ProxyWrapper wrapper) {
      this.router = new InvocationRouter(this, indexer);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new MethodInterceptorHandler(local, router);
      this.generator = new EnhancerGenerator(handler);
      this.builder = new BridgeConstructorBuilder(generator, indexer, wrapper, local);
      this.resolver = new MethodInvocationResolver();
   }

   @Override
   public Invocation createSuperConstructor(Type real, Type base) {
      try {
         return builder.createSuperConstructor(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + real + "'", e);
      } 
   }

   @Override
   public Invocation createSuperMethod(Type real, Method method) {
      try {
         return resolver.resolveSuperMethod(real, method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation createMethod(Type real, Method method) {
      try {
         return resolver.resolveMethod(method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation createConstructor(Type real, Constructor constructor) {
      try {
         return resolver.resolveConstructor(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + constructor + "'", e);
      }
   }
}
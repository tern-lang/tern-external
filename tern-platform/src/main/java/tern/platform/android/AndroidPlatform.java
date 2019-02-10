package tern.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import tern.core.convert.proxy.ProxyWrapper;
import tern.core.function.Invocation;
import tern.core.function.index.FunctionIndexer;
import tern.core.platform.Platform;
import tern.core.type.Type;
import tern.platform.InvocationRouter;
import tern.platform.ThreadLocalHandler;
import tern.platform.generate.BridgeConstructorBuilder;
import tern.platform.generate.BridgeInstance;

public class AndroidPlatform implements Platform {
  
   private final BridgeConstructorBuilder builder;
   private final ProxyInvocationResolver resolver;
   private final ProxyClassGenerator generator; 
   private final InvocationHandler handler;
   private final InvocationRouter router;
   private final ProxyClassLoader loader;
   private final ThreadLocal local;

   public AndroidPlatform(FunctionIndexer indexer, ProxyWrapper wrapper) {
      this.router = new InvocationRouter(this, indexer);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new ThreadLocalHandler(local, router);
      this.loader = new ProxyClassLoader(handler);
      this.generator = new ProxyClassGenerator(loader);
      this.builder = new BridgeConstructorBuilder(generator, indexer, wrapper, local);
      this.resolver = new ProxyInvocationResolver(loader);
   }

   @Override
   public Invocation createSuperConstructor(Type real, Type base) {
      try {
         return builder.createSuperConstructor(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + base + "'", e);
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
         throw new IllegalStateException("Could not create adapter for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation createConstructor(Type real, Constructor constructor) {
      try {
         return resolver.resolveConstructor(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + constructor + "'", e);
      }
   }
}
package tern.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import tern.common.thread.ThreadPool;
import tern.core.function.Invocation;

public class ProxyInvocationBuilder {
   
   private final ProxyAdapterBuilder generator;
   private final Executor executor;

   public ProxyInvocationBuilder(ProxyClassLoader generator) {
      this.generator = new ProxyAdapterBuilder(generator);
      this.executor = new ThreadPool(1);
   }
   
   public Invocation createSuperMethod(Method method) {
      return new ProxyMethodSuperInvocation(method);
   }
   
   public Invocation createMethod(Method method) {
      return new ProxyMethodInvocation(generator, method, executor);
   }
   
   public Invocation createConstructor(Constructor constructor) {
      return new ProxyConstructorInvocation(generator, constructor, executor);
   }
}
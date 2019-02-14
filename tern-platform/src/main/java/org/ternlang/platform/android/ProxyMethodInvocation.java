package org.ternlang.platform.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.scope.Scope;
import org.ternlang.dx.stock.ProxyAdapter;

public class ProxyMethodInvocation implements Invocation {
   
   private volatile MethodExchanger exchanger;
   private volatile ProxyAdapter reference;
   private volatile Executor executor;
   private volatile Method method;
   
   public ProxyMethodInvocation(ProxyAdapterBuilder generator, Method method, Executor executor) {
      this.exchanger = new MethodExchanger(generator, method);
      this.reference = new MethodAdapter(method);
      this.executor = executor;
      this.method = method;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) throws Exception {
      try {
         return reference.invoke(value, arguments);
      }catch(InvocationTargetException cause) {
         Throwable target = cause.getTargetException();
         
         if(target != null) {
            throw new InternalStateException("Error occured invoking " + method, target);
         }
         throw cause;
      }catch(InternalError cause) {
         Throwable target = cause.getCause();
         
         if(target != null) {
            throw new InternalStateException("Error occured invoking " + method, target);
         }
         throw cause;
      }
   }
   
   private class MethodAdapter implements ProxyAdapter {

      private final AtomicLong counter;
      private final Method method;
      
      public MethodAdapter(Method method) {
         this(method, 10);
      }
      
      public MethodAdapter(Method method, int threshold) {
         this.counter = new AtomicLong(threshold);
         this.method = method;
      }
      
      @Override
      public Object invoke(Object object, Object... list) throws Exception {
         long count = counter.getAndDecrement();
         
         if(count == 0) {
            executor.execute(exchanger); // generate proxy adapter
         }
         return method.invoke(object, list);
      }   
   }
   
   private class MethodExchanger implements Runnable {
      
      private final ProxyAdapterBuilder generator;
      private final InternalClassFilter filter;
      private final Method method;
      
      public MethodExchanger(ProxyAdapterBuilder generator, Method method) {
         this.filter = new InternalClassFilter();
         this.generator = generator;
         this.method = method;
      }

      @Override
      public void run() {
         if(filter.accept(method)) { // in a private dex class loader
            ProxyAdapter adapter = generator.generate(method);
            
            if(adapter != null) {
               reference = adapter;
            }
         }
      }
   }
}
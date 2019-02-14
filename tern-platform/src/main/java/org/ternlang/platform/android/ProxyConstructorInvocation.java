package org.ternlang.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.scope.Scope;
import org.ternlang.dx.stock.ProxyAdapter;

public class ProxyConstructorInvocation implements Invocation {
   
   private volatile ConstructorExchanger exchanger;
   private volatile ProxyAdapter reference;
   private volatile Constructor constructor;
   private volatile Executor executor;
   
   public ProxyConstructorInvocation(ProxyAdapterBuilder generator, Constructor constructor, Executor executor) {
      this.exchanger = new ConstructorExchanger(generator, constructor);
      this.reference = new ConstructorAdapter(constructor);
      this.constructor = constructor;
      this.executor = executor;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) throws Exception {
      try {
         return reference.invoke(value, arguments);
      }catch(InvocationTargetException cause) {
         Throwable target = cause.getTargetException();
         
         if(target != null) {
            throw new InternalStateException("Error occured invoking " + constructor, target);
         }
         throw cause;
      }catch(InternalError cause) {
         Throwable target = cause.getCause();
         
         if(target != null) {
            throw new InternalStateException("Error occured invoking " + constructor, target);
         }
         throw cause;
      }
   }
   
   private class ConstructorAdapter implements ProxyAdapter {

      private final Constructor constructor;
      private final AtomicLong counter;
      
      public ConstructorAdapter(Constructor constructor) {
         this(constructor, 10);
      }
      
      public ConstructorAdapter(Constructor constructor, int threshold) {
         this.counter = new AtomicLong(threshold);
         this.constructor = constructor;
      }
      
      @Override
      public Object invoke(Object object, Object... list) throws Exception {
         long count = counter.getAndDecrement();
         
         if(count == 0) {
            executor.execute(exchanger); // generate proxy adapter
         }
         return constructor.newInstance(list);
      }   
   }
   
   private class ConstructorExchanger implements Runnable {
      
      private final ProxyAdapterBuilder generator;
      private final InternalClassFilter filter;
      private final Constructor constructor;
      
      public ConstructorExchanger(ProxyAdapterBuilder generator, Constructor constructor) {
         this.filter = new InternalClassFilter();
         this.constructor = constructor;
         this.generator = generator;
      }

      @Override
      public void run() {
         if(filter.accept(constructor)) { // in a private dex class loader
            ProxyAdapter adapter = generator.generate(constructor);
         
            if(adapter != null) {
               reference = adapter;
            }
         }
      }
   }
}
package org.ternlang.platform.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.scope.Scope;

public class DelegateConstructorInvocation implements Invocation {
   
   private final Constructor constructor;
   
   public DelegateConstructorInvocation(Constructor constructor) {
      this.constructor = constructor;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) throws Exception {
      try {
         return constructor.newInstance(arguments);
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
}
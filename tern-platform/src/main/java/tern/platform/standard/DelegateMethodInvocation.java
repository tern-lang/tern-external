package tern.platform.standard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tern.core.error.InternalStateException;
import tern.core.function.Invocation;
import tern.core.scope.Scope;

public class DelegateMethodInvocation implements Invocation {
   
   private final Method method;
   
   public DelegateMethodInvocation(Method method) {
      this.method = method;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) throws Exception {
      try {
         return method.invoke(value, arguments);
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
}
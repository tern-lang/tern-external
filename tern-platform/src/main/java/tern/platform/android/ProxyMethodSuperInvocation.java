package tern.platform.android;

import java.lang.reflect.Method;

import tern.core.error.InternalStateException;
import tern.core.function.Invocation;
import tern.core.scope.Scope;
import tern.dx.stock.ProxyBuilder;

public class ProxyMethodSuperInvocation implements Invocation {
   
   private final Method method;
   
   public ProxyMethodSuperInvocation(Method method) {
      this.method = method;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         return ProxyBuilder.callSuper(value, method, arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke " + method, e);
      }
   }
}
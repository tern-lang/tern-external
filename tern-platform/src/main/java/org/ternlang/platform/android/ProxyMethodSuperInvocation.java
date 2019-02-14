package org.ternlang.platform.android;

import java.lang.reflect.Method;

import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.scope.Scope;
import org.ternlang.dx.stock.ProxyBuilder;

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
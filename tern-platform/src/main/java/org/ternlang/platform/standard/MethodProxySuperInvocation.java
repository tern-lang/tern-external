package org.ternlang.platform.standard;

import org.ternlang.cglib.core.Signature;
import org.ternlang.cglib.proxy.MethodProxy;
import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;
import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.scope.Scope;

public class MethodProxySuperInvocation implements Invocation {
   
   private final Cache<Class, MethodProxy> cache;
   private final Signature signature;
   
   public MethodProxySuperInvocation(Signature signature) {
      this.cache = new CopyOnWriteCache<Class, MethodProxy>();
      this.signature = signature;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         Class type = value.getClass();
         MethodProxy proxy = cache.fetch(type);
         
         if(proxy == null) {
            proxy = MethodProxy.find(type, signature);
            cache.cache(type, proxy);
         }
         return proxy.invokeSuper(value, arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke " + signature, e);
      }
   }
}
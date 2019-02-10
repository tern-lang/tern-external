package tern.platform.standard;

import tern.cglib.core.Signature;
import tern.cglib.proxy.MethodProxy;
import tern.common.Cache;
import tern.common.CopyOnWriteCache;
import tern.core.error.InternalStateException;
import tern.core.function.Invocation;
import tern.core.scope.Scope;

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
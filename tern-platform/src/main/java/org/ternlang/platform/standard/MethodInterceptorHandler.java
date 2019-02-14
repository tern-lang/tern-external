package org.ternlang.platform.standard;

import java.lang.reflect.Method;

import org.ternlang.cglib.proxy.MethodInterceptor;
import org.ternlang.cglib.proxy.MethodProxy;
import org.ternlang.platform.InvocationRouter;
import org.ternlang.platform.ThreadLocalHandler;
import org.ternlang.platform.generate.BridgeInstance;

public class MethodInterceptorHandler extends ThreadLocalHandler implements MethodInterceptor  {
   
   public MethodInterceptorHandler(ThreadLocal<BridgeInstance> local, InvocationRouter handler) {
      super(local, handler);
   }

   @Override
   public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
      return invoke(object, method, list);
   }
}
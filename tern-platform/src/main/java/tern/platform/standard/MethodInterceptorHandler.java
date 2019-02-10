package tern.platform.standard;

import java.lang.reflect.Method;

import tern.cglib.proxy.MethodInterceptor;
import tern.cglib.proxy.MethodProxy;
import tern.platform.InvocationRouter;
import tern.platform.ThreadLocalHandler;
import tern.platform.generate.BridgeInstance;

public class MethodInterceptorHandler extends ThreadLocalHandler implements MethodInterceptor  {
   
   public MethodInterceptorHandler(ThreadLocal<BridgeInstance> local, InvocationRouter handler) {
      super(local, handler);
   }

   @Override
   public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
      return invoke(object, method, list);
   }
}
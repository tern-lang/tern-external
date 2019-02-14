package org.ternlang.platform;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.ternlang.core.platform.Bridge;
import org.ternlang.platform.generate.BridgeInstance;

public class ThreadLocalHandler implements InvocationHandler {
   
   private final ThreadLocal<BridgeInstance> local;
   private final InvocationRouter router;
   
   public ThreadLocalHandler(ThreadLocal<BridgeInstance> local, InvocationRouter router) {
      this.router = router;
      this.local = local;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] list) throws Throwable {
      Bridge bridge = (Bridge)proxy;
      BridgeInstance instance = (BridgeInstance)bridge.getInstance();
      
      if(instance == null) {
         instance = local.get(); // thread local is set first
      
         if(instance == null) {
            throw new IllegalStateException("Object has not been constructed");
         }
         bridge.setInstance(instance); // now we knot its set
      }
      instance.getHolder().setBridge(bridge);
      return router.route(bridge, method, list);
   }
   
}
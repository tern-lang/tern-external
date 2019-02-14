package org.ternlang.platform;

import java.lang.reflect.Method;

import org.ternlang.core.Context;
import org.ternlang.core.convert.proxy.ProxyWrapper;
import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.function.index.FunctionIndexer;
import org.ternlang.core.function.index.FunctionPointer;
import org.ternlang.core.function.resolve.FunctionCall;
import org.ternlang.core.function.resolve.FunctionResolver;
import org.ternlang.core.module.Module;
import org.ternlang.core.platform.Bridge;
import org.ternlang.core.platform.Platform;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeBinder;
import org.ternlang.core.scope.instance.Instance;
import org.ternlang.core.type.Type;

public class InvocationRouter {

   private final MethodComparator comparator;
   private final FunctionIndexer indexer;
   private final ScopeBinder binder;
   private final Platform builder;
   
   public InvocationRouter(Platform builder, FunctionIndexer indexer) {
      this.comparator = new MethodComparator();
      this.binder = new ScopeBinder();
      this.indexer = indexer;
      this.builder = builder;
   }
   
   public Object route(Bridge bridge, Method method, Object[] list) throws Throwable {
      Instance instance = (Instance)bridge.getInstance();
      Class owner = method.getDeclaringClass();
      Scope scope = binder.bind(instance, instance);
      
      if(owner != Bridge.class) {
         Invocation invocation = bind(bridge, instance, method, list);
         Object value = invocation.invoke(scope, bridge, list);
         Module module = scope.getModule();
         Context context = module.getContext();
         ProxyWrapper wrapper = context.getWrapper();
         Class returns = method.getReturnType();
         
         if(returns != void.class) {
            return wrapper.toProxy(value, returns);
         }
         return null;
      }
      return scope;
   }
   
   private Invocation bind(Bridge bridge, Instance instance, Method method, Object[] list) throws Throwable {
      String name = method.getName();
      Type type = instance.getType();
      Scope scope = binder.bind(instance, instance);
      FunctionPointer match = indexer.index(type, name, list); 
      
      if (comparator.isAbstract(match)) {
         throw new InternalStateException("No implementaton of " + method + " for '" + type + "'");
      }
      if (comparator.isEqual(match, method)) { // could be slow on android
         return builder.createSuperMethod(type, method);
      }
      Module module = scope.getModule();
      Context context = module.getContext();
      FunctionResolver resolver = context.getResolver();
      FunctionCall call = resolver.resolveInstance(scope, scope, name, list);
      
      if (call == null) {
         return builder.createSuperMethod(type, method);
      }
      return new CallableInvocation(call);
   
   }
   
   private static class CallableInvocation implements Invocation {

      private final FunctionCall call;
      
      public CallableInvocation(FunctionCall call) {
         this.call = call;
      }
      
      @Override
      public Object invoke(Scope scope, Object object, Object... list) throws Exception {
         return call.invoke(scope, scope, list);
      }
      
   }
}
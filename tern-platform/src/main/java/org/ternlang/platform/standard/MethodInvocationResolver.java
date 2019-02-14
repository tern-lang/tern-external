package org.ternlang.platform.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.ternlang.common.CopyOnWriteCache;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.type.Type;
import org.ternlang.platform.InvocationCache;
import org.ternlang.platform.InvocationCacheTable;

public class MethodInvocationResolver {

   private final InvocationCacheTable table;
   private final MethodProxyBuilder builder;

   public MethodInvocationResolver() {
      this.table = new InvocationCacheTable(CopyOnWriteCache.class);
      this.builder = new MethodProxyBuilder();
   }

   public Invocation resolveSuperMethod(Type real, Method method) {
      InvocationCache cache = table.get(real);
      Invocation invocation = cache.fetch(method);
      
      if(invocation == null) {
         invocation = builder.createSuperMethod(method);
         cache.cache(method, invocation);
      }
      return invocation;
   }
   
   public Invocation resolveMethod(Method method) {
      return new DelegateMethodInvocation(method);
   }
   
   public Invocation resolveConstructor(Constructor constructor) {
      return new DelegateConstructorInvocation(constructor);
   }
}
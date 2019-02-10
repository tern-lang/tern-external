package tern.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import tern.common.Cache;
import tern.common.IdentityCache;
import tern.core.function.Invocation;
import tern.core.type.Type;
import tern.platform.InvocationCache;
import tern.platform.InvocationCacheTable;

public class ProxyInvocationResolver {
   
   private final Cache<Object, Invocation> adapters;
   private final ProxyInvocationBuilder builder;
   private final InvocationCacheTable table;

   public ProxyInvocationResolver(ProxyClassLoader generator) {
      this.table = new InvocationCacheTable(IdentityCache.class);
      this.adapters = new IdentityCache<Object, Invocation>();
      this.builder = new ProxyInvocationBuilder(generator);
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
      Invocation invocation = adapters.fetch(method);
      
      if (invocation == null) {
         invocation = builder.createMethod(method);
         adapters.cache(method, invocation);
      }
      return invocation;
   }
   
   public Invocation resolveConstructor(Constructor constructor) {
      Invocation invocation = adapters.fetch(constructor);
      
      if (invocation == null) {
         invocation = builder.createConstructor(constructor);
         adapters.cache(constructor, invocation);
      }
      return invocation;
   }
}
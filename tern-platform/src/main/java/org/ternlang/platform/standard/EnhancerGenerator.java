package org.ternlang.platform.standard;

import org.ternlang.cglib.proxy.Callback;
import org.ternlang.cglib.proxy.Enhancer;
import org.ternlang.cglib.proxy.MethodInterceptor;
import org.ternlang.core.Any;
import org.ternlang.core.ContextClassLoader;
import org.ternlang.core.EntityCache;
import org.ternlang.core.convert.InterfaceCollector;
import org.ternlang.core.platform.Bridge;
import org.ternlang.core.type.Type;
import org.ternlang.platform.generate.ClassGenerator;

public class EnhancerGenerator implements ClassGenerator{

   private final InterfaceCollector collector;
   private final EntityCache<Class> cache;
   private final Callback[] interceptors;
   private final ClassLoader loader;
   
   public EnhancerGenerator(MethodInterceptor interceptor) {
      this.interceptors = new Callback[] {interceptor};
      this.loader = new ContextClassLoader(Any.class);
      this.collector = new InterfaceCollector();
      this.cache = new EntityCache<Class>();      
   }
   
   @Override
   public Class generate(Type type, Class base) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         proxy = create(type, base);
         Enhancer.registerCallbacks(proxy, interceptors);
         cache.cache(type, proxy);
      }
      return proxy;
   }
   
   private Class create(Type type, Class base) {
      Class[] interfaces = collector.collect(type);
      
      try {
         Enhancer enhancer = new Enhancer();
         Class[] types = new Class[] {MethodInterceptor.class};
         
         enhancer.setClassLoader(loader);
         enhancer.setSuperclass(base);
         enhancer.setInterceptDuringConstruction(true);
         enhancer.setBeanInterfaces(Bridge.class);
         enhancer.setInterfaces(interfaces); // ensure we can convert from object to Instance
         enhancer.setCallbackTypes(types);
         
         return enhancer.createClass();
      } catch(Exception e) {
         throw new IllegalStateException("Could not create proxy for " + type, e);
      }
   }
}
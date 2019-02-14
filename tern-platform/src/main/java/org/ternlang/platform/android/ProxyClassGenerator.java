package org.ternlang.platform.android;

import org.ternlang.core.EntityCache;
import org.ternlang.core.type.Type;
import org.ternlang.core.convert.InterfaceCollector;
import org.ternlang.platform.generate.ClassGenerator;

public class ProxyClassGenerator implements ClassGenerator{

   private final ProxyClassLoader generator;
   private final InterfaceCollector collector;
   private final EntityCache<Class> cache;
   
   public ProxyClassGenerator(ProxyClassLoader generator) {
      this.collector = new InterfaceCollector();
      this.cache = new EntityCache<Class>();
      this.generator = generator;
   }
   
   @Override
   public Class generate(Type type, Class base) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         Class[] interfaces = collector.collect(type);
         
         proxy = generator.loadClass(base, interfaces);
         cache.cache(type, proxy);
      }
      return proxy;
   }
}
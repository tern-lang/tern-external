package org.ternlang.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.ternlang.dx.stock.ProxyAdapter;

public class ProxyAdapterBuilder {
   
   private final ProxyClassLoader loader;
   
   public ProxyAdapterBuilder(ProxyClassLoader loader) {
      this.loader = loader;
   }

   public ProxyAdapter generate(Method method) {
      Class adapter = loader.loadClass(method);
      
      try {
         if(adapter != null) {
            return (ProxyAdapter)adapter.newInstance();
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not create adapter " + adapter, e);
      }
      return null;
   }
   
   public ProxyAdapter generate(Constructor constructor) {
      Class adapter = loader.loadClass(constructor);
      
      try {
         if(adapter != null) {
            return (ProxyAdapter)adapter.newInstance();
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not create adapter " + adapter, e);
      }
      return null;
   }
}
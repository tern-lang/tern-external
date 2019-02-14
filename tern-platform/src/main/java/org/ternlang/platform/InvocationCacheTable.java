package org.ternlang.platform;

import org.ternlang.common.Cache;
import org.ternlang.core.type.Type;

public class InvocationCacheTable<T> {

   private volatile InvocationCacheArray array;
   private volatile CacheAllocator allocator;
   
   public InvocationCacheTable(Class<? extends Cache> internal) {
      this(internal, 32);
   }
   
   public InvocationCacheTable(Class<? extends Cache> internal, int capacity) {
      this.array = new InvocationCacheArray(capacity, capacity);
      this.allocator = new CacheAllocator(internal);
   }
   
   public InvocationCache get(Type type) {
      int index = type.getOrder(); // index 0 will cluster anonymous types
      int length = array.length();
      
      if(index >= length) {
         return allocator.allocate(type);
      }
      InvocationCache cache = array.get(index);
      
      if(cache == null) {
         return allocator.allocate(type);
      }
      return cache;
   }
   
   private class CacheAllocator {
      
      private final Class<? extends Cache> internal;
      
      public CacheAllocator(Class<? extends Cache> internal) {
         this.internal = internal;
      }
      
      public synchronized InvocationCache allocate(Type type) {
         int index = type.getOrder();
         InvocationCacheArray local = array.copy(index);
         InvocationCache cache = local.get(index);
         
         if(cache == null) {
            cache = new InvocationCache(internal);
            local.set(index, cache);
            array = local;
         }
         return cache;
      }
   }
}
package org.ternlang.platform;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class InvocationCacheArray {
   
   private final AtomicReferenceArray<InvocationCache> array;
   private final int capacity;
   private final int expand;
   
   public InvocationCacheArray(int capacity, int expand) {
      this.array = new AtomicReferenceArray<InvocationCache>(capacity);
      this.capacity = capacity;
      this.expand = expand;
   }
   
   public InvocationCacheArray copy(int require) {
      int length = array.length();
      
      if(require >= length) {
         InvocationCacheArray copy = new InvocationCacheArray(require + expand, expand);
         
         for(int i = 0; i < length; i++) {
            InvocationCache cache = array.get(i);
            copy.set(i, cache);
         }
         return copy;
      }
      return this;
   }
   
   public InvocationCache get(int index) {
      return array.get(index);
   }
   
   public void set(int index, InvocationCache cache) {
      array.set(index, cache);
   }

   public int length(){
      return capacity;
   }
}
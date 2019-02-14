package org.ternlang.platform.generate;

import java.util.concurrent.Callable;

import org.ternlang.core.platform.Bridge;

public class BridgeHolder {

   private volatile Callable<Bridge> source;
   private volatile Bridge value;
   
   public BridgeHolder(Callable<Bridge> source) {
      this.source = source;
   }
   
   public Bridge getBridge() {
      if(value == null) {
         try {
            value = source.call();
         }catch(Exception e) {
            throw new IllegalStateException("Could not create instance", e);
         }
      }
      return value;
   }
   
   public void setBridge(Bridge value) {
      this.value = value;
   }
}
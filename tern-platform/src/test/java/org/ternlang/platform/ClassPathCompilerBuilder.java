package org.ternlang.platform;

import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.store.Store;
import org.ternlang.compile.Compiler;
import org.ternlang.compile.StoreContext;
import org.ternlang.compile.StringCompiler;
import org.ternlang.core.Context;

public class ClassPathCompilerBuilder {
   
   //private static final Executor EXECUTOR = Executors.newFixedThreadPool(3);

   public static Compiler createCompiler() {
      Store store = new ClassPathStore();
      Context context = new StoreContext(store, null);
      
      return new StringCompiler(context);
   }
}

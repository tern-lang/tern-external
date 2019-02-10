package tern.platform;

import tern.common.store.ClassPathStore;
import tern.common.store.Store;
import tern.compile.Compiler;
import tern.compile.StoreContext;
import tern.compile.StringCompiler;
import tern.core.Context;

public class ClassPathCompilerBuilder {
   
   //private static final Executor EXECUTOR = Executors.newFixedThreadPool(3);

   public static Compiler createCompiler() {
      Store store = new ClassPathStore();
      Context context = new StoreContext(store, null);
      
      return new StringCompiler(context);
   }
}

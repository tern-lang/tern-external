package org.ternlang.platform;

import java.io.File;
import java.util.concurrent.Executor;

import org.ternlang.common.store.FileStore;
import org.ternlang.common.store.Store;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.Compiler;
import org.ternlang.compile.Executable;
import org.ternlang.compile.ResourceCompiler;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;

public class RayTraceDebugger {

   // 27478
   // 27603
   // 27290
   // 25187
   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\misc\\src"),
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\misc\\assets")
      );
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      
      try {
         //Executable executable = compiler.compile("/ray_tracer.tern");
         Executable executable = compiler.compile("/ray_tracer_no_constraints.tern");
         executable.execute();
      } catch(Exception e){
         e.printStackTrace();
      }
   }
}

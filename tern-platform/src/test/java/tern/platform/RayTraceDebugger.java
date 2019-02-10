package tern.platform;

import java.io.File;
import java.util.concurrent.Executor;

import tern.common.store.FileStore;
import tern.common.store.Store;
import tern.common.thread.ThreadPool;
import tern.compile.Compiler;
import tern.compile.Executable;
import tern.compile.ResourceCompiler;
import tern.compile.StoreContext;
import tern.core.Context;

public class RayTraceDebugger {

   // 27478
   // 27603
   // 27290
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

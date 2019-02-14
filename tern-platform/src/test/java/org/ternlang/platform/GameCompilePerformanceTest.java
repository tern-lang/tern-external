package org.ternlang.platform;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

import org.ternlang.common.store.FileStore;
import org.ternlang.common.store.Store;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.Compiler;
import org.ternlang.compile.Executable;
import org.ternlang.compile.ResourceCompiler;
import org.ternlang.compile.StoreContext;
import org.ternlang.compile.verify.VerifyError;
import org.ternlang.compile.verify.VerifyException;
import org.ternlang.core.Context;
import org.ternlang.core.scope.EmptyModel;
import org.ternlang.core.scope.Model;

public class GameCompilePerformanceTest {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\games\\src"),
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\games\\assets")
      );
//      Store store = new FileStore(
//            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\games\\mario\\src"),
//            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\games\\mario\\assets")
//      );
      Executor executor = new ThreadPool(8);
      
      for(int i = 0; i < 10000; i++) {
         execute(store, executor);
      }
   }
   
   private static void execute(Store store, Executor executor) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         Context context = new StoreContext(store, executor);
         Compiler compiler = new ResourceCompiler(context);
         Model model = new EmptyModel();         
         Executable executable = compiler.compile("/mario/MarioGame.tern");
         executable.execute(model, true);
      } catch(VerifyException e){
         List<VerifyError> errors = e.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
         }
      } catch(Exception e){
         e.printStackTrace();
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish-start;
         System.err.println("Compile time was " + duration);
      }
   }
}

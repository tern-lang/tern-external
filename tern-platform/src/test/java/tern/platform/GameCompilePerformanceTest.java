package tern.platform;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

import tern.common.store.FileStore;
import tern.common.store.Store;
import tern.common.thread.ThreadPool;
import tern.compile.Compiler;
import tern.compile.Executable;
import tern.compile.ResourceCompiler;
import tern.compile.StoreContext;
import tern.compile.verify.VerifyError;
import tern.compile.verify.VerifyException;
import tern.core.Context;
import tern.core.scope.EmptyModel;
import tern.core.scope.Model;

public class GameCompilePerformanceTest {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\games\\src"),
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\games\\assets")
      );
//      Store store = new FileStore(
//            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\games\\mario\\src"),
//            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\games\\mario\\assets")
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

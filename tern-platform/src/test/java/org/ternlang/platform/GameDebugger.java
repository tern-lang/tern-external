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

public class GameDebugger {

   public static void main(String[] list) throws Exception {
//      File[] roots = new File[] {
//            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\games\\src"),
//            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\games\\assets")
//      };
    File[] roots = new File[] {
    new File("C:\\Work\\development\\tern-lang\\tern-demo\\games\\mario\\src"),
    new File("C:\\Work\\development\\tern-lang\\tern-demo\\games\\mario\\assets")
    };
      Store store = new FileStore(roots);
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);

      //compile(roots, executor);
      execute(compiler);
   }
   
   private static void compile(File[] roots, Executor executor) {
      try {
         for(int i = 0; i < 1000; i++) {
            Store store = new FileStore(roots);
            long start = System.currentTimeMillis();
            Context context = new StoreContext(store, executor);
            Compiler compiler = new ResourceCompiler(context);
            Model model = new EmptyModel();
            Executable executable = compiler.compile("/mario/MarioGame.tern");
            executable.execute(model, true);
            long finish = System.currentTimeMillis();
            System.out.println("COMPILE: " +(finish-start));
         }
      } catch(VerifyException e){
         List<VerifyError> errors = e.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
         }
      } catch(Exception e){
         e.printStackTrace();
      }
   }

   private static void execute(Compiler compiler) {
      try {
         Executable executable = compiler.compile("/mario/MarioGame.tern");
         executable.execute();
      } catch(VerifyException e){
         List<VerifyError> errors = e.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
         }
      } catch(Exception e){
         e.printStackTrace();
      }
   }
}

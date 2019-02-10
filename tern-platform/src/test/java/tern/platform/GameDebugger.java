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

public class GameDebugger {

   public static void main(String[] list) throws Exception {
//      File[] roots = new File[] {
//            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\games\\src"),
//            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\games\\assets")
//      };
    File[] roots = new File[] {
    new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\games\\mario\\src"),
    new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\games\\mario\\assets")
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

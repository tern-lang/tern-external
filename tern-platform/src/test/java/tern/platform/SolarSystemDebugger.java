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

public class SolarSystemDebugger {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\physics\\src"),
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\demo\\physics\\assets")
      );
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      
      //compile(store, executor);
      execute(compiler);
   }
   
   
   private static void compile(Store store, Executor executor) {
      try {
         for(int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            Context context = new StoreContext(store, executor);
            Compiler compiler = new ResourceCompiler(context);
            Model model = new EmptyModel();
            Executable executable = compiler.compile("/solarsystem/SolarSystem.snap");
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
         Executable executable = compiler.compile("/solarsystem/SolarSystem.snap");
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

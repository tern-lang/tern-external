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

public class SolarSystemDebugger {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\physics\\src"),
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\physics\\assets")
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
            Executable executable = compiler.compile("/solarsystem/SolarSystem.tern");
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
         Executable executable = compiler.compile("/solarsystem/SolarSystem.tern");
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

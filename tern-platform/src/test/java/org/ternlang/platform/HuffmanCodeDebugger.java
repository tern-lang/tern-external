package org.ternlang.platform;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.ternlang.common.store.FileStore;
import org.ternlang.common.store.Store;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.Compiler;
import org.ternlang.compile.Executable;
import org.ternlang.compile.ResourceCompiler;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.core.scope.MapModel;
import org.ternlang.core.scope.Model;

public class HuffmanCodeDebugger {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio\\work\\algorithms\\main\\src\\hard")
      );
      Map<String, Object> map = new HashMap<String, Object>();
      Model model = new MapModel(map);
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      
      map.put("args", Arrays.asList("C:/Users/Niall/.tern/log/ternd.log"));
      
      try {
         //Executable executable = compiler.compile("/ray_tracer.tern");
         Executable executable = compiler.compile("/huffman_tree.tern");
         executable.execute(model);
      } catch(Exception e){
         e.printStackTrace();
      }
   }
}

package org.ternlang.platform.generate;

import org.ternlang.core.function.index.FunctionIndexer;
import org.ternlang.core.module.Module;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;

public class BridgeInstanceBuilder {

   private final ObjectGenerator generator;
   
   public BridgeInstanceBuilder(ClassGenerator generator, FunctionIndexer indexer) {
      this.generator = new ObjectGenerator(generator, indexer);
   }
   
   public BridgeInstance createInstance(Type real, Type base, Object... arguments) throws Exception {
      Class require = base.getType();
      Scope outer = real.getScope();
      Module module = base.getModule();
      BridgeHolder holder = generator.generate(base, require, arguments);
      
      return new BridgeInstance(holder, module, outer, real, base);  
   }
}
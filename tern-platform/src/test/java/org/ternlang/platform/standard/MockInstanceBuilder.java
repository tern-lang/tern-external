package org.ternlang.platform.standard;

import static org.ternlang.core.ModifierType.CLASS;

import org.ternlang.core.Context;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.platform.Platform;
import org.ternlang.core.platform.PlatformProvider;
import org.ternlang.core.scope.instance.Instance;

public class MockInstanceBuilder {
   
   public static void createInstance(Context context) throws Exception {
      PlatformProvider provider = new PlatformProvider(context.getExtractor(), context.getWrapper());
      
      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(context.getLoader().defineType("foo", "Foo", CLASS.mask), context.getLoader().loadType("javax.swing.JPanel"));
      Instance instance = (Instance)invocation.invoke(null, null);

      
      if(!instance.getBridge().getClass().getSuperclass().getName().equals("javax.swing.JPanel")){
         throw new RuntimeException(instance.getBridge().getClass().getName());
      }
   }
}

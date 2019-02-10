package tern.platform.standard;

import static tern.core.ModifierType.CLASS;

import tern.core.Context;
import tern.core.function.Invocation;
import tern.core.platform.Platform;
import tern.core.platform.PlatformProvider;
import tern.core.scope.instance.Instance;

public class MockInstanceBuilder {
   
   public static void createInstance(Context context) throws Exception {
      PlatformProvider provider = new PlatformProvider(context.getExtractor(), context.getWrapper(), context.getStack());
      
      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(context.getLoader().defineType("foo", "Foo", CLASS.mask), context.getLoader().loadType("javax.swing.JPanel"));
      Instance instance = (Instance)invocation.invoke(null, null);

      
      if(!instance.getBridge().getClass().getSuperclass().getName().equals("javax.swing.JPanel")){
         throw new RuntimeException(instance.getBridge().getClass().getName());
      }
   }
}

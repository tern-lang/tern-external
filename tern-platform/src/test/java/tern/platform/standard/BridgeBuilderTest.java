package tern.platform.standard;

import static tern.core.ModifierType.CLASS;
import static tern.core.type.Phase.COMPILE;
import junit.framework.TestCase;

import tern.common.store.ClassPathStore;
import tern.compile.StoreContext;
import tern.core.Context;
import tern.core.constraint.Constraint;
import tern.core.function.Invocation;
import tern.core.platform.Platform;
import tern.core.platform.PlatformProvider;
import tern.core.scope.instance.Instance;
import tern.core.type.Type;

public class BridgeBuilderTest extends TestCase {
   
   public void testBridgeBuilder() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      
      createInstance(context);
      MockInstanceBuilder.createInstance(context);
   }
   
   public void createInstance(Context context) throws Exception {
      PlatformProvider provider = new PlatformProvider(context.getExtractor(), context.getWrapper(), context.getStack());
      
      Type type = context.getLoader().defineType("foo", "Foo", CLASS.mask);
      Type panel = context.getLoader().loadType("javax.swing.JPanel");
      
      type.getProgress().done(COMPILE);
      type.getTypes().add(Constraint.getConstraint(panel));

      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(type, panel);
      Instance instance = (Instance)invocation.invoke(null, null);
      
      assertEquals(instance.getBridge().getClass().getSuperclass().getName(), "javax.swing.JPanel");
   }

}

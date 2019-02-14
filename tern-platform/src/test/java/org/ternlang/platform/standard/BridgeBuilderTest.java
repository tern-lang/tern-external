package org.ternlang.platform.standard;

import static org.ternlang.core.ModifierType.CLASS;
import static org.ternlang.core.type.Phase.COMPILE;
import junit.framework.TestCase;

import org.ternlang.common.store.ClassPathStore;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.platform.Platform;
import org.ternlang.core.platform.PlatformProvider;
import org.ternlang.core.scope.instance.Instance;
import org.ternlang.core.type.Type;

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

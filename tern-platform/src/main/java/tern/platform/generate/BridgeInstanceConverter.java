package tern.platform.generate;

import java.util.List;

import tern.core.constraint.Constraint;
import tern.core.convert.proxy.ProxyWrapper;
import tern.core.property.Property;
import tern.core.scope.ScopeState;
import tern.core.type.Type;
import tern.core.variable.Value;

public class BridgeInstanceConverter {
   
   private final ProxyWrapper wrapper;
   
   public BridgeInstanceConverter(ProxyWrapper wrapper) {
      this.wrapper = wrapper;
   }

   public void convert(BridgeInstance instance) {
      Type base = instance.getBase(); // this might be the wrong type
      List<Constraint> types = base.getTypes();
      ScopeState state = instance.getState();
      
      update(instance, state, base);
      
      for(Constraint type : types) {
         Type match = type.getType(instance);
         
         if(match != null) {
            update(instance, state, match);
         }
      }
   }

   private void update(BridgeInstance instance, ScopeState state, Type type) {
      List<Property> properties = type.getProperties();      
      
      for(Property property : properties) {
         String name = property.getName();
         Object current = state.getValue(name);
         
         if(current == null) {
            Value value = new BridgeValue(instance, wrapper, property, name);
            state.addValue(name, value);
         }
      }
   }
}
package org.ternlang.platform.generate;

public class ConstructorData {

   private final Object[] arguments;
   private final Class[] types;

   public ConstructorData(Class[] types, Object[] arguments) {
      this.arguments = arguments;
      this.types = types;
   }

   public Object[] getArguments() {
      return arguments;
   }

   public Class[] getTypes() {
      return types;
   }
}
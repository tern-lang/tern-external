package tern.platform.generate;

public class ConstructorArguments {
   
   private final Object[] arguments;
   private final Class[] types;

   public ConstructorArguments(Class[] types, Object[] arguments) {
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
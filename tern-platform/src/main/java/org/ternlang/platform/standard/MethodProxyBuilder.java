package org.ternlang.platform.standard;

import java.lang.reflect.Method;

import org.ternlang.asm.Type;
import org.ternlang.cglib.core.Signature;
import org.ternlang.core.function.Invocation;

public class MethodProxyBuilder {
   
   private final Type[] empty;

   public MethodProxyBuilder() {
      this.empty = new Type[] {};
   }
   
   public Invocation createSuperMethod(Method method) {
      String name = method.getName();
      Class returns = method.getReturnType();
      Class[] parameters = method.getParameterTypes();
      Signature signature = createSignature(name, returns, parameters);
      
      return new MethodProxySuperInvocation(signature);
   }
   
   public Signature createSignature(String name, Class returns, Class[] parameters) {
      Type type = Type.getType(returns);
      
      if(parameters.length > 0) {
         Type[] types = new Type[parameters.length];
         
         for (int i = 0; i < parameters.length; i++) {
            types[i] = Type.getType(parameters[i]);
         }
         return new Signature(name, type, types);
      }
      return new Signature(name, type, empty);
   }
}
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ternlang.dx.stock;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PUBLIC;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ternlang.dx.Code;
import org.ternlang.dx.DexMaker;
import org.ternlang.dx.Local;
import org.ternlang.dx.MethodId;
import org.ternlang.dx.TypeId;

/*
 * This class is <b>not thread safe</b>.
 */
public final class ProxyAdapterBuilder<T> {
   // Version of ProxyBuilder. It should be updated if the implementation
   // of the generated proxy class changes.
   public static final int VERSION = 1;

   /*
    * A cache of all proxy classes ever generated. At the time of writing, Android's runtime doesn't 
    * support class unloading so there's little value in using weak references.
    */
   private static final Map<Class<?>, Class<?>> generatedAccessorClasses = Collections.synchronizedMap(new HashMap<Class<?>, Class<?>>());

   private final Class<T> baseClass;
   private ClassLoader parentClassLoader = ProxyBuilder.class.getClassLoader();
   private File dexCache;

   private ProxyAdapterBuilder(Class<T> clazz) {
      baseClass = clazz;
   }

   public static <T> ProxyAdapterBuilder<T> forClass(Class<T> clazz) {
      return new ProxyAdapterBuilder<T>(clazz);
   }

   /*
    * Specifies the parent ClassLoader to use when creating the proxy.
    *
    * <p>
    * If null, {@code ProxyBuilder.class.getClassLoader()} will be used.
    */
   public ProxyAdapterBuilder<T> parentClassLoader(ClassLoader parent) {
      parentClassLoader = parent;
      return this;
   }

   /*
    * Sets the directory where executable code is stored. See {@link DexMaker#generateAndLoad DexMaker.generateAndLoad()} 
    * for guidance on choosing a secure location for the dex cache.
    */
   public ProxyAdapterBuilder<T> dexCache(File dexCacheParent) {
      dexCache = new File(dexCacheParent, "v" + Integer.toString(VERSION));
      dexCache.mkdir();
      return this;
   }

   // TODO: test coverage for this

   public Class buildAccessor(Method method) throws Exception {
      Method accessibleMethod = getAccessibleMethod(method);
      
      if(accessibleMethod != null) {
         Class accessorClass = generatedAccessorClasses.get(accessibleMethod);
   
         if (accessorClass == null) {
            DexMaker dexMaker = new DexMaker();
            // the cache missed; generate the class
            String generatedName = getNameForAccessorOf(accessibleMethod);
            TypeId<? extends T> generatedType = TypeId.get("L" + generatedName + ";");
            TypeId interfaceType = TypeId.get(ProxyAdapter.class);
            generateConstructorsForAccessor(dexMaker, generatedType, TypeId.OBJECT); // generate default no arg
            generateCodeForAccessor(dexMaker, generatedType, accessibleMethod);
            dexMaker.declare(generatedType, generatedName + ".generated", PUBLIC | FINAL, TypeId.OBJECT, interfaceType);
            ClassLoader classLoader = dexMaker.generateAndLoad(parentClassLoader, dexCache, generatedName);
            try {
               accessorClass = loadClass(classLoader, generatedName);
            } catch (IllegalAccessError e) {
               // Thrown when the base class is not accessible.
               throw new UnsupportedOperationException("cannot proxy inaccessible class " + baseClass, e);
            } catch (ClassNotFoundException e) {
               // Should not be thrown, we're sure to have generated this class.
               throw new AssertionError(e);
            }
         }
         return accessorClass;
      }
      return null;
   }

   public Class buildAccessor(Constructor constructor) throws Exception {
      Constructor accessibleConstructor = getAccessibleConstructor(constructor);
      
      if(accessibleConstructor != null) {
         Class accessorClass = generatedAccessorClasses.get(accessibleConstructor);
   
         if (accessorClass == null) {
            DexMaker dexMaker = new DexMaker();
            // the cache missed; generate the class
            String generatedName = getNameForAccessorOf(accessibleConstructor);
            TypeId<? extends T> generatedType = TypeId.get("L" + generatedName + ";");
            TypeId interfaceType = TypeId.get(ProxyAdapter.class);
            generateConstructorsForAccessor(dexMaker, generatedType, TypeId.OBJECT); // generate default no arg
            generateCodeForAccessor(dexMaker, generatedType, accessibleConstructor);
            dexMaker.declare(generatedType, generatedName + ".generated", PUBLIC | FINAL, TypeId.OBJECT, interfaceType);
            ClassLoader classLoader = dexMaker.generateAndLoad(parentClassLoader, dexCache, generatedName);
            try {
               accessorClass = loadClass(classLoader, generatedName);
            } catch (IllegalAccessError e) {
               // Thrown when the base class is not accessible.
               throw new UnsupportedOperationException("cannot proxy inaccessible class " + baseClass, e);
            } catch (ClassNotFoundException e) {
               // Should not be thrown, we're sure to have generated this class.
               throw new AssertionError(e);
            }
         }
         return accessorClass;
      }
      return null;
   }

   // The type cast is safe: the generated type will extend the base class type.
   @SuppressWarnings("unchecked")
   private Class<? extends T> loadClass(ClassLoader classLoader, String generatedName) throws ClassNotFoundException {
      return (Class<? extends T>) classLoader.loadClass(generatedName);
   }

   private static <T, G extends T> void generateCodeForAccessor(DexMaker dexMaker, TypeId<G> generatedType, Method accessorMethod) {
      int modifiers = accessorMethod.getModifiers();
      String name = accessorMethod.getName();
      Class declaringClass = accessorMethod.getDeclaringClass();
      Class<?>[] argClasses = accessorMethod.getParameterTypes();
      Class<?> returnClass = accessorMethod.getReturnType();
      TypeId<Object[]> objectArrayType = TypeId.get(Object[].class);
      TypeId<Object> objectType = TypeId.get(Object.class);
      TypeId instanceType = TypeId.get(declaringClass);
      TypeId[] argTypes = new TypeId[argClasses.length];
      Local[] argumentHolders = new Local[argTypes.length];
      Local[] boxedParameterHolder = new Local[argTypes.length];
      Local[] realParameterHolders = new Local[argTypes.length];
      for (int i = 0; i < argTypes.length; ++i) {
         argTypes[i] = TypeId.get(argClasses[i]);
      }
      TypeId returnType = TypeId.get(returnClass);
      MethodId methodToGenerate = generatedType.getMethod(objectType, "invoke", objectType, objectArrayType); // public Object invoke(Object targetObject, Object[] argumentarray)
      MethodId methodToInvoke = instanceType.getMethod(returnType, name, argTypes);
      Code code = dexMaker.declare(methodToGenerate, PUBLIC | FINAL);

      /*
       * The code generated basically casts the object and the arguments and then 
       * invoke the type, we need to make sure this is public
       * 
       * // List.get(int): Object
       * 
       * public Object invoke(Object object, Object[] arguments) { 
       *    List instance; 
       *    Object ret; 
       *    Object result; 
       *    Object argument; 
       *    Integer boxed; 
       *    int real;
       * 
       *    argument = arguments[0]; 
       *    boxed = (Integer)argument; 
       *    real = boxed.intValue(); 
       *    instance = (List)object; 
       *    result = instance.get(real); 
       *    ret = (Object)result;
       * 
       *    return ret; 
       * }
       */

      Local temp = code.newLocal(TypeId.OBJECT); // Object ret
      Local targetObject = code.getParameter(0, TypeId.OBJECT);
      Local argumentArray = code.getParameter(1, objectArrayType);
      Local<Integer> intValue = code.newLocal(TypeId.INT); // declare int to index argumentValues
      Local returnValue = code.newLocal(TypeId.OBJECT); // Object ret
      Local resultHolder = null;
      Local instanceHolder = null;

      if (returnClass != void.class) {
         resultHolder = code.newLocal(returnType);
      }
      if (!Modifier.isStatic(modifiers)) {
         instanceHolder = code.newLocal(instanceType); // ObjectToInvoke target
      }
      for (int p = 0; p < argTypes.length; ++p) {
         argumentHolders[p] = code.newLocal(objectType); // Object element
         Class boxedType = PRIMITIVE_TO_BOXED.get(argClasses[p]);

         if (boxedType != null) {
            boxedParameterHolder[p] = code.newLocal(TypeId.get(boxedType));
            realParameterHolders[p] = code.newLocal(argTypes[p]); // the primitive type
         } else {
            boxedParameterHolder[p] = code.newLocal(argTypes[p]); // MyObject local
            realParameterHolders[p] = boxedParameterHolder[p];
         }
      }
      for (int p = 0; p < argTypes.length; ++p) {
         code.loadConstant(intValue, p); // i = 0
         code.aget(argumentHolders[p], argumentArray, intValue); // element = arguments[i]

         if (PRIMITIVE_TO_UNBOX_METHOD.containsKey(argClasses[p])) {
            code.cast(boxedParameterHolder[p], argumentHolders[p]); // x = (Integer)element
            MethodId unboxingMethodFor = getUnboxMethodForPrimitive(argClasses[p]);
            code.invokeVirtual(unboxingMethodFor, realParameterHolders[p], boxedParameterHolder[p]);
         } else {
            code.cast(realParameterHolders[p], argumentHolders[p]); // local = (MyObject)element;
         }
      }
      if (Modifier.isStatic(modifiers)) {
         code.invokeStatic(methodToInvoke, resultHolder, realParameterHolders);
      } else if (declaringClass.isInterface()) {
         code.cast(instanceHolder, targetObject); // target = (ObjectToInvoke)object
         code.invokeInterface(methodToInvoke, resultHolder, instanceHolder, realParameterHolders);
      } else {
         code.cast(instanceHolder, targetObject); // target = (ObjectToInvoke)object
         code.invokeVirtual(methodToInvoke, resultHolder, instanceHolder, realParameterHolders);
      }
      if (returnClass != void.class) {
         Local boxedIfNecessary = boxIfRequired(code, resultHolder, temp);
         code.cast(returnValue, boxedIfNecessary); // ret = (Object)result
      } else {
         code.loadConstant(returnValue, null); // return null
      }
      code.returnValue(returnValue); // return ret
   }

   private static <T, G extends T> void generateCodeForAccessor(DexMaker dexMaker, TypeId<G> generatedType, Constructor accessorConstructor) {
      Class declaringClass = accessorConstructor.getDeclaringClass();
      Class<?>[] argClasses = accessorConstructor.getParameterTypes();
      Class<?> returnClass = declaringClass;
      TypeId<Object[]> objectArrayType = TypeId.get(Object[].class);
      TypeId<Object> objectType = TypeId.get(Object.class);
      TypeId instanceType = TypeId.get(declaringClass);
      TypeId[] argTypes = new TypeId[argClasses.length];
      Local[] argumentHolders = new Local[argTypes.length];
      Local[] boxedParameterHolder = new Local[argTypes.length];
      Local[] realParameterHolders = new Local[argTypes.length];
      for (int i = 0; i < argTypes.length; ++i) {
         argTypes[i] = TypeId.get(argClasses[i]);
      }
      TypeId returnType = TypeId.get(returnClass);
      MethodId methodToGenerate = generatedType.getMethod(objectType, "invoke", objectType, objectArrayType); // public Object invoke(Object targetObject, Object[] argumentarray)
      MethodId constructorToInvoke = instanceType.getConstructor(argTypes);
      Code code = dexMaker.declare(methodToGenerate, PUBLIC);

      /*
       * The code generated basically casts the object and the arguments and then 
       * invoke the type, we need to make sure this is public
       * 
       * // new ArrayList(int): ArrayList
       * 
       * public Object invoke(Object ignore, Object[] arguments) { 
       *    List instance; 
       *    Object ret; 
       *    Object result; 
       *    Object argument; 
       *    Integer boxed; 
       *    int real;
       * 
       *    argument = arguments[0]; 
       *    boxed = (Integer)argument; 
       *    real = boxed.intValue();
       *    result = new ArrayList(real); 
       *    ret = (ArrayList)result;
       * 
       *    return ret; 
       * }
       */

      Local argumentArray = code.getParameter(1, objectArrayType);
      Local<Integer> intValue = code.newLocal(TypeId.INT); // declare int to index argumentValues
      Local returnValue = code.newLocal(TypeId.OBJECT); // Object ret
      Local resultHolder = code.newLocal(returnType);

      for (int p = 0; p < argTypes.length; ++p) {
         argumentHolders[p] = code.newLocal(objectType); // Object element
         Class boxedType = PRIMITIVE_TO_BOXED.get(argClasses[p]);

         if (boxedType != null) {
            boxedParameterHolder[p] = code.newLocal(TypeId.get(boxedType));
            realParameterHolders[p] = code.newLocal(argTypes[p]); // the primitive type
         } else {
            boxedParameterHolder[p] = code.newLocal(argTypes[p]); // MyObject local
            realParameterHolders[p] = boxedParameterHolder[p];
         }
      }
      for (int p = 0; p < argTypes.length; ++p) {
         code.loadConstant(intValue, p); // i = 0
         code.aget(argumentHolders[p], argumentArray, intValue); // element = arguments[i]

         if (PRIMITIVE_TO_UNBOX_METHOD.containsKey(argClasses[p])) {
            code.cast(boxedParameterHolder[p], argumentHolders[p]); // x = (Integer)element
            MethodId unboxingMethodFor = getUnboxMethodForPrimitive(argClasses[p]);
            code.invokeVirtual(unboxingMethodFor, realParameterHolders[p], boxedParameterHolder[p]);
         } else {
            code.cast(realParameterHolders[p], argumentHolders[p]); // local = (MyObject)element;
         }
      }
      code.newInstance(resultHolder, constructorToInvoke, realParameterHolders);
      code.cast(returnValue, resultHolder); // ret = (Object)result
      code.returnValue(returnValue); // return ret
   }

   private static Local<?> boxIfRequired(Code code, Local<?> parameter, Local<Object> temp) {
      MethodId<?, ?> unboxMethod = PRIMITIVE_TYPE_TO_UNBOX_METHOD.get(parameter.getType());
      if (unboxMethod == null) {
         return parameter;
      }
      code.invokeStatic(unboxMethod, temp, parameter);
      return temp;
   }

   private static <T, G extends T> void generateConstructorsForAccessor(DexMaker dexMaker, TypeId<G> generatedType, TypeId<T> superType) {
      MethodId<?, ?> method = generatedType.getConstructor();
      Code constructorCode = dexMaker.declare(method, PUBLIC); // declare constructor
      Local<G> thisRef = constructorCode.getThis(generatedType);
      constructorCode.invokeDirect(TypeId.OBJECT.getConstructor(), null, thisRef);
      constructorCode.returnVoid();
   }

   private static <T> String getNameForAccessorOf(Method method) {
      try {
         String name = method.getName();
         String type = method.getDeclaringClass().getSimpleName();
         String source = method.toString();
         byte[] data = source.getBytes();
         MessageDigest digest = MessageDigest.getInstance("MD5");
         byte[] octets = digest.digest(data);

         StringBuilder builder = new StringBuilder();
         builder.append(type);
         builder.append("_");
         builder.append(name);
         builder.append("_");

         for (int i = 0; i < octets.length; i++) {
            int value = (octets[i] & 0xff) + 0x100;
            String code = Integer.toString(value, 16);
            String token = code.substring(1);

            builder.append(token);
         }
         return builder.toString();
      } catch (Exception e) {
         throw new IllegalStateException("Unable to generate name for " + method, e);
      }
   }

   private static <T> String getNameForAccessorOf(Constructor constructor) {
      try {
         String type = constructor.getDeclaringClass().getSimpleName();
         String source = constructor.toString();
         byte[] data = source.getBytes();
         MessageDigest digest = MessageDigest.getInstance("MD5");
         byte[] octets = digest.digest(data);

         StringBuilder builder = new StringBuilder();
         builder.append(type);
         builder.append("_new_");

         for (int i = 0; i < octets.length; i++) {
            int value = (octets[i] & 0xff) + 0x100;
            String code = Integer.toString(value, 16);
            String token = code.substring(1);

            builder.append(token);
         }
         return builder.toString();
      } catch (Exception e) {
         throw new IllegalStateException("Unable to generate name for " + constructor, e);
      }
   }
   
   private static Constructor getAccessibleConstructor(Constructor constructor) {
      Class type = constructor.getDeclaringClass();
      int typeModifiers = type.getModifiers();
      int constructorModifiers = type.getModifiers();

      if(!Modifier.isPublic(constructorModifiers) || !Modifier.isPublic(typeModifiers)) {
         return null;
      }
      return constructor;
   }
   
   private static Method getAccessibleMethod(Method method) {
      Class type = method.getDeclaringClass();
      Class[] methodParameters = method.getParameterTypes();
      String methodName = method.getName();
      int typeModifiers = type.getModifiers();
      int methodModifiers = type.getModifiers();

      if(!Modifier.isPublic(methodModifiers)) {
         return null;
      }
      if(!Modifier.isPublic(typeModifiers)) {
         while(type != null) {
            Class[] interfaceTypes = type.getInterfaces();
            
            for(int i = 0; i < interfaceTypes.length; i++) {
               Class interfaceType = interfaceTypes[i];
               int interfaceModifiers = interfaceType.getModifiers();
               
               if(Modifier.isPublic(interfaceModifiers)) {
                  Method[] interfaceMethods = interfaceType.getDeclaredMethods();
               
                  for(int j = 0; j < interfaceMethods.length; j++){
                     Method interfaceMethod = interfaceMethods[j];
                     Class[] interfaceParameters = interfaceMethod.getParameterTypes();
                     String interfaceMethodName = interfaceMethod.getName();
                    
                     if(interfaceParameters.length == methodParameters.length) {
                        if(methodName.equals(interfaceMethodName)) {
                           if(isTypeArrayEqual(interfaceParameters, methodParameters)) {
                              return interfaceMethod;
                           }
                        }
                     }
                  }
               }
            }
            type = type.getSuperclass();
         }
         return null;
      }
      return method;
   }
   
   private static boolean isTypeArrayEqual(Class[] interfaceParameters, Class[] methodParameters) {
      if(interfaceParameters.length != methodParameters.length) {
         return false;
      }
      for(int i = 0; i < interfaceParameters.length; i++){
         Class interfaceParameter = interfaceParameters[i];
         Class methodParameter = methodParameters[i];
         
         if(interfaceParameter != methodParameter) {
            return false;
         }
      }
      return true;
   }

   private static MethodId<?, ?> getUnboxMethodForPrimitive(Class<?> methodReturnType) {
      return PRIMITIVE_TO_UNBOX_METHOD.get(methodReturnType);
   }

   private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED;
   static {
      PRIMITIVE_TO_BOXED = new HashMap<Class<?>, Class<?>>();
      PRIMITIVE_TO_BOXED.put(boolean.class, Boolean.class);
      PRIMITIVE_TO_BOXED.put(int.class, Integer.class);
      PRIMITIVE_TO_BOXED.put(byte.class, Byte.class);
      PRIMITIVE_TO_BOXED.put(long.class, Long.class);
      PRIMITIVE_TO_BOXED.put(short.class, Short.class);
      PRIMITIVE_TO_BOXED.put(float.class, Float.class);
      PRIMITIVE_TO_BOXED.put(double.class, Double.class);
      PRIMITIVE_TO_BOXED.put(char.class, Character.class);
   }

   private static final Map<TypeId<?>, MethodId<?, ?>> PRIMITIVE_TYPE_TO_UNBOX_METHOD;
   static {
      PRIMITIVE_TYPE_TO_UNBOX_METHOD = new HashMap<TypeId<?>, MethodId<?, ?>>();
      for (Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_TO_BOXED.entrySet()) {
         TypeId<?> primitiveType = TypeId.get(entry.getKey());
         TypeId<?> boxedType = TypeId.get(entry.getValue());
         MethodId<?, ?> valueOfMethod = boxedType.getMethod(boxedType, "valueOf", primitiveType);
         PRIMITIVE_TYPE_TO_UNBOX_METHOD.put(primitiveType, valueOfMethod);
      }
   }

   /*
    * Map from primitive type to method used to unbox a boxed version of the primitive.
    * <p>
    * This is required for methods whose return type is primitive, since the {@link InvocationHandler} will 
    * return us a boxed result, and we'll need to convert it back to primitive value.
    */
   private static final Map<Class<?>, MethodId<?, ?>> PRIMITIVE_TO_UNBOX_METHOD;
   static {
      Map<Class<?>, MethodId<?, ?>> map = new HashMap<Class<?>, MethodId<?, ?>>();
      map.put(boolean.class, TypeId.get(Boolean.class).getMethod(TypeId.BOOLEAN, "booleanValue"));
      map.put(int.class, TypeId.get(Integer.class).getMethod(TypeId.INT, "intValue"));
      map.put(byte.class, TypeId.get(Byte.class).getMethod(TypeId.BYTE, "byteValue"));
      map.put(long.class, TypeId.get(Long.class).getMethod(TypeId.LONG, "longValue"));
      map.put(short.class, TypeId.get(Short.class).getMethod(TypeId.SHORT, "shortValue"));
      map.put(float.class, TypeId.get(Float.class).getMethod(TypeId.FLOAT, "floatValue"));
      map.put(double.class, TypeId.get(Double.class).getMethod(TypeId.DOUBLE, "doubleValue"));
      map.put(char.class, TypeId.get(Character.class).getMethod(TypeId.CHAR, "charValue"));
      PRIMITIVE_TO_UNBOX_METHOD = map;
   }
}
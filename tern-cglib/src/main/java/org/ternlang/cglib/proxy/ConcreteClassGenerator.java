/*
 * Copyright 2003 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ternlang.cglib.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ternlang.asm.ClassVisitor;
import org.ternlang.asm.Type;
import org.ternlang.cglib.core.AbstractClassGenerator;
import org.ternlang.cglib.core.ClassEmitter;
import org.ternlang.cglib.core.CodeEmitter;
import org.ternlang.cglib.core.CollectionUtils;
import org.ternlang.cglib.core.Constants;
import org.ternlang.cglib.core.DuplicatesPredicate;
import org.ternlang.cglib.core.EmitUtils;
import org.ternlang.cglib.core.KeyFactory;
import org.ternlang.cglib.core.MethodInfo;
import org.ternlang.cglib.core.MethodInfoTransformer;
import org.ternlang.cglib.core.MethodWrapper;
import org.ternlang.cglib.core.Predicate;
import org.ternlang.cglib.core.ReflectUtils;
import org.ternlang.cglib.core.RejectModifierPredicate;
import org.ternlang.cglib.core.Signature;
import org.ternlang.cglib.core.Transformer;
import org.ternlang.cglib.core.TypeUtils;
import org.ternlang.cglib.core.VisibilityPredicate;

/*
 * @author Juozas Baliuka, Chris Nokleberg
 */
public class ConcreteClassGenerator extends AbstractClassGenerator
{
    private static final Source SOURCE = new Source(ConcreteClassGenerator.class.getName());
    private static final ConcreteClassGeneratorKey KEY_FACTORY =
      (ConcreteClassGeneratorKey)KeyFactory.create(ConcreteClassGeneratorKey.class);
    
    interface ConcreteClassGeneratorKey {
        public Object newInstance(String superclass, String[] interfaces);
    }

    private Class[] interfaces;
    private Class superclass;
    private boolean classOnly;

    public ConcreteClassGenerator() {
        super(SOURCE);
    }

    /*
     * Set the class which the generated class will extend. The class
     * must not be declared as final, and must have a non-private
     * no-argument constructor.
     * @param superclass class to extend, or null to extend Object
     */
    public void setSuperclass(Class superclass) {
        if (superclass != null && superclass.equals(Object.class)) {
            superclass = null;
        }
        this.superclass = superclass;
    }
    
    /*
     * Set the interfaces to implement. The <code>Factory</code> interface will
     * always be implemented regardless of what is specified here.
     * @param interfaces array of interfaces to implement, or null
     * @see Factory
     */
    public void setInterfaces(Class... interfaces) {
        this.interfaces = interfaces;
    }

    protected ClassLoader getDefaultClassLoader() {
        if (superclass != null) {
            return superclass.getClassLoader();
        } else {
            return null;
        }
    }

    protected ProtectionDomain getProtectionDomain() {
        return ReflectUtils.getProtectionDomain(superclass);
    }

    public Class createClass() {
        classOnly = true;
        return createHelper();
    }

    private Class createHelper() {
        if (superclass != null) {
            setNamePrefix(superclass.getName());
        }
        String superName = (superclass != null) ? superclass.getName() : "java.lang.Object";
        Object key = KEY_FACTORY.newInstance(superName, ReflectUtils.getNames(interfaces));
        return (Class)super.create(key);
    }

    public void generateClass(ClassVisitor v) throws Exception {
       Class sc = (superclass == null) ? Object.class : superclass;

       if (TypeUtils.isFinal(sc.getModifiers()))
           throw new IllegalArgumentException("Cannot subclass final class " + sc.getName());
       List constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
       Enhancer.filterConstructors(sc, constructors);
       
       // Order is very important: must add superclass, then
       // its superclass chain, then each interface and
       // its superinterfaces.
       List actualMethods = new ArrayList();
       List interfaceMethods = new ArrayList();
       final Set forcePublic = new HashSet();
       getMethods(sc, interfaces, actualMethods, interfaceMethods, forcePublic);
       
       List methods = CollectionUtils.transform(actualMethods, new Transformer() {
          public Object transform(Object value) {
              Method method = (Method)value;
              int modifiers = Constants.ACC_FINAL
                  | (method.getModifiers()
                     & ~Constants.ACC_ABSTRACT
                     & ~Constants.ACC_NATIVE
                     & ~Constants.ACC_SYNCHRONIZED);
              if (forcePublic.contains(MethodWrapper.create(method))) {
                  modifiers = (modifiers & ~Constants.ACC_PROTECTED) | Constants.ACC_PUBLIC;
              }
              return ReflectUtils.getMethodInfo(method, modifiers);
          }
       });
       ClassEmitter ce = new ClassEmitter(v);
       ce.begin_class(Constants.V1_2,
             Constants.ACC_PUBLIC,
             getClassName(),
             Type.getType(sc),
             TypeUtils.getTypes(interfaces),
             Constants.SOURCE_FILE);
       List constructorInfo = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
       emitConstructors(ce, constructorInfo);
       Iterator iterator = methods.iterator();
       
       while(iterator.hasNext()) {
          MethodInfo methodInfo = (MethodInfo)iterator.next();
          CodeEmitter emitter = EmitUtils.begin_method(ce, methodInfo);
          emitter.throw_exception(Type.getType(UnsupportedOperationException.class), "Method not implemented");
          emitter.end_method();
       }
       ce.end_class();
    }
    
    private void emitConstructors(ClassEmitter ce, List constructors) {
       boolean seenNull = false;
       for (Iterator it = constructors.iterator(); it.hasNext();) {
           MethodInfo constructor = (MethodInfo)it.next();
           CodeEmitter e = EmitUtils.begin_method(ce, constructor, Constants.ACC_PUBLIC);
           e.load_this();
           e.dup();
           e.load_args();
           Signature sig = constructor.getSignature();
           seenNull = seenNull || sig.getDescriptor().equals("()V");
           e.super_invoke_constructor(sig);
           e.return_value();
           e.end_method();
       }
   }
    
    public static void getMethods(Class superclass, Class[] interfaces, List methods, List interfaceMethods, Set forcePublic)
    {
        ReflectUtils.addAllMethods(superclass, methods);
        List target = (interfaceMethods != null) ? interfaceMethods : methods;
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i] != Factory.class) {
                    ReflectUtils.addAllMethods(interfaces[i], target);
                }
            }
        }
        if (interfaceMethods != null) {
            if (forcePublic != null) {
                forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
            }
            methods.addAll(interfaceMethods);
        }
        CollectionUtils.filter(methods, new RejectModifierPredicate(Constants.ACC_STATIC));
        CollectionUtils.filter(methods, new VisibilityPredicate(superclass, true));
        CollectionUtils.filter(methods, new DuplicatesPredicate());
        CollectionUtils.filter(methods, new RejectModifierPredicate(Constants.ACC_FINAL));
        CollectionUtils.filter(methods, new Predicate() {
           public boolean evaluate(Object name) {
               Method method = (Method)name;
               int modifiers = method.getModifiers();
               return Modifier.isAbstract(modifiers);
           }
        });
    }

    protected Object firstInstance(Class type) {
        if (classOnly) {
            return type;
        } else {
            return ReflectUtils.newInstance(type);
        }
    }

    protected Object nextInstance(Object instance) {
        Class protoclass = (instance instanceof Class) ? (Class)instance : instance.getClass();
        if (classOnly) {
            return protoclass;
        } else {
            return ReflectUtils.newInstance(protoclass);
        }
    }
}
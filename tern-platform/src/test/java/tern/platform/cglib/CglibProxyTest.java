package tern.platform.cglib;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import tern.cglib.proxy.Enhancer;
import tern.cglib.proxy.MethodInterceptor;
import tern.cglib.proxy.MethodProxy;
import tern.core.Any;
import tern.core.ContextClassLoader;

public class CglibProxyTest extends TestCase {

   public static abstract class SampleClass {

      public String execute(String value) {
         return implementMe(value);
      }

      public abstract String implementMe(String value);
   }

   public void testMethodInterceptor() throws Exception {
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(SampleClass.class);
      enhancer.setCallback(new MethodInterceptor() {
         @Override
         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class && method.getName().equals("implementMe")) {
               return "value=" + args[0];
            }
            return proxy.invokeSuper(obj, args);
         }
      });
      SampleClass proxy = (SampleClass) enhancer.create();
      assertEquals("value=foo", proxy.execute("foo"));
      proxy.hashCode(); // Does not throw an exception or result in an endless loop.
   }

   public void testJPanel() throws Exception {
      ClassLoader loader = new ContextClassLoader(Any.class);
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(loader.loadClass("javax.swing.JPanel"));
      enhancer.setClassLoader(Any.class.getClassLoader());
      enhancer.setCallback(new MethodInterceptor() {
         @Override
         public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class && method.getName().equals("implementMe")) {
               return "value=" + args[0];
            }
            return proxy.invokeSuper(obj, args);
         }
      });
      Object proxy = enhancer.create();
      System.err.println(proxy);
   }
}

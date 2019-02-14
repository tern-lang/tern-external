package org.ternlang.platform;

import org.ternlang.compile.Compiler;
import org.ternlang.compile.Executable;

import junit.framework.TestCase;

public class SuperConstructorCallsBaseMethodTest extends TestCase {
   
   private static final String SOURCE =
   "import org.ternlang.platform.SuperConstructorCallsBaseMethodTest.SomeSuperCaller;\n"+
   "import org.ternlang.platform.SuperConstructorCallsBaseMethodTest.SomeResult;\n"+
   "\n"+
   "class FirstResult extends SomeResult{\n"+
   "   new(x):super(x){}\n"+
   "   public override getVal(): Integer{\n"+
   "      return 1;\n"+
   "   }\n"+
   "}\n"+
   "class SecondResult extends SomeResult{\n"+
   "   new(x):super(x){}\n"+
   "   public override getVal(): Integer{\n"+
   "      return 2;\n"+
   "   }\n"+
   "}\n"+   
   "class ThirdResult extends SomeResult{\n"+
   "   new(x):super(x){}\n"+
   "   public override getVal(): Integer{\n"+
   "      return 3;\n"+
   "   }\n"+ 
   "}\n"+      
   "class SomeBase extends SomeSuperCaller with Runnable {\n"+
   "   new(a,b) : super(a, b){\n"+
   "   }\n"+
   "\n"+
   "   public override getSomething(): SomeResult{\n"+
   "      return new FirstResult(this);\n"+
   "   }\n"+
   "}\n"+
   "var s = new SomeBase(1, 2);\n"+
   "s.dump();\n"+
   "var r = new SecondResult(s);\n"+
   "s.setSomething(r);\n"+
   "s.setSomething(new ThirdResult(s));";
   
   public abstract static class SomeSuperCaller {
      
      protected SomeResult result;
      protected int x;
      protected int y;
      
      protected SomeSuperCaller(int x, int y) {
         this.x = x;
         this.y = y;
         this.result = getSomething();
      }
      
      public void run(){
         System.err.println("SomeSuperCaller.run()");
      }
      
      public void dump() {
         result.doIt();
      }
      
      public void setSomething(SomeResult result) {
         this.result = result;
      }
      
      public abstract SomeResult getSomething();
   }
   
   public static abstract class SomeResult {
      
      private final SomeSuperCaller caller;
      
      public SomeResult(SomeSuperCaller caller) {
         this.caller = caller;
      }
      
      public void doIt(){
         System.err.println("x="+caller.x + " y="+caller.y);
      }
      
      public abstract int getVal();
   }

   public void testSuperCallToBaseMethod() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE);
      Executable executable = compiler.compile(SOURCE);
      executable.execute();
   }
}

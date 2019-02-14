package org.ternlang.platform;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.ternlang.compile.Compiler;
import org.ternlang.compile.Executable;

public class OverrideOverloadTest extends TestCase {

   public static class Foo {
   
      public void onMessage(String s){}
      public void onMessage(ByteBuffer b){}
   }
   
   private static final String SOURCE =
   "import org.ternlang.platform.OverrideOverloadTest.Foo;\n"+
   "\n"+
   "class Bar extends Foo {\n"+
   "   override onMessage(s:String){}\n"+
   "}\n"+
   "var b = new Bar();\n"+
   "b.onMessage('message');\n";

   public void testOverrideOverload() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE);
      Executable executable = compiler.compile(SOURCE);
      executable.execute();
   }
}

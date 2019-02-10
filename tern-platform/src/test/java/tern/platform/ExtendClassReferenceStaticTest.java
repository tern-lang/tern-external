package tern.platform;

import junit.framework.TestCase;

import tern.compile.Compiler;
import tern.compile.Executable;

public class ExtendClassReferenceStaticTest extends TestCase {

   private static final String SOURCE_1 =
   "import swing.JFrame;\n"+
   "class GameFrame extends JFrame {\n"+
   "   public static const BLAH = \"ss\";\n"+
   "   public static var playbackFormat: Integer=3411;\n"+
   "   new(a,b){\n"+
   "     println(BLAH + ':' + a + ':' + playbackFormat);\n"+
   "   }\n"+
   "\n"+
   "   dump(){\n"+
   "      println(\"a=${a} b=${b}\");\n"+
   "   }\n"+
   "}\n"+
   "var y = new GameFrame(1, 2);\n"+
   "println(y);\n";
   
   public void testConstructorStaticReference() throws Exception{
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_1);
      Executable executable = compiler.compile(SOURCE_1);
      executable.execute();
   }
         
}

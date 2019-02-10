package tern.platform;

import java.io.OutputStream;

import tern.compile.Compiler;
import tern.compile.Executable;

import junit.framework.TestCase;

public class BridgeExtractTest extends TestCase {

   private static final String SOURCE =
   "import tern.platform.BridgeExtractTest.OutputStreamHolder as Holder;\n"+
   "\n"+
   "class BlahOutputStream extends FilterOutputStream with Runnable {\n"+
   "   new(out) : super(out){\n"+
   "      println(\"${out}\");\n"+
   "   }\n"+
   "   override run(){\n"+
   "      println('BlahOutputStream.close()');\n"+
   "   }\n"+
   "   override toString(){\n"+
   "      return out.toString();\n"+
   "   }\n"+
   "}\n"+   
   "var buffer = new ByteArrayOutputStream();\n"+
   "var stream = new BlahOutputStream(buffer);\n"+
   "var holder = new Holder();\n"+
   "\n"+
   "holder.set(stream);\n" +
   "\n"+
   "var recovered = holder.get();\n"+
   "var bytes = 'hello world'.getBytes();\n"+
   "\n"+
   "recovered.write(bytes);\n"+
   "\n"+
   "var result = recovered.toString();\n"+
   "println(result);\n"+
   "assert result == 'hello world';\n";

   public static class OutputStreamHolder {
      
      private OutputStream stream;
      
      public void set(OutputStream stream){
         this.stream = stream;
      }
      
      public OutputStream get() {
         return this.stream;
      }
   }
   
   public void testExtendJavaClass() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE);
      Executable executable = compiler.compile(SOURCE);
      executable.execute();
   }
}

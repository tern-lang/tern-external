package tern.platform;

import tern.compile.Compiler;
import tern.compile.Executable;

import junit.framework.TestCase;

public class ExtendJavaClassTest extends TestCase {
   
   private static final String SOURCE_1 =
   "class FileClassLoader extends URLClassLoader {\n"+
   "   const x;\n"+
   "   new(x) : super([], String.class.type.getClassLoader()){\n"+
   "      this.x = x;\n"+
   "   }\n"+
   "   foo(){\n"+
   "      println(\"FileClassLoader.foo(${x})\");\n"+
   "   }\n"+
   "   override loadClass(name: String, resolve: Boolean): Class{\n"+
   "     println(\"FileClassLoader::loadClass(x=${x})\");\n"+
   "     //return Class.forName(name);\n"+
   "     return super.loadClass(name, resolve);\n"+
   "   }\n"+
   "}\n"+
   "var loader = new FileClassLoader(2211);\n"+
   "var result = loader.loadClass(String.class.type.getName());\n"+
   "loader.foo();\n"+
   "println(result);\n";
   
   private static final String SOURCE_2 =
   "class BlahOutputStream extends FilterOutputStream {\n"+
   "   new(out) : super(out){\n"+
   "      println(\"${out}\");\n"+
   "   }\n"+
   "}\n"+   
   "class MyOutputStream extends FilterOutputStream {\n"+
   "   new(out) : super(out){\n"+
   "      println(\"${out}\");\n"+
   "   }\n"+
   "   dump() {\n"+
   "      println(\"foo=${out}\");\n"+
   "   }\n"+
   "}\n"+
   "var stream = new MyOutputStream(System.out);\n"+
   "var printer = new PrintStream(stream);\n"+
   "stream.dump();\n"+
   "printer.println(\"print 1\");\n"+
   "printer.println(\"print 2\");\n";   
         
   private static final String SOURCE_3 =
   "class Bag extends HashMap with Closeable{\n"+
   "   override close(){\n"+
   "      super.clear();\n"+
   "   }\n"+
   "}\n"+
   "var bag = new Bag();\n"+
   "bag.put('a', 'A');\n"+
   "bag.put('b', 'B');\n"+
   "assert bag.get('a') == 'A';\n";
   
   private static final String SOURCE_4 =
   "class BufferStream extends OutputStream {\n"+
   "   const buffer: ByteArrayOutputStream;\n"+
   "   new() {\n"+
   "      this.buffer = new ByteArrayOutputStream();\n"+
   "   }\n"+
   "   override write(octet: Integer){\n"+
   "      write([octet]);\n"+
   "   }\n"+
   "   override write(array: Byte[], off: Integer, size: Integer) {\n"+
   "      buffer.write(array, off, size);\n"+
   "   }\n"+
   "   override flush() {\n"+
   "      buffer.flush();\n"+
   "   }\n"+
   "   override close() {\n"+
   "      buffer.close();\n"+
   "   }\n"+
   "   override toString() {\n"+
   "      return buffer.toString();\n"+
   "   }\n"+
   "}\n"+
   "\n"+
   "var buffer = new BufferStream();\n"+
   "\n"+
   "buffer.write(10);\n"+   
   "buffer.write('hello world'.getBytes());\n"+
   "buffer.write(10);\n"+
   "buffer.write('next'.getBytes());\n"+
   "\n"+
   "var text = buffer.toString();\n"+
   "\n"+
   "println(text);\n"+
   "\n"+
   "assert text == '\nhello world\nnext';\n";  
   
   private static final String SOURCE_5 =
   "import swing.JPanel;\n"+
   "import awt.Graphics;\n"+
   "class UpdatablePanel extends JPanel {\n"+
   "   const foo;\n"+
   "   new(foo){\n"+
   "      this.foo = foo;\n"+
   "   }\n"+
   "   override update(g: Graphics) {\n"+
   "      super.update(g);\n"+
   "   }\n"+
   "   override paint(g: Graphics) {\n"+
   "      super.paint(g);\n"+
   "   }\n"+
   "}\n"+
   "var panel = new UpdatablePanel('str');\n"+
   "println(panel);\n";
         
   public void testExtendJavaClass() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_1);
      Executable executable = compiler.compile(SOURCE_1);
      executable.execute();
   }
   
   public void testReferenceSuperField() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_2);
      Executable executable = compiler.compile(SOURCE_2);
      executable.execute();
   }
   
   public void testExtendWithDefaultSuperConstructor() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_3);
      Executable executable = compiler.compile(SOURCE_3);
      executable.execute();
   }
   
   public void testExtendWithTypeConstraints() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_4);
      Executable executable = compiler.compile(SOURCE_4);
      executable.execute();
   }
   
   public void testExtendSwingPanel() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_5);
      Executable executable = compiler.compile(SOURCE_5);
      executable.execute();
   }
}

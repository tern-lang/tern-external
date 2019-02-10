package tern.platform;

import java.io.File;

import junit.framework.TestCase;

import tern.common.store.FileStore;
import tern.common.store.Store;
import tern.compile.Compiler;
import tern.compile.Executable;
import tern.compile.StoreContext;
import tern.compile.StringCompiler;
import tern.core.Context;

// this test can be disabled
public class TestSuiteTest extends TestCase {
   
   private static final String LOCATION_OF_TEST_SUITE = "../../tern-studio/tern-studio/work/test";
   
   private static final String PATTERN = ".*ModuleImportTest.*";
   private static final String SOURCE = 
   "import test.TestCaseRunner;\n"+   
   "const runner = new TestCaseRunner('%s');\n"+
   //"assert runner.runTests(name -> name.matches(`" + PATTERN + "`));\n";
   "assert runner.runTests();\n";
         
   public void testSuite() throws Exception {
      File file = new File(LOCATION_OF_TEST_SUITE);
      Store store = new FileStore(file);
      Context context = new StoreContext(store, null);
      assertTrue("Test suite directory not found: " +file.getCanonicalPath(), file.exists());
      Compiler compiler = new StringCompiler(context);
      String source = String.format(SOURCE, file.getCanonicalPath().replace(""+File.separatorChar,"\\"+File.separatorChar));
      System.err.println(source);
      Executable executable = compiler.compile(source);
      executable.execute();
   }
}

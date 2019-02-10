package tern.platform;

import junit.framework.TestCase;

import tern.compile.Compiler;
import tern.compile.Executable;
import tern.compile.verify.VerifyException;

public class ReferenceVariableInSubclassTest extends TestCase {
   
   private static final String SOURCE_1 =
   "import tern.platform.ReferenceVariableInSubclassTest.Screen;\n"+
   "import tern.platform.ReferenceVariableInSubclassTest.Game;\n"+
   "\n" +
   "class TetrisGame extends Game {\n"+
   "   public new(): super(1,2){}\n"+
   "   public getScreen(): Screen {\n"+
   "      return new TetrisScreen(this);\n"+
   "   }\n"+
   "   public showScore(){\n"+
   "      var tetrisScreen: TetrisScreen = screen as TetrisScreen;\n"+
   "      println(tetrisScreen.score);\n"+
   "   }\n"+
   "}\n"+
   "class TetrisScreen extends Screen{\n"+
   "   var score: Integer;\n"+
   "   public new(game) :super(game){\n"+
   "      this.score = 11;\n"+
   "   }\n"+
   "}\n"+
   "var g = new TetrisGame();\n"+
   "g.showScore();\n"+
   "assert g.screen.score == 11;";

   private static final String SOURCE_2 =
   "import tern.platform.ReferenceVariableInSubclassTest.Screen;\n"+
   "import tern.platform.ReferenceVariableInSubclassTest.Game;\n"+
   "\n" +
   "class TetrisGame extends Game {\n"+
   "   public new(): super(1,2){}\n"+
   "   public getScreen(): Screen {\n"+
   "      return new TetrisScreen(this);\n"+
   "   }\n"+
   "   public showScore(){\n"+
   "      println(screen.score);\n"+ // error here, as the field is Screen which has no 'score'
   "   }\n"+
   "}\n"+
   "class TetrisScreen extends Screen{\n"+
   "   var score: Integer;\n"+
   "   public new(game) :super(game){\n"+
   "      this.score = 11;\n"+
   "   }\n"+
   "}\n"+
   "var g = new TetrisGame();\n"+
   "g.showScore();\n"+
   "assert g.screen.score == 11;";
   
   public abstract static class Screen {
      protected Game game;
      public Screen(Game game) {
         this.game = game;
      }
      public void show(){}
   }
   public abstract static class Game {
      protected Screen screen;
      protected int x;
      protected int y;
      public Game(int x, int y){
         this.screen = getScreen();
         this.x=x;
         this.y=y;
      }
      public abstract Screen getScreen();
   }
   
   public void testSuperCallToBaseMethod() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_1);
      Executable executable = compiler.compile(SOURCE_1);
      executable.execute();
   }
   
   public void testSuperCallToBaseMethodCompileError() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      boolean failure = false;
      
      try {
         System.err.println(SOURCE_2);
         Executable executable = compiler.compile(SOURCE_2);
         executable.execute();
      } catch(VerifyException e) {
         failure=true;
         e.getErrors().get(0).getCause().printStackTrace();
      }
      assertTrue("Should be a compile error", failure);
   }
}

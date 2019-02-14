package org.ternlang.platform;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import junit.framework.TestCase;

import org.ternlang.compile.Compiler;
import org.ternlang.compile.Executable;
import org.ternlang.core.scope.MapModel;
import org.ternlang.core.scope.Model;

public class KeyAdapterTest extends TestCase {

   private static final String SOURCE_1 = 
   "import java.awt.event.KeyAdapter;\n"+
   "import java.awt.event.KeyEvent;\n"+
   "\n"+
   "class DelegateListener extends KeyAdapter {\n"+
   "\n"+
   "  private delegate;\n"+
   "\n"+
   "  public new(delegate) {\n"+
   "     this.delegate = delegate;\n"+
   "  }\n"+
   "\n"+
   "   public override keyReleased(e: KeyEvent) {\n"+
   "      delegate.keyReleased(e);\n"+
   "   }\n"+
   "\n"+
   "   public override keyPressed(e: KeyEvent) {\n"+
   "      delegate.keyPressed(e);\n"+
   "   }\n"+
   "\n"+
   "   public override keyTyped(e: KeyEvent) {\n"+
   "      delegate.keyTyped(e);\n"+
   "   }\n"+
   "}\n"+
   "callback.update(new DelegateListener(delegate));\n";
   
   private static final String SOURCE_2 = 
   "import java.awt.event.KeyAdapter;\n"+
   "import java.awt.event.KeyEvent;\n"+
   "\n"+
   "class DelegateListener extends KeyAdapter {\n"+
   "\n"+
   "  private delegate;\n"+
   "\n"+
   "  public new(delegate) {\n"+
   "     this.delegate = delegate;\n"+
   "  }\n"+
   "\n"+
   "   public override keyReleased(e) {\n"+
   "      delegate.keyReleased(e);\n"+
   "   }\n"+
   "\n"+
   "   public override keyPressed(e) {\n"+
   "      delegate.keyPressed(e);\n"+
   "   }\n"+
   "\n"+
   "   public override keyTyped(e) {\n"+
   "      delegate.keyTyped(e);\n"+
   "   }\n"+
   "}\n"+
   "callback.update(new DelegateListener(delegate));\n";
   
   public void testKeyAdapter() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_1);
      KeyListenerReceiver receiver = new KeyListenerReceiver();
      KeyListenerDelegate delegate = new KeyListenerDelegate();
      Map map = new HashMap();
      Model model = new MapModel(map);
      map.put("callback", receiver);
      map.put("delegate", delegate);
      Executable executable = compiler.compile(SOURCE_1);
      executable.execute(model);
      JPanel panel = new JPanel();
      KeyEvent event = new KeyEvent(panel, 0, 0L, 0, 0, 'a', 0);
      receiver.listener.keyPressed(event);
      assertEquals(delegate.pressed, event);
      assertNull(delegate.typed);
      assertNull(delegate.released);
      receiver.listener.keyReleased(event);
      assertEquals(delegate.pressed, event);
      assertNull(delegate.typed);
      assertEquals(delegate.released, event);
   }
   
   public void testKeyAdapterWithInvalidOverride() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_2);
      KeyListenerReceiver receiver = new KeyListenerReceiver();
      KeyListenerDelegate delegate = new KeyListenerDelegate();
      Map map = new HashMap();
      Model model = new MapModel(map);
      map.put("callback", receiver);
      map.put("delegate", delegate);
      Executable executable = compiler.compile(SOURCE_2);
      boolean failure = false;
      
      try {
        executable.execute(model);
      } catch(Exception e){
         failure = true;
         e.printStackTrace();
      }
      assertTrue("Should fail because override is not proper match", failure);
   }
   
   private static class KeyListenerDelegate implements KeyListener {

      KeyEvent typed;
      KeyEvent pressed;
      KeyEvent released;
      
      @Override
      public void keyTyped(KeyEvent e) {
         typed = e;
         
      }

      @Override
      public void keyPressed(KeyEvent e) {
         pressed = e;
      }

      @Override
      public void keyReleased(KeyEvent e) {
         released = e;
      }
      
   }
   private static class KeyListenerReceiver {
      private KeyListener listener;
      public void update(KeyListener listener){
         this.listener = listener;
      }
   }
         
}

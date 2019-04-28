package org.ternlang.platform.generate;

import org.ternlang.core.module.Module;
import org.ternlang.core.platform.Bridge;
import org.ternlang.core.scope.MapState;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.scope.index.ArrayTable;
import org.ternlang.core.scope.index.ScopeIndex;
import org.ternlang.core.scope.index.ScopeTable;
import org.ternlang.core.scope.index.StackIndex;
import org.ternlang.core.scope.instance.Instance;
import org.ternlang.core.stack.StackFrame;
import org.ternlang.core.stack.StackTrace;
import org.ternlang.core.type.Type;
import org.ternlang.core.variable.Reference;
import org.ternlang.core.variable.Value;

public class BridgeInstance implements Instance {

   private final BridgeHolder holder;
   private final StackFrame frame;
   private final ScopeIndex index;
   private final ScopeTable table;
   private final ScopeState state;
   private final Module module;
   private final Value self;
   private final Type real;
   private final Type base;

   public BridgeInstance(BridgeHolder holder, Module module, Scope scope, Type real, Type base) {
      this.frame = new StackFrame(scope);
      this.self = new Reference(this);
      this.state = new MapState(scope);
      this.index = new StackIndex(scope);
      this.table = new ArrayTable();
      this.holder = holder;
      this.module = module;
      this.real = real;
      this.base = base;
   }
   
   public BridgeHolder getHolder() {
      return holder;
   }
   
   @Override
   public Bridge getBridge() {
      return holder.getBridge();
   }
   
   @Override
   public StackTrace getStack() {
      return frame.getTrace();
   }
   
   @Override
   public Value getThis() {
      return self;
   }
   
   @Override
   public Object getProxy(){
      return null;
   }

   @Override
   public ScopeIndex getIndex() {
      return index;
   }
   
   @Override
   public ScopeTable getTable(){
      return table;
   }

   @Override
   public ScopeState getState() {
      return state;
   }
   
   @Override
   public Instance getChild() {
      return this;
   }

   @Override
   public Instance getParent() {
      return this;
   }

   @Override
   public Instance getSuper() {
      return this;
   }

   @Override
   public Type getType() {
      return real;
   }
   
   public Type getBase() {
      return base;
   }

   @Override
   public Module getModule() {
      return module;
   }

   @Override
   public Type getHandle() {
      return real;
   }
}
package tern.platform.generate;

import tern.core.module.Module;
import tern.core.platform.Bridge;
import tern.core.scope.MapState;
import tern.core.scope.Scope;
import tern.core.scope.ScopeState;
import tern.core.scope.index.ArrayTable;
import tern.core.scope.index.ScopeIndex;
import tern.core.scope.index.ScopeTable;
import tern.core.scope.index.StackIndex;
import tern.core.scope.instance.Instance;
import tern.core.type.Type;
import tern.core.variable.Reference;
import tern.core.variable.Value;

public class BridgeInstance implements Instance {

   private final BridgeHolder holder;
   private final ScopeIndex index;
   private final ScopeTable table;
   private final ScopeState state;
   private final Module module;
   private final Value self;
   private final Type real;
   private final Type base;

   public BridgeInstance(BridgeHolder holder, Module module, Scope scope, Type real, Type base) {
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
   public Instance getStack() {
      return this;
   }

   @Override
   public Instance getScope() {
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
package statemachine;

import StateMachineModule.Loop;
import java.util.Map;
import statemachine.LoopAspectLoopAspectProperties;

@SuppressWarnings("all")
public class LoopAspectLoopAspectContext {
  public final static LoopAspectLoopAspectContext INSTANCE = new LoopAspectLoopAspectContext();
  
  public static LoopAspectLoopAspectProperties getSelf(final Loop _self) {
    		if (!INSTANCE.map.containsKey(_self))
    			INSTANCE.map.put(_self, new statemachine.LoopAspectLoopAspectProperties());
    		return INSTANCE.map.get(_self);
  }
  
  private Map<Loop, LoopAspectLoopAspectProperties> map = new java.util.WeakHashMap<StateMachineModule.Loop, statemachine.LoopAspectLoopAspectProperties>();
  
  public Map<Loop, LoopAspectLoopAspectProperties> getMap() {
    return map;
  }
}

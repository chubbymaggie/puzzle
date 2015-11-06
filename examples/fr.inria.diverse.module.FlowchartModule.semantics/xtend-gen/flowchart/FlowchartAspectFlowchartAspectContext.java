package flowchart;

import FlowchartModule.Flowchart;
import flowchart.FlowchartAspectFlowchartAspectProperties;
import java.util.Map;

@SuppressWarnings("all")
public class FlowchartAspectFlowchartAspectContext {
  public final static FlowchartAspectFlowchartAspectContext INSTANCE = new FlowchartAspectFlowchartAspectContext();
  
  public static FlowchartAspectFlowchartAspectProperties getSelf(final Flowchart _self) {
    		if (!INSTANCE.map.containsKey(_self))
    			INSTANCE.map.put(_self, new flowchart.FlowchartAspectFlowchartAspectProperties());
    		return INSTANCE.map.get(_self);
  }
  
  private Map<Flowchart, FlowchartAspectFlowchartAspectProperties> map = new java.util.WeakHashMap<FlowchartModule.Flowchart, flowchart.FlowchartAspectFlowchartAspectProperties>();
  
  public Map<Flowchart, FlowchartAspectFlowchartAspectProperties> getMap() {
    return map;
  }
}

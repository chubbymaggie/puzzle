package flowchartpck;

import flowchartpck.ExpressionAspect;
import flowchartpck.Loop;
import flowchartpck.LoopAspectLoopAspectProperties;
import flowchartpck.Program;
import flowchartpck.ProgramAspect;
import flowchartpck.StatementAspect;
import fr.inria.diverse.k3.al.annotationprocessor.Aspect;
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod;
import java.util.Hashtable;

@Aspect(className = Loop.class)
@SuppressWarnings("all")
public class LoopAspect extends StatementAspect {
  @OverrideAspectMethod
  public static void eval(final Loop _self, final Hashtable<String, Object> context) {
    flowchartpck.LoopAspectLoopAspectProperties _self_ = flowchartpck.LoopAspectLoopAspectContext.getSelf(_self);
     if (_self instanceof flowchartpck.Loop){
     flowchartpck.LoopAspect._privk3_eval(_self_, (flowchartpck.Loop)_self,context);
    } else  { throw new IllegalArgumentException("Unhandled parameter types: " + java.util.Arrays.<Object>asList(_self).toString()); };
  }
  
  private static void super_eval(final Loop _self, final Hashtable<String, Object> context) {
    flowchartpck.StatementAspectStatementAspectProperties _self_ = flowchartpck.StatementAspectStatementAspectContext.getSelf(_self);
     flowchartpck.StatementAspect._privk3_eval(_self_, _self,context);
  }
  
  protected static void _privk3_eval(final LoopAspectLoopAspectProperties _self_, final Loop _self, final Hashtable<String, Object> context) {
    while ((((Boolean) ExpressionAspect.eval(_self.getGuard(), context))).booleanValue()) {
      Program _body = _self.getBody();
      ProgramAspect.eval(_body, context);
    }
  }
}

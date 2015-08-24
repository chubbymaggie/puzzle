package flowchart;

import flowchart.LiteralAspect;
import flowchart.StringLit;
import flowchart.StringLitAspectStringLitAspectProperties;
import fr.inria.diverse.k3.al.annotationprocessor.Aspect;
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod;
import java.util.Hashtable;

@Aspect(className = StringLit.class)
@SuppressWarnings("all")
public class StringLitAspect extends LiteralAspect {
  @OverrideAspectMethod
  public static Object eval(final StringLit _self, final Hashtable<String, Object> context) {
    flowchart.StringLitAspectStringLitAspectProperties _self_ = flowchart.StringLitAspectStringLitAspectContext.getSelf(_self);
    Object result = null;
     if (_self instanceof flowchart.StringLit){
    result = flowchart.StringLitAspect._privk3_eval(_self_, (flowchart.StringLit)_self,context);
    } else  if (_self instanceof flowchart.Literal){
    result = flowchart.LiteralAspect.eval((flowchart.Literal)_self,context);
    } else  if (_self instanceof flowchart.Expression){
    result = flowchart.ExpressionAspect.eval((flowchart.Expression)_self,context);
    } else  { throw new IllegalArgumentException("Unhandled parameter types: " + java.util.Arrays.<Object>asList(_self).toString()); };
    return (java.lang.Object)result;
  }
  
  private static Object super_eval(final StringLit _self, final Hashtable<String, Object> context) {
    flowchart.LiteralAspectLiteralAspectProperties _self_ = flowchart.LiteralAspectLiteralAspectContext.getSelf(_self);
    return  flowchart.LiteralAspect._privk3_eval(_self_, _self,context);
  }
  
  protected static Object _privk3_eval(final StringLitAspectStringLitAspectProperties _self_, final StringLit _self, final Hashtable<String, Object> context) {
    return _self.getValue();
  }
}

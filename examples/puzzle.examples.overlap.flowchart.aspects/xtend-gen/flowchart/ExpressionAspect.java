package flowchart;

import flowchart.ExpressionAspectExpressionAspectProperties;
import flowchartpck.Expression;
import fr.inria.diverse.k3.al.annotationprocessor.Aspect;
import java.util.Hashtable;

@Aspect(className = Expression.class)
@SuppressWarnings("all")
public class ExpressionAspect {
  public static Object eval(final Expression _self, final Hashtable<String, Object> context) {
    flowchart.ExpressionAspectExpressionAspectProperties _self_ = flowchart.ExpressionAspectExpressionAspectContext.getSelf(_self);
    Object result = null;
    result =_privk3_eval(_self_, _self,context);
    return (java.lang.Object)result;
  }
  
  protected static Object _privk3_eval(final ExpressionAspectExpressionAspectProperties _self_, final Expression _self, final Hashtable<String, Object> context) {
    return Integer.valueOf(0);
  }
}
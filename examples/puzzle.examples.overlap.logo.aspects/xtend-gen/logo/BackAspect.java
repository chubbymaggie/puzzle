package logo;

import fr.inria.diverse.k3.al.annotationprocessor.Aspect;
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod;
import kmLogo.Back;
import kmLogo.Expression;
import logo.BackAspectBackAspectProperties;
import logo.Context;
import logo.ExpressionAspect;
import logo.PrimitiveAspect;
import org.eclipse.xtext.xbase.lib.InputOutput;

@Aspect(className = Back.class)
@SuppressWarnings("all")
public class BackAspect extends PrimitiveAspect {
  @OverrideAspectMethod
  public static int eval(final Back _self, final Context context) {
    logo.BackAspectBackAspectProperties _self_ = logo.BackAspectBackAspectContext.getSelf(_self);
    Object result = null;
    result =_privk3_eval(_self_, _self,context);
    return (int)result;
  }
  
  private static int super_eval(final Back _self, final Context context) {
    logo.PrimitiveAspectPrimitiveAspectProperties _self_ = logo.PrimitiveAspectPrimitiveAspectContext.getSelf(_self);
    return  logo.PrimitiveAspect._privk3_eval(_self_, _self,context);
  }
  
  protected static int _privk3_eval(final BackAspectBackAspectProperties _self_, final Back _self, final Context context) {
    Expression _steps = _self.getSteps();
    Object _eval = ExpressionAspect.eval(_steps, context);
    int param = ((-1) * (((Integer) _eval)).intValue());
    InputOutput.<String>println(("BACK " + Integer.valueOf(param)));
    context.turtle.forward(param);
    return 0;
  }
}
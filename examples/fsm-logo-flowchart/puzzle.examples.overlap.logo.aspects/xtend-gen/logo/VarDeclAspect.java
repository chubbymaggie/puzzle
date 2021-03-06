package logo;

import fr.inria.diverse.k3.al.annotationprocessor.Aspect;
import java.util.Hashtable;
import kmLogo.Expression;
import kmLogo.VarDecl;
import logo.ExpressionAspect;
import logo.VarDeclAspectVarDeclAspectProperties;

@Aspect(className = VarDecl.class)
@SuppressWarnings("all")
public class VarDeclAspect {
  public static void eval(final VarDecl _self, final Hashtable<String, Object> context) {
    logo.VarDeclAspectVarDeclAspectProperties _self_ = logo.VarDeclAspectVarDeclAspectContext.getSelf(_self);
    _privk3_eval(_self_, _self,context);
  }
  
  protected static void _privk3_eval(final VarDeclAspectVarDeclAspectProperties _self_, final VarDecl _self, final Hashtable<String, Object> context) {
    String _key = _self.getKey();
    Expression _expression = _self.getExpression();
    Object _eval = ExpressionAspect.eval(_expression, context);
    context.put(_key, _eval);
  }
}

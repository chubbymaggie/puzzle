/**
 */
package flowchartsemantics.flowchartmt.flowchartpck;

import java.util.Hashtable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Assignation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link flowchartsemantics.flowchartmt.flowchartpck.Assignation#getVarRef <em>Var Ref</em>}</li>
 *   <li>{@link flowchartsemantics.flowchartmt.flowchartpck.Assignation#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see flowchartsemantics.flowchartmt.flowchartpck.FlowchartpckPackage#getAssignation()
 * @model
 * @generated
 */
public interface Assignation extends Statement {
	/**
	 * Returns the value of the '<em><b>Var Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Var Ref</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Var Ref</em>' reference.
	 * @see #setVarRef(VarDecl)
	 * @see flowchartsemantics.flowchartmt.flowchartpck.FlowchartpckPackage#getAssignation_VarRef()
	 * @model required="true"
	 * @generated
	 */
	VarDecl getVarRef();

	/**
	 * Sets the value of the '{@link flowchartsemantics.flowchartmt.flowchartpck.Assignation#getVarRef <em>Var Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Var Ref</em>' reference.
	 * @see #getVarRef()
	 * @generated
	 */
	void setVarRef(VarDecl value);

	/**
	 * Returns the value of the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression</em>' containment reference.
	 * @see #setExpression(Expression)
	 * @see flowchartsemantics.flowchartmt.flowchartpck.FlowchartpckPackage#getAssignation_Expression()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getExpression();

	/**
	 * Sets the value of the '{@link flowchartsemantics.flowchartmt.flowchartpck.Assignation#getExpression <em>Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression</em>' containment reference.
	 * @see #getExpression()
	 * @generated
	 */
	void setExpression(Expression value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model contextDataType="flowchartsemantics.flowchartmt.flowchartpck.Hashtable"
	 * @generated
	 */
	void eval(Hashtable context);

} // Assignation

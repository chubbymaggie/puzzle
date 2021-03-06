/**
 */
package simpleconstraints;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relational Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link simpleconstraints.RelationalConstraint#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see simpleconstraints.SimpleconstraintsPackage#getRelationalConstraint()
 * @model
 * @generated
 */
public interface RelationalConstraint extends Constraint {
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
	 * @see simpleconstraints.SimpleconstraintsPackage#getRelationalConstraint_Expression()
	 * @model containment="true" required="true" derived="true"
	 * @generated
	 */
	Expression getExpression();

	/**
	 * Sets the value of the '{@link simpleconstraints.RelationalConstraint#getExpression <em>Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression</em>' containment reference.
	 * @see #getExpression()
	 * @generated
	 */
	void setExpression(Expression value);

} // RelationalConstraint

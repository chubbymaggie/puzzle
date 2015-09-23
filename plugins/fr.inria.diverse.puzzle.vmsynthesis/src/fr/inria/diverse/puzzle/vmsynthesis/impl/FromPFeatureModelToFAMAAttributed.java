package fr.inria.diverse.puzzle.vmsynthesis.impl;

import java.util.Iterator;

import vm.PBinaryExpression;
import vm.PBinaryOperator;
import vm.PBooleanExpression;
import vm.PConstraint;
import vm.PFeature;
import vm.PFeatureGroup;
import vm.PFeatureModel;
import vm.PFeatureRef;
import vm.PUnaryExpression;
import vm.PUninaryOperator;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.ComplexConstraint;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.KeyWords;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

/**
 * Offers the services for translating feature models from diverse formats to the VM metamodel.
 * @author David Mendez-Acuna
 *
 */
public class FromPFeatureModelToFAMAAttributed {

	// -----------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------
	
	private static FromPFeatureModelToFAMAAttributed instance;
	
	// -----------------------------------------------------------
	// Constructor and singleton
	// -----------------------------------------------------------
	
	private FromPFeatureModelToFAMAAttributed(){
		
	}
	
	public static FromPFeatureModelToFAMAAttributed getInstance(){
		if(instance == null)
			instance = new FromPFeatureModelToFAMAAttributed();
		return instance;
	}

	// -----------------------------------------------------------
	// Methods
	// -----------------------------------------------------------

	/**
	 * Translates from a features model from FAMAFeatureModel (FAMA) to FeatureModel (Puzzle).
	 * @param famafm The feature model as an FAMAFeatureModel object.
	 * @return
	 */
	public FAMAAttributedFeatureModel fromPFeatureModelToFAMA(
			PFeatureModel pFeatureModel) {
		
		FAMAAttributedFeatureModel famaFeatureModel = new FAMAAttributedFeatureModel();
		famaFeatureModel.setRoot(fromFAMAFeatureToPFeature(pFeatureModel.getRootFeature()));
		
		for (PConstraint pConstraint : pFeatureModel.getConstraints()) {
			Tree<String> constraintAST = new Tree<String>(this.createNode(pConstraint.getExpression()));
			Constraint constraint = new ComplexConstraint(constraintAST);
			famaFeatureModel.addConstraint(constraint);
		}
		return famaFeatureModel;
	}
	
	private Node<String> createNode(PBooleanExpression expression) {
		if(expression instanceof PBinaryExpression){
			PBinaryExpression binaryExpression = (PBinaryExpression) expression;
			String operator = this.getOperatorByString(binaryExpression.getOperator().getName());
			Node<String> treeRoot = new Node<String>(operator);
			Node<String> leftNode = this.createNode(binaryExpression.getLeft());
			Node<String> rightNode = this.createNode(binaryExpression.getRight());
			treeRoot.addChild(leftNode);
			treeRoot.addChild(rightNode);
			return treeRoot;
		}
		else if(expression instanceof PUnaryExpression){
			PUnaryExpression unaryExpression = (PUnaryExpression) expression;
			String operator = this.getOperatorByString(unaryExpression.getOperator().getName());
			Node<String> treeRoot = new Node<String>(operator);
			Node<String> exprNode = this.createNode(unaryExpression.getExpr());
			treeRoot.addChild(exprNode);
			return treeRoot;
		}
		else if(expression instanceof PFeatureRef){
			PFeatureRef featureRef = (PFeatureRef) expression;
			Node<String> treeRoot = new Node<String>(featureRef.getRef().getName());
			return treeRoot;
		}
		else
			return null;
	}

	private String getOperatorByString(String name) {
		if(name.equals(PBinaryOperator.IMPLIES.getName()))
			return KeyWords.IMPLIES;
		else if(name.equals(PBinaryOperator.AND.getName()))
			return KeyWords.AND;
		else if(name.equals(PBinaryOperator.OR.getName()))
			return KeyWords.OR;
		else if(name.equals(PUninaryOperator.NOT.getName()))
			return KeyWords.NOT;
		else
			return null;
	}

	/**
	 * Finds a FAMA feature by the name in the features model in the parameter
	 * @param name
	 * @param root
	 * @return
	 */
	private AttributedFeature getFamaFeatureByName(String name, AttributedFeature root) {
		if(root.getName().equals(name))
			return root;
		else{
			Iterator<Relation> it = root.getRelations();
			while (it.hasNext()) {
				Relation relation = (Relation) it.next();
				Iterator<AttributedFeature> ito = relation.getDestination();
				while (ito.hasNext()) {
					AttributedFeature attributedFeature = (AttributedFeature) ito.next();
					if(attributedFeature != null)
						return attributedFeature;
				}
			}
		}
		return null;
	}

	/**
	 * In-deep translating of the FAMA feature in the parameter to a new puzzle feature.
	 * @param famaFeature. The FAMA feature that should be translated. 
	 * @return
	 */
	private AttributedFeature fromFAMAFeatureToPFeature(PFeature pFeature){
		AttributedFeature feature = new AttributedFeature(pFeature.getName());
		
		for (PFeatureGroup group : pFeature.getGroups()) {
			Relation relation = new Relation();
			relation.setParent(feature);
			
			Cardinality cardinality = new Cardinality(group.getCardinality().getLowerBound(), group.getCardinality().getUpperBound());
			relation.addCardinality(cardinality);
			
			for (PFeature groupFeature : group.getFeatures()) {
				AttributedFeature relationFeature = this.fromFAMAFeatureToPFeature(groupFeature);
				relation.addDestination(relationFeature);
			}
			
			feature.addRelation(relation);
		}
		return feature;
	}
}
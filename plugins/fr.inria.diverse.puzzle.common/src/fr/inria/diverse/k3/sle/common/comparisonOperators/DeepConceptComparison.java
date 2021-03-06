package fr.inria.diverse.k3.sle.common.comparisonOperators;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EReference;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;

public class DeepConceptComparison implements ConceptComparison {

	@Override
	public boolean equals(EClassifier left, EClassifier right) {
		if(left != null && left.getName() != null && right != null && right.getName() != null){
			if(left instanceof EClass && right instanceof EClass)
				return this.compareEClasses((EClass) left, (EClass) right);
			else if(left instanceof EEnum && right instanceof EEnum)
				return this.compareEEnums((EEnum) left, (EEnum) right);
			else
				return false;
		}
		else
			return false;
	}

	private boolean compareEClasses(EClass left, EClass right) {
		if(!left.getName().equals(right.getName()))
			return false;
		
		if(left.getEReferences().size() != right.getEReferences().size())
			return false;
		
		for (EReference eReference : left.getEReferences()) {
			if(!existsEReference(right, eReference))
				return false;
		}
		
		if(left.getEAttributes().size() != right.getEAttributes().size())
			return false;
		
		for (EAttribute eAttribute : left.getEAttributes()) {
			if(!existsEAttribute(right, eAttribute))
				return false;
		}
		
		return true;
	}

	private boolean existsEReference(EClass eClass, EReference eReference){
		for (EReference current : eClass.getEReferences()) {
			if(compareEReferences(current, eReference))
				return true;
		}
		return false;
	}
	
	private boolean compareEReferences(EReference left, EReference right) {
		boolean identicalNames = left.getName().equals(right.getName());

		boolean identicalTypes = false;
		if(left.getEType() != null && right.getEType() != null && 
				left.getEType().getName() != null && right.getEType().getName() != null){
			identicalTypes = left.getEType().getName().equals(right.getEType().getName());
		}
		else if(left.getEType() == null && right.getEType() == null){
			identicalTypes = true;
		}
		
		boolean identicalLowerBounds = left.getLowerBound() == right.getLowerBound();
		boolean identicalUperBounds = left.getUpperBound() == right.getUpperBound();
		boolean identicalContainment = left.isContainment() == right.isContainment();
		
		return identicalNames && identicalTypes && identicalLowerBounds && identicalUperBounds && identicalContainment;
	}

	private boolean existsEAttribute(EClass eClass, EAttribute eAttribute) {
		for (EAttribute current : eClass.getEAttributes()) {
			if(compareEAttributes(current, eAttribute))
				return true;
		}
		return false;
	}
	
	private boolean compareEAttributes(EAttribute left, EAttribute right) {
		boolean identicalNames = left.getName().equals(right.getName());
		
		boolean identicalTypes = false;
		if(left.getEType() != null && right.getEType() != null && 
				left.getEType().getName() != null && right.getEType().getName() != null){
			identicalTypes = left.getEType().getName().equals(right.getEType().getName());
		}
		else if(left.getEType() == null && right.getEType() == null){
			identicalTypes = true;
		}		
		boolean identicalLowerBounds = left.getLowerBound() == right.getLowerBound();
		boolean identicalUperBounds = left.getUpperBound() == right.getUpperBound();
		
		return identicalNames && identicalTypes && identicalLowerBounds && identicalUperBounds;
	}
	
	private boolean compareEEnums(EEnum left, EEnum right) {
		if(!left.getName().equals(right.getName()))
			return false;
		
		if(left.getELiterals().size() != right.getELiterals().size())
			return false;
		
		for (EEnumLiteral eLiteral : left.getELiterals()) {
			if(!existsLiteral(right, eLiteral)){
				return false;
			}
				
		}
		
		return true;
	}

	private boolean existsLiteral(EEnum eEnum, EEnumLiteral eLiteral) {
		for (EEnumLiteral current : eEnum.getELiterals()) {
			if(compareELiteral(current, eLiteral))
				return true;
		}
		return false;
	}

	private boolean compareELiteral(EEnumLiteral left, EEnumLiteral right) {
		boolean identicalNames = left.getName().equals(right.getName());
		boolean identicalLiteral = left.getLiteral().equals(right.getLiteral());
		boolean identicalValue = left.getValue() == right.getValue();
		return identicalNames && identicalLiteral && identicalValue;
	}
}

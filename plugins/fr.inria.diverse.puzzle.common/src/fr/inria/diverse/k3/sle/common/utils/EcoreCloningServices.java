package fr.inria.diverse.k3.sle.common.utils;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

public class EcoreCloningServices {

	private static EcoreCloningServices instance;
	
	/** Stores the classifiers of the new metamodel that have been already cloned.*/
	private Hashtable<String, EClassifier> clonedClassifiers;
	
	/** Stores the classifiers of the original metamodel that have been already cloned. */
	private Hashtable<String, EClassifier> oldClassifiers;
	
	private EcoreCloningServices(){
		clonedClassifiers = new Hashtable<String, EClassifier>();
		oldClassifiers = new Hashtable<String, EClassifier>();
	}
	
	public static EcoreCloningServices getInstance(){
		if(instance == null)
			instance = new EcoreCloningServices();
		return instance;
	}
	
	/**
	 * Clones the eclass in the parameter.
	 * @param oldEClass
	 * @return
	 */
	public EClass cloneEClass(EClass oldEClass) {
		EClass newEClass = EcoreFactory.eINSTANCE.createEClass();
		newEClass.setAbstract(oldEClass.isAbstract());
		newEClass.setInstanceClass(oldEClass.getInstanceClass());
		newEClass.setInstanceClassName(oldEClass.getInstanceClassName());
		newEClass.setInstanceTypeName(oldEClass.getInstanceTypeName());
		newEClass.setInterface(oldEClass.isInterface());
		newEClass.setName(oldEClass.getName());
		
		//Cloning the attributes. 
		for (EStructuralFeature eStructuralFeature : oldEClass.getEStructuralFeatures()) {
			if(eStructuralFeature instanceof EAttribute){
				EAttribute newAttribute = this.cloneEAttribute((EAttribute)eStructuralFeature);
				newEClass.getEStructuralFeatures().add(newAttribute);
			}
		}
		
		//Cloning the references. 
		for (EStructuralFeature eStructuralFeature : oldEClass.getEStructuralFeatures()) {
			if(eStructuralFeature instanceof EReference){
				EReference newEReference = this.cloneEReference((EReference)eStructuralFeature);
				newEClass.getEStructuralFeatures().add(newEReference);
			}
		}
		
		oldClassifiers.put(oldEClass.getName(), oldEClass);
		clonedClassifiers.put(newEClass.getName(), newEClass);
		
		return newEClass;
	}

	/**
	 * 
	 * @param oldEAttribute
	 * @return
	 */
	private EAttribute cloneEAttribute(EAttribute oldEAttribute) {
		EAttribute newEAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		newEAttribute.setChangeable(oldEAttribute.isChangeable());
		newEAttribute.setDefaultValueLiteral(oldEAttribute.getDefaultValueLiteral());
		newEAttribute.setDerived(oldEAttribute.isDerived());
		newEAttribute.setID(oldEAttribute.isID());
		newEAttribute.setLowerBound(oldEAttribute.getLowerBound());
		newEAttribute.setName(oldEAttribute.getName());
		newEAttribute.setOrdered(oldEAttribute.isOrdered());
		newEAttribute.setTransient(oldEAttribute.isTransient());
		newEAttribute.setUnique(oldEAttribute.isUnique());
		newEAttribute.setUnsettable(oldEAttribute.isUnsettable());
		newEAttribute.setUpperBound(oldEAttribute.getUpperBound());
		newEAttribute.setVolatile(oldEAttribute.isVolatile());
		
		if(!(oldEAttribute.getEType() instanceof EEnum)){
			newEAttribute.setEType(oldEAttribute.getEType());
		}
		
		return newEAttribute;
	}

	private EReference cloneEReference(EReference oldEReference) {
		EReference newEReference = EcoreFactory.eINSTANCE.createEReference();
		newEReference.setChangeable(oldEReference.isChangeable());
		newEReference.setContainment(oldEReference.isContainment());
		newEReference.setDefaultValueLiteral(oldEReference.getDefaultValueLiteral());
		newEReference.setDerived(oldEReference.isDerived());
		newEReference.setLowerBound(oldEReference.getLowerBound());
		newEReference.setName(oldEReference.getName());
		newEReference.setOrdered(oldEReference.isOrdered());
		newEReference.setResolveProxies(oldEReference.isResolveProxies());
		newEReference.setTransient(oldEReference.isTransient());
		newEReference.setUnique(oldEReference.isUnique());
		newEReference.setUnsettable(oldEReference.isUnsettable());
		newEReference.setUpperBound(oldEReference.getUpperBound());
		newEReference.setVolatile(oldEReference.isVolatile());
		return newEReference;
	}
	
	/**
	 * Clones the enum in the parameter. 
	 * @param oldEEnum
	 * @return
	 */
	public EEnum cloneEEnum(EEnum oldEEnum) {
		EEnum newEEnum = EcoreFactory.eINSTANCE.createEEnum();
		newEEnum.setInstanceClass(oldEEnum.getInstanceClass());
		newEEnum.setInstanceClassName(oldEEnum.getInstanceClassName());
		newEEnum.setInstanceTypeName(oldEEnum.getInstanceTypeName());
		newEEnum.setName(oldEEnum.getName());
		newEEnum.setSerializable(oldEEnum.isSerializable());
		
		for (EEnumLiteral oldLiteral : oldEEnum.getELiterals()) {
			EEnumLiteral newLiteral = this.cloneEEnumLiteral(oldLiteral);
			newEEnum.getELiterals().add(newLiteral);
		}
		
		oldClassifiers.put(oldEEnum.getName(), oldEEnum);
		clonedClassifiers.put(newEEnum.getName(), newEEnum);
		
		return newEEnum;
	}

	/**
	 * Clones the enum literal in the parameter.
	 * @param oldLiteral
	 * @return
	 */
	private EEnumLiteral cloneEEnumLiteral(EEnumLiteral oldLiteral) {
		EEnumLiteral newEEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
		newEEnumLiteral.setInstance(oldLiteral.getInstance());
		newEEnumLiteral.setLiteral(oldLiteral.getLiteral());
		newEEnumLiteral.setName(oldLiteral.getName());
		newEEnumLiteral.setValue(oldLiteral.getValue());
		return newEEnumLiteral;
	}

	/**
	 * Assigns the correct type to each (local) reference of the eclasses of the given metamodel
	 * @param metamodel
	 */
	public void resolveLocalReferences(EPackage metamodel) {
		for (EClassifier eClassifier : metamodel.getEClassifiers()) {
			if(eClassifier instanceof EClass){
				EClass eClass = (EClass)eClassifier;
				for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures()) {
					if(eStructuralFeature instanceof EReference){
						EReference eReference = (EReference)eStructuralFeature;
						if(eReference.getEType() == null){
							EReference oldEReference = EcoreQueries.
									searchEReferenceByName((EClass)oldClassifiers.get(eClass.getName()), 
											eReference.getName());
							EClassifier eReferenceType = clonedClassifiers.get(oldEReference.getEType().getName());
							eReference.setEType(eReferenceType);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Assigns the correct type to each (local) attribute of the eclasses of the given metamodel
	 * @param metamodel
	 */
	public void resolveLocalAttributes(EPackage metamodel) {
		for (EClassifier eClassifier : metamodel.getEClassifiers()) {
			if(eClassifier instanceof EClass){
				EClass eClass = (EClass)eClassifier;
				for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures()) {
					if(eStructuralFeature instanceof EAttribute){
						EAttribute eAttribute = (EAttribute) eStructuralFeature;
						if(eAttribute.getEType() == null){
							EAttribute oldEAttribute = EcoreQueries.
									searchEAttributeByName((EClass)oldClassifiers.get(eClass.getName()), 
											eAttribute.getName());
							System.out.println("oldEAttribute: " + oldEAttribute.getName());
							EClassifier eAttributeType = clonedClassifiers.get(oldEAttribute.getEType().getName());
							eAttribute.setEType(eAttributeType);
						}
					}
				}
			}
		}
		
	}
	
	public void resolveInterfaceReferences(EPackage metamodel) {
		ArrayList<EClassifier> toAdd = new ArrayList<EClassifier>();
		for (EClassifier eClassifier : metamodel.getEClassifiers()) {
			if(eClassifier instanceof EClass){
				EClass eClass = (EClass)eClassifier;
				for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures()) {
					if(eStructuralFeature instanceof EReference){
						EReference eReference = (EReference)eStructuralFeature;
						if(eReference.getEType() == null){
							EReference oldEReference = EcoreQueries.
									searchEReferenceByName((EClass)oldClassifiers.get(eClass.getName()), 
											eReference.getName());
							EClassifier eReferenceType = clonedClassifiers.get(oldEReference.getEType().getName());
							
							if(eReferenceType == null){
								eReferenceType = EcoreFactory.eINSTANCE.createEClass();
								eReferenceType.setName(oldEReference.getEType().getName());
								EAnnotation requiredAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
								requiredAnnotation.setSource("Required");
								eReferenceType.getEAnnotations().add(requiredAnnotation);
								toAdd.add(eReferenceType);
								clonedClassifiers.put(eReferenceType.getName(), eReferenceType);
							}
							eReference.setEType(eReferenceType);
						}
					}
				}
			}
		}
		metamodel.getEClassifiers().addAll(toAdd);
	}
	
	public void resolveLocalSuperTypes(EPackage metamodel){
		for (EClassifier eClassifier : metamodel.getEClassifiers()) {
			if(eClassifier instanceof EClass){
				EClass eClass = (EClass)eClassifier;
				System.out.println(eClass.getName());
				EClass oldEClass = (EClass) oldClassifiers.get(eClass.getName());
				
				if(oldEClass != null){
					for (EClass oldSuperType : oldEClass.getESuperTypes()) {
						EClass newSuperType = (EClass) clonedClassifiers.get(oldSuperType.getName());
						
						if(newSuperType != null)
							eClass.getESuperTypes().add(newSuperType);
					}
				}
			}
		}
	}
	
	public void resetClonedClassifiers(){
		clonedClassifiers = new Hashtable<String, EClassifier>();
	}
	
	public void resetOldClassifiers(){
		oldClassifiers = new Hashtable<String, EClassifier>();
	}
}
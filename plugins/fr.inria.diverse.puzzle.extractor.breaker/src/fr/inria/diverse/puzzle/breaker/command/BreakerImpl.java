package fr.inria.diverse.puzzle.breaker.command;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGroup;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMembers;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;
import fr.inria.diverse.k3.sle.common.utils.EcoreCloningServices;
import fr.inria.diverse.k3.sle.common.utils.EcoreQueries;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.k3.sle.common.utils.MelangeServices;
import fr.inria.diverse.k3.sle.common.utils.ModelUtils;
import fr.inria.diverse.k3.sle.common.utils.ProjectManagementServices;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Aspect;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.metrics.componentsMetrics.ModularizationQuality;

public class BreakerImpl {

	// ---------------------------------------------
	// Attributes
	// ---------------------------------------------
	
	private static BreakerImpl instance;
	
	// ---------------------------------------------
	// Constructor and singleton
	// ---------------------------------------------
	
	private BreakerImpl(){
		
	}
	
	public static BreakerImpl getInstance(){
		if(instance == null)
			instance = new BreakerImpl();
		return instance;
	}
	
	// ---------------------------------------------
	// Methods
	// ---------------------------------------------
	
	/**
	 * Breaks-down the family in the parameter using the comparison operators and the decomposition strategy in the parameters (TODO) 
	 * @param languages
	 * @throws Exception
	 */
	public EcoreGraph breakDownFamily(ArrayList<Language> languages, SynthesisProperties synthesisProperties, IProject lplProject) throws Exception{
		ConceptComparison conceptComparisonOperator = synthesisProperties.getConceptComparisonOperator();
		GraphPartition graphPartition = synthesisProperties.getGraphPartition();
		
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(languages);
		ArrayList<TupleConceptMembers> conceptMembersList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, conceptComparisonOperator);
		ArrayList<TupleMembersConcepts> membersConceptList = FamiliesServices.getInstance().getMembersGroupVsConceptVOList(conceptMembersList);
		EcoreGraph dependenciesGraph = new EcoreGraph(membersConceptList, conceptComparisonOperator);
		graphPartition.graphPartition(dependenciesGraph, membersConceptList, conceptComparisonOperator);
		
		buildModules(dependenciesGraph, languages);
		
		double mq = (new ModularizationQuality()).compute(dependenciesGraph);
		System.out.println("Modularization Quality: " + mq);
		
		return dependenciesGraph;
	}

	private void buildModules(EcoreGraph dependenciesGraph,
			ArrayList<Language> languages) throws Exception {
		for (EcoreGroup group : dependenciesGraph.getGroups()) {
			ArrayList<EClassifier> requiredClassifiers = buildSyntacticModule(group, languages);
			buildSemanticModule(group, languages, requiredClassifiers);
		}
	}

	/**
	 * Builds a syntactic module for the given constructs group.
	 * Returns the set of classifiers required by the generated module. 
	 * @param group
	 * @throws CoreException
	 * @throws IOException 
	 */
	private ArrayList<EClassifier> buildSyntacticModule(EcoreGroup group, ArrayList<Language> languages) throws Exception {
		ArrayList<EClassifier> requiredClassifiers = new ArrayList<EClassifier>();
		
		EPackage moduleEPackage = this.createEPackageByModule(group);
		
		// Adding the constructs that are not used in the syntax but in the semantics
		ArrayList<Aspect> allAspects = findAspects(group, languages);
		addMetaclassesRequiredByTheSemantics(allAspects, moduleEPackage);
		
		// Adding the required operations to the required interface
		EcoreQueries.searchRequiredConcepts(moduleEPackage, requiredClassifiers);
		ArrayList<Aspect> requiredAspects = findAspects(requiredClassifiers, languages);
		addRequiredOperationsToRequiredInterface(requiredAspects, moduleEPackage);
		
		String languageName = EcoreGraph.getLanguageModuleName(group.getVertex()).trim();
		
		// Create the module project with the folders.
		IProject moduleProject = ProjectManagementServices.createEclipseProject("fr.inria.diverse.module." + 
				languageName + ".syntax");
		String modelsFolderPath = ProjectManagementServices.createFolderByName(moduleProject, "models");
					
		// Serialize the module metamodel in the corresponding project. 
		String ecoreLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + ".ecore";
		ModelUtils.saveEcoreFile(ecoreLocation, moduleEPackage);
		
		// Create the genmodel and generate the code of the module.
		String genModelLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + ".genmodel";
		ProjectManagementServices.generateGenmodelFile(moduleProject, moduleEPackage, ecoreLocation, genModelLocation, moduleProject.getName(), languageName);
		
		// Refresh projects
		ProjectManagementServices.refreshProject(moduleProject);
		
		return requiredClassifiers;
	}

	private void addMetaclassesRequiredByTheSemantics(
			ArrayList<Aspect> allAspects, EPackage moduleEPackage) {
		
		for (Aspect aspect : allAspects) {
			System.out.println("ecore fragment: " + aspect.getEcoreFragment().getEClassifiers());
		}
		
	}

	/**
	 * Adds the required operations to the required interface in the metamodel in the parameter. 
	 * @param requiredAspects
	 * @param moduleEPackage
	 */
	private void addRequiredOperationsToRequiredInterface(
			ArrayList<Aspect> requiredAspects, EPackage moduleEPackage) {
		for (Aspect aspect : requiredAspects) {
			EClass metaclass = EcoreQueries.searchEClassByName(moduleEPackage, aspect.getAspectedClass().getName());
			ArrayList<EOperation> eOperations = createEOperationsByAspect(aspect, moduleEPackage);
			metaclass.getEOperations().addAll(eOperations);
		}
	}

	/**
	 * Returns a list of eoperations that represents the list of methods defined in the given aspect. 
	 * @param aspect
	 * @return eOperations
	 */
	private ArrayList<EOperation> createEOperationsByAspect(Aspect aspect, EPackage metamodel) {
		ArrayList<EOperation> eOperations = new ArrayList<EOperation>();
		
		for (EObject aspectContent : aspect.getAspectTypeRef().getType().eContents()) {
			if(aspectContent instanceof JvmOperation){
				JvmOperation operation = (JvmOperation) aspectContent;
				if(!operation.getSimpleName().startsWith("_privk3_")){
					EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
					eOperation.setName(operation.getSimpleName());
					EClassifier returnType = findType(operation.getReturnType(), metamodel);
					eOperation.setEType(returnType);
					ArrayList<EParameter> params = convertParameters(operation.getParameters(), metamodel);
					eOperation.getEParameters().addAll(params);
					eOperations.add(eOperation);
				}
			}
		}
		return eOperations;
	}

	/**
	 * Finds an ecore type from a java type. 
	 * @param returnType
	 * @param metamodel
	 * @return
	 */
	private EClassifier findType(JvmTypeReference returnType, EPackage metamodel) {
		String typeName = returnType.getSimpleName();

		EClassifier metaclass = EcoreQueries.searchEClassByName(metamodel, typeName);
		if(metaclass != null)
			return metaclass;
		
		EClassifier nativeEcoreType = EcoreQueries.searchNativeTypeByName(typeName);
		if(nativeEcoreType != null)
			return nativeEcoreType;
		
		if(returnType.getSimpleName().contains("void") || returnType.getSimpleName().contains("Void"))
			return null;
		
		return EcorePackage.eINSTANCE.getEJavaObject();
	}
	
	/**
	 * Converts a list of java parameters to ecore parameters. 
	 * @param parameters
	 * @return
	 */
	private ArrayList<EParameter> convertParameters(
			EList<JvmFormalParameter> parameters, EPackage metamodel) {
		ArrayList<EParameter> eparams = new ArrayList<EParameter>();
		for (JvmFormalParameter javaParameter : parameters) {
			if(!javaParameter.getSimpleName().equals("_self")){
				EParameter eparam = EcoreFactory.eINSTANCE.createEParameter();
				eparam.setName(javaParameter.getSimpleName());
				eparam.setEType(findType(javaParameter.getParameterType(), metamodel));
				eparams.add(eparam);
			}
		}
		return eparams;
	}

	/**
	 * Builds a semantic module for the given constructs group. 
	 * @param group
	 * @throws CoreException
	 * @throws IOException 
	 */
	private void buildSemanticModule(EcoreGroup group,
			ArrayList<Language> languages, ArrayList<EClassifier> requiredClassifiers) throws Exception {
		
		// Create the module project with the folders.
		String moduleName = EcoreGraph.getLanguageModuleName(group.getVertex()).trim();
		IProject moduleProject = ProjectManagementServices.createEclipseProject("fr.inria.diverse.module." + 
				moduleName + ".semantics");
		ProjectManagementServices.createXtendConfigurationFile(moduleProject, moduleName);
		
		ArrayList<EClassifier> classifiers = new ArrayList<EClassifier>();
		for (EcoreVertex vertex : group.getVertex()) {
			classifiers.add(vertex.getClassifier());
		}
		
		ArrayList<Aspect> aspects = findAspects(group, languages);
		ArrayList<Aspect> requiredAspects = findAspects(requiredClassifiers, languages);
		
		for (Aspect aspect : aspects) {
			ProjectManagementServices.copyAspectResource(aspect.getAspectTypeRef().getType().eResource(), moduleProject, moduleName, classifiers, requiredAspects);
		}
		
		// Refresh projects
		ProjectManagementServices.refreshProject(moduleProject);
	}
	
	/**
	 * Returns the set of aspects associated to the group in the parameter. 
	 * @param group
	 * @param languages
	 * @return
	 */
	private ArrayList<Aspect> findAspects(EcoreGroup group,
			ArrayList<Language> languages) {
		ArrayList<EClassifier> classifiers = new ArrayList<EClassifier>();
		
		for (EcoreVertex vertex : group.getVertex()) {
			classifiers.add(vertex.getClassifier());
		}
		
		return this.findAspects(classifiers, languages);
	}
	
	/**
	 * Returns the collection of aspects associated to the classifiers in the parameter. 
	 * @param requiredClassifiers
	 * @return
	 */
	private ArrayList<Aspect> findAspects(
			ArrayList<EClassifier> eclassifiers, ArrayList<Language> languages) {
		ArrayList<Aspect> aspects = new ArrayList<Aspect>();
		
		for (EClassifier eclassifier : eclassifiers) {
			ArrayList<Aspect> aspectsByMetaclass = findAspectsByMetaclass(eclassifier, languages);
			aspects.addAll(aspectsByMetaclass);
		}
		return aspects;
	}
	
	/**
	 * Returns the list of aspects associated to the given metaclass.
	 * @param classifier
	 * @param languages
	 * @return
	 */
	private ArrayList<Aspect> findAspectsByMetaclass(EClassifier classifier, ArrayList<Language> languages) {
		ArrayList<Aspect> aspects = new ArrayList<Aspect>();
		for (Language language : languages) {
			EPackage languageMetamodel = MelangeServices.getEPackageFromLanguage(language);
			
			if(EcoreQueries.searchEClassByName(languageMetamodel, classifier.getName()) != null){
				for (Aspect aspect : language.getSemantics()) {
					if(aspect.getAspectedClass().getName().equals(classifier.getName())){
						aspects.add(aspect);
					}
				}
				break;
			}
		}
		return aspects;
	}
	
	// ---------------------------------------------
	// Utilities
	// ---------------------------------------------

	/**
	 * Creates a metamodel by module taking into consideration the corresponding dependencies with other modules
	 * by establishing the required interfaces.
	 * @param moduleConceptsVO
	 * @return
	 */
	private EPackage createEPackageByModule(EcoreGroup group) {
		EcoreCloningServices.getInstance().resetClonedClassifiers();
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
		String moduleName = EcoreGraph.getLanguageModuleName(group.getVertex());
		newPackage.setName(moduleName.trim());
		newPackage.setNsPrefix(moduleName.trim());
		newPackage.setNsURI(moduleName.trim());
		
		for (EcoreVertex vertex : group.getVertex()) {
			if(vertex.getClassifier() instanceof EClass){
				EClass newClass = EcoreCloningServices.getInstance().cloneEClass((EClass)vertex.getClassifier());
				newPackage.getEClassifiers().add(newClass);
			}
			else if(vertex.getClassifier() instanceof EEnum){
				EEnum newEEnum = EcoreCloningServices.getInstance().cloneEEnum((EEnum)vertex.getClassifier());
				newPackage.getEClassifiers().add(newEEnum);
			}
		}
		EcoreCloningServices.getInstance().resolveLocalReferences(newPackage);
		EcoreCloningServices.getInstance().resolveLocalAttributes(newPackage);
		EcoreCloningServices.getInstance().resolveInterfaceReferences(newPackage);
		EcoreCloningServices.getInstance().resolveLocalSuperTypes(newPackage);
		return newPackage;
	}
}
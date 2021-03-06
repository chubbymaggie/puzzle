package fr.inria.diverse.puzzle.breaker.command;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
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
import fr.inria.diverse.k3.sle.common.vos.AspectLanguageMapping;
import fr.inria.diverse.k3.sle.common.vos.MetaclassAspectMapping;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Aspect;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Implementation for the command: break-down family of DSLs
 * @author David Mendez-Acuna
 *
 */
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
		MethodComparison methodComparison = synthesisProperties.getMethodComparisonOperator();
		GraphPartition graphPartition = synthesisProperties.getGraphPartition();
		
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(languages);
		ArrayList<TupleConceptMembers> conceptMembersList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, conceptComparisonOperator);
		ArrayList<TupleMembersConcepts> membersConceptList = FamiliesServices.getInstance().getMembersGroupVsConceptVOList(conceptMembersList);
		EcoreGraph dependenciesGraph = new EcoreGraph(membersConceptList, conceptComparisonOperator);
		graphPartition.graphPartition(dependenciesGraph, membersConceptList, conceptComparisonOperator);
		
		buildModules(dependenciesGraph, languages, conceptComparisonOperator, methodComparison);
		createSemanticsCommonsProject(languages);
		
		return dependenciesGraph;
	}

	/**
	 * Build the modules corresponding to the partition defined in the given dependencies graph. 
	 * @param dependenciesGraph
	 * @param languages
	 * @param methodComparison
	 * @throws Exception
	 */
	private void buildModules(EcoreGraph dependenciesGraph,
			ArrayList<Language> languages, ConceptComparison conceptComparison,
			MethodComparison methodComparison) throws Exception {
		
		ArrayList<MetaclassAspectMapping> originalMapping = new ArrayList<MetaclassAspectMapping>();
		findAspectMapping(languages, originalMapping, conceptComparison);
		ArrayList<MetaclassAspectMapping> semanticVariabilityMapping = detectSemanticVariability(originalMapping, methodComparison);
		
		for (EcoreGroup group : dependenciesGraph.getGroups()) {
			ArrayList<EClassifier> requiredClassifiers = buildSyntacticModule(group, languages);
			buildSemanticModule(group, originalMapping, semanticVariabilityMapping, languages, requiredClassifiers);
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
		
		for (EcoreVertex requiredVertex : group.getRequiredVertex()) {
			requiredClassifiers.add(requiredVertex.getClassifier());
		}
		
		String moduleName = EcoreGraph.getLanguageModuleName(group.getVertex());
		
		EPackage moduleEPackage = this.createEPackageByModule(group, moduleName);
		ArrayList<EClassifier> metamodelClassifiers = new ArrayList<EClassifier>();
		EcoreQueries.collectEClassifiers(moduleEPackage, metamodelClassifiers);
		
		// Adding the constructs that are not used in the syntax but in the semantics
		ArrayList<Aspect> allAspects = findAspects(group, languages);
		addMetaclassesRequiredByTheSemantics(allAspects, moduleEPackage);
		
		// Adding the required operations to the required interface
		ArrayList<Aspect> requiredAspects = findAspects(requiredClassifiers, languages);
		this.addRequiredOperationsToRequiredInterface(requiredAspects, moduleEPackage);
		
		EPackage moduleRequiredInteface = null;
		if(requiredClassifiers.size() != 0)
			moduleRequiredInteface = this.createEPackageByClassifiersCollection(requiredClassifiers, moduleName + "Req");
		
		EPackage moduleProvidedInteface = this.createEPackageByClassifiersCollection(metamodelClassifiers, moduleName + "Prov");
		String languageName = EcoreGraph.getLanguageModuleName(group.getVertex()).trim();
		
		// Create the module project with the folders.
		IProject moduleProject = ProjectManagementServices.createEclipseJavaProject("fr.inria.diverse.module." + 
				languageName + ".syntax");
		group.setImplementationProjectName(moduleProject.getName());
		group.setImplementationProjectLocation(moduleProject.getLocation().removeLastSegments(1).toString());
		String modelsFolderPath = ProjectManagementServices.createFolderByName(moduleProject, "models");
					
		// Serialize the module metamodel in the corresponding project. 
		String ecoreLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + ".ecore";
		group.setMetamodelPath(ecoreLocation);
		ModelUtils.saveEcoreFile(ecoreLocation, moduleEPackage);
		
		// Serialize the module's required interface in the corresponding project. 
		String requiredInterfaceLocation = null;
		if(moduleRequiredInteface != null){
			requiredInterfaceLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + "Req.ecore";
			group.setRequiredInterfacePath(requiredInterfaceLocation);
			ModelUtils.saveEcoreFile(requiredInterfaceLocation, moduleRequiredInteface);
		}
		
		// Serialize the module's provided interface in the corresponding project. 
		String providedInterfaceLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + "Prov.ecore";
		group.setProvidedInterfacePath(providedInterfaceLocation);
		ModelUtils.saveEcoreFile(providedInterfaceLocation, moduleProvidedInteface);
		
		// Create the module's genmodel and generate the code of the module.
		String genModelLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + ".genmodel";
		ProjectManagementServices.generateGenmodelFile(moduleProject, moduleEPackage, ecoreLocation, genModelLocation, moduleProject.getName(), languageName);
		
		// Create the required interface's genmodel and generate the corresponding code.
		if(moduleRequiredInteface != null){
			String requiredInterfaceGenModelLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + "Req.genmodel";
			ProjectManagementServices.generateGenmodelFile(moduleProject, moduleRequiredInteface, requiredInterfaceLocation, requiredInterfaceGenModelLocation, moduleProject.getName(), languageName);
		}
				
		// Create the provided interface's genmodel and generate the corresponding code.
		String providedInterfaceGenModelLocation = modelsFolderPath + "/" + EcoreGraph.getLanguageModuleName(group.getVertex()) + "Prov.genmodel";
		ProjectManagementServices.generateGenmodelFile(moduleProject, moduleProvidedInteface, providedInterfaceLocation, providedInterfaceGenModelLocation, moduleProject.getName(), languageName);
		
		// Refresh projects
		ProjectManagementServices.refreshProject(moduleProject);
		
		return requiredClassifiers;
	}
	
	/**
	 * Creates a metamodel by module taking into consideration the corresponding dependencies with other modules
	 * by establishing the required interfaces.
	 * @param moduleConceptsVO
	 * @return
	 */
	private EPackage createEPackageByModule(EcoreGroup group, String moduleName) {
		EcoreCloningServices.getInstance().resetClonedClassifiers();
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
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

	private void addMetaclassesRequiredByTheSemantics(
			ArrayList<Aspect> allAspects, EPackage moduleEPackage) {
		//TODO
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
	 * Creates an EPackage with the classifiers in the parameter to build an interface.
	 * @param requiredClassifiers
	 * @return
	 */
	private EPackage createEPackageByClassifiersCollection(
			ArrayList<EClassifier> requiredClassifiers, String moduleName) {
		EcoreCloningServices.getInstance().resetClonedClassifiers();
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
		newPackage.setName(moduleName);
		newPackage.setNsPrefix(moduleName);
		newPackage.setNsURI(moduleName);
		
		for (EClassifier eClassifier : requiredClassifiers) {
			if(eClassifier instanceof EClass){
				EClass newClass = EcoreCloningServices.getInstance().cloneEClass((EClass)eClassifier);
				newPackage.getEClassifiers().add(newClass);
			}
			else if(eClassifier instanceof EEnum){
				EEnum newEEnum = EcoreCloningServices.getInstance().cloneEEnum((EEnum)eClassifier);
				newPackage.getEClassifiers().add(newEEnum);
			}
		}
		
		EcoreCloningServices.getInstance().resolveLocalReferences(newPackage);
		EcoreCloningServices.getInstance().resolveLocalAttributes(newPackage);
		EcoreCloningServices.getInstance().resolveInterfaceReferences(newPackage);
		EcoreCloningServices.getInstance().resolveLocalSuperTypes(newPackage);
		return newPackage;
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
	 * Detects the semantical variability. 
	 * @param mapping
	 * @param methodComparison
	 * @return
	 */
	private ArrayList<MetaclassAspectMapping> detectSemanticVariability(
			ArrayList<MetaclassAspectMapping> mapping, MethodComparison methodComparison) {
		ArrayList<MetaclassAspectMapping> mappingVariability = new ArrayList<MetaclassAspectMapping>();
		
		for (MetaclassAspectMapping metaclassAspectMapping : mapping) {
			MetaclassAspectMapping newEntry = new MetaclassAspectMapping(metaclassAspectMapping.getMetaclass());
			
			for (AspectLanguageMapping aspectMapping : metaclassAspectMapping.getAspects()) {
				AspectLanguageMapping legacyMapping = findAspectMapping(aspectMapping.getAspect(), newEntry, methodComparison);
				
				if(legacyMapping == null){
					AspectLanguageMapping newAspectLanguageMapping = new AspectLanguageMapping(aspectMapping.getAspect(), aspectMapping.getLanguage());
					newEntry.getAspects().add(newAspectLanguageMapping);
				}else{
					legacyMapping.setLanguagesList(legacyMapping.getLanguagesList() + 
							aspectMapping.getLanguage().getName());
					legacyMapping.getLanguagesObjectList().addAll(aspectMapping.getLanguagesObjectList());
				}
			}
			mappingVariability.add(newEntry);
		}
		
		return mappingVariability;
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
	private void buildSemanticModule(EcoreGroup group, ArrayList<MetaclassAspectMapping> originalMapping, 
			ArrayList<MetaclassAspectMapping> semanticVariabilityMapping,
			ArrayList<Language> languages, ArrayList<EClassifier> requiredClassifiers) throws Exception {
		
		// Create the module project with the folders.
		String moduleName = EcoreGraph.getLanguageModuleName(group.getVertex()).trim();
		IProject moduleProject = ProjectManagementServices.createEclipseJavaProject("fr.inria.diverse.module." + 
				moduleName + ".semantics");
		ProjectManagementServices.createXtendConfigurationFile(moduleProject, moduleName, false, "aspects");
		
		ArrayList<EClassifier> classifiers = new ArrayList<EClassifier>();
		for (EcoreVertex vertex : group.getVertex()) {
			classifiers.add(vertex.getClassifier());
		}
		
		ArrayList<Aspect> requiredAspects = findAspects(requiredClassifiers, languages);
		ArrayList<MetaclassAspectMapping> originalLocalMapping = filterAspects(group, originalMapping);
		ArrayList<MetaclassAspectMapping> semanticVariabilityLocalMapping = filterAspects(group, semanticVariabilityMapping);
		
		boolean semanticVariability = thereIsSemanticVariability(semanticVariabilityLocalMapping);
		
		if(semanticVariability){
			for (MetaclassAspectMapping metaclassAspectMapping : originalLocalMapping) {
				for (AspectLanguageMapping aspectLanguageMapping : metaclassAspectMapping.getAspects()) {
					ProjectManagementServices.copyAspectResource(aspectLanguageMapping, moduleProject, moduleName, classifiers, requiredAspects);
				}
			}
		}else{
			for (MetaclassAspectMapping metaclassAspectMapping : semanticVariabilityLocalMapping) {
				for (AspectLanguageMapping aspectLanguageMapping : metaclassAspectMapping.getAspects()) {
					ProjectManagementServices.copyAspectResource(aspectLanguageMapping, moduleProject, moduleName, classifiers, requiredAspects);
				}
			}
		}
		
		// Refresh projects
		ProjectManagementServices.refreshProject(moduleProject);
	}
	
	private boolean thereIsSemanticVariability(
			ArrayList<MetaclassAspectMapping> localMapping) {
		ArrayList<Integer> aspectsAmount = new ArrayList<Integer>();
		for (MetaclassAspectMapping metaclassAspectMapping : localMapping) {
			aspectsAmount.add(metaclassAspectMapping.getAspects().size());
			
			System.out.println("mapping: " + metaclassAspectMapping.getMetaclass().getName() + " - "
					+ metaclassAspectMapping.getAspects().size());
			
		}
		System.out.println("aspectsAmount: " + aspectsAmount);
		for (Integer x : aspectsAmount) {
			for (Integer y : aspectsAmount) {
				if(x!=y)
					return true;
			}
		}
		
		return false;
	}

	/**
	 * Creates a project with all the xtend classes used by different modules in the family. 
	 * @param languages. Set of languages under study. 
	 * @throws Exception 
	 */
	private void createSemanticsCommonsProject(ArrayList<Language> languages) throws Exception{
		IProject commonsProject = ProjectManagementServices.createEclipseJavaProject("fr.inria.diverse.commons.semantics");
		ProjectManagementServices.createXtendConfigurationFile(commonsProject, "commons", true, "aspects");
		ProjectManagementServices.createFolderByName(commonsProject, "src/commons");
		
		ArrayList<File> commonResources = this.findCommonResources(languages);
		for (File file : commonResources) {
			ProjectManagementServices.copyNonAspectResource(file, commonsProject);
		}
		ProjectManagementServices.refreshProject(commonsProject);
	}
	
	/**
	 * Scan all the semantic projects of the given languages and returns all the
	 * xtend files that are not implementing an aspect. 
	 * 
	 * @param languages
	 * @return
	 */
	private ArrayList<File> findCommonResources(
			ArrayList<Language> languages) {
		
		ArrayList<IProject> semanticProjects = collectSemanticProjects(languages);
		ArrayList<File> commonResources = new ArrayList<File>();
		for (IProject iProject : semanticProjects) {
			findUnaspectedResources(languages, iProject, commonResources);
		}
		return commonResources;
	}
	
	/**
	 * Collects the semantic projects of the given set of languages. 
	 * @param languages
	 * @return
	 */
	private ArrayList<IProject> collectSemanticProjects(ArrayList<Language> languages){
		ArrayList<IProject> semanticProjects = new ArrayList<IProject>();
		for (Language language : languages) {
			for (Aspect aspect : language.getSemantics()) {
				Resource eResource = aspect.getAspectTypeRef().getType().eResource();
				URI fileURIResource0 = URI.createFileURI(eResource.getURI().path());
				
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject project = root.getProject(fileURIResource0.segments()[1]);
				
				if(!semanticProjects.contains(project))
					semanticProjects.add(project);
			}
		}
		return semanticProjects;
	}

	/**
	 * Fills the commonResources parameter with all the xtend files in the given project that
	 * do not contain an aspect from the given collection of languages. 
	 * 
	 * @param languages
	 * @param iProject
	 * @param commonResources
	 */
	private void findUnaspectedResources(ArrayList<Language> languages,
			IProject iProject, ArrayList<File> commonResources) {
		
		ArrayList<Aspect> allAspects = this.findAllAspects(languages);
		ArrayList<File> xtendFiles = ProjectManagementServices.collectAllXtendFiles(iProject);
		ArrayList<File> unaspectedFiles = new ArrayList<File>();
		
		for (int i = 0; i < xtendFiles.size(); i++) {
			File file = xtendFiles.get(i);
			boolean aspected = false;
			for (int j = 0; j < allAspects.size() && !aspected; j++) {
				Aspect aspect = allAspects.get(j);
				String aspectURIstring = aspect.getAspectTypeRef().getType().eResource().getURI().toString();
				String left = aspectURIstring.substring(aspectURIstring.lastIndexOf("/"));
				String right = file.getPath().substring(file.getPath().lastIndexOf("/"));
				if(left.equals(right)){
					aspected = true;
				}
			}
			if(!aspected){
				unaspectedFiles.add(file);
			}
		}
		commonResources.addAll(unaspectedFiles);
	}
	
	/**
	 * Returns only the aspects corresponding to the given group. 
	 * @param group
	 * @param mapping
	 * @return
	 */
	private ArrayList<MetaclassAspectMapping> filterAspects(EcoreGroup group,
			ArrayList<MetaclassAspectMapping> mapping) {
		ArrayList<MetaclassAspectMapping> filteredMapping = new ArrayList<MetaclassAspectMapping>();
		for (MetaclassAspectMapping metaclassAspectMapping : mapping) {
			if(belongsTo(metaclassAspectMapping.getMetaclass(), group)){
				filteredMapping.add(metaclassAspectMapping);
			}
		}
		return filteredMapping;
	}

	/**
	 * Returns true if the given classifier belongs to the given ecore group. 
	 * @param metaclass
	 * @param group
	 * @return
	 */
	private boolean belongsTo(EClassifier metaclass, EcoreGroup group) {
		for (EcoreVertex vertex : group.getVertex()) {
			if(vertex.getClassifier().getName().equals(metaclass.getName()))
				return true;
		}
		return false;
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
	 * Returns the collection of all the aspects associated to the given set of languages.
	 * @param languages
	 * @return
	 */
	private ArrayList<Aspect> findAllAspects(ArrayList<Language> languages) {
		ArrayList<Aspect> allAspects = new ArrayList<Aspect>();
		for (Language language : languages) {
			allAspects.addAll(language.getSemantics());
		}
		return allAspects;
	}
	
	/**
	 * 
	 * @param aspects
	 * @param mapping 
	 * @return
	 */
	private void findAspectMapping(
			ArrayList<Language> languages, ArrayList<MetaclassAspectMapping> mapping,
			ConceptComparison conceptComparison) {
		
		for (Language language : languages) {
			for (Aspect aspect : language.getSemantics()) {
				MetaclassAspectMapping map = getMappingByClassifier(aspect.getAspectedClass(), mapping, conceptComparison);
				if(map == null){
					map = new MetaclassAspectMapping(aspect.getAspectedClass(), aspect, language);
					map.getAspects().get(0).getLanguagesObjectList().add(language);
					mapping.add(map);
				}else{
					AspectLanguageMapping newMap = new AspectLanguageMapping(aspect, language);
					newMap.getLanguagesObjectList().add(language);
					map.getAspects().add(newMap);
				}
			}
		}
	}
	
	public MetaclassAspectMapping getMappingByClassifier(EClassifier classifier, 
			ArrayList<MetaclassAspectMapping> mapping, ConceptComparison conceptComparison){
		for (MetaclassAspectMapping metaclassAspectMapping : mapping) {
			if(conceptComparison.equals( metaclassAspectMapping.getMetaclass(), classifier))
				return metaclassAspectMapping;
		}
		return null;
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
	
	private AspectLanguageMapping findAspectMapping(Aspect aspect,
			MetaclassAspectMapping newEntry, MethodComparison methodComparison) {
		for (AspectLanguageMapping aspectI : newEntry.getAspects()) {
			if(aspectsEqual(aspect, aspectI.getAspect(), methodComparison)){
				return aspectI;
			}
		}
		return null;
	}

	private boolean aspectsEqual(Aspect rightAspect, Aspect leftAspect,
			MethodComparison methodComparison) {
		boolean nameEqual = rightAspect.getAspectTypeRef().getType().getSimpleName().equals(
				leftAspect.getAspectTypeRef().getType().getSimpleName());
		
		ArrayList<JvmOperation> rightOperations = collectJvmOperations(rightAspect);
		ArrayList<JvmOperation> leftOperations = collectJvmOperations(leftAspect);
		
		boolean operationsEqual = true;
		for (JvmOperation jvmOperation : rightOperations) {
			if(!operationExists(jvmOperation, leftOperations, methodComparison)){
				operationsEqual = false;
				break;
			}
		}
		boolean operationsSizeEqual = rightOperations.size() == leftOperations.size();
		return nameEqual && operationsEqual && operationsSizeEqual;
	}

	private boolean operationExists(JvmOperation jvmOperation,
			ArrayList<JvmOperation> leftOperations,
			MethodComparison methodComparison) {
		for (JvmOperation jvmOperationJ : leftOperations) {
			if(methodComparison.equal(jvmOperation, jvmOperationJ))
				return true;
		}
		return false;
	}

	private ArrayList<JvmOperation> collectJvmOperations(Aspect aspect) {
		ArrayList<JvmOperation> jvmOperations = new ArrayList<JvmOperation>();
		for (EObject eObject : aspect.getAspectTypeRef().getType().eContents()) {
			if(eObject instanceof JvmOperation){
				JvmOperation operation = (JvmOperation) eObject;
				jvmOperations.add(operation);
			}
		}
		return jvmOperations;
	}
}
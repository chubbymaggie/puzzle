package fr.inria.diverse.puzzle.breaker.popup.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import fr.inria.diverse.k3.sle.common.utils.EcoreCloningServices;
import fr.inria.diverse.k3.sle.common.utils.ModelUtils;
import fr.inria.diverse.k3.sle.common.utils.ProjectManagementServices;
import fr.inria.diverse.puzzle.breaker.vos.ConceptMemberVO;
import fr.inria.diverse.puzzle.breaker.vos.ConceptMembersGroupVO;
import fr.inria.diverse.puzzle.breaker.vos.ModuleConceptsVO;

/**
 * 
 * @author dmendeza
 *
 */
public class BreakDownFamilyImpl {
	
	private static BreakDownFamilyImpl instance;
	
	private BreakDownFamilyImpl(){}
	
	public static BreakDownFamilyImpl getInstance(){
		if(instance == null)
			instance = new BreakDownFamilyImpl();
		return instance;
	}
	
	public void breakDownFamily(ArrayList<IResource> selectedResources) throws CoreException{
		ArrayList<EPackage> ePackages = new ArrayList<EPackage>();
		for (IResource iResource : selectedResources) {
			IFile currentFile = (IFile) iResource;
			EPackage currentMetamodel = ModelUtils.loadEcoreFile(currentFile.getLocation().toString());
			ePackages.add(currentMetamodel);
		}
		
		// Step 1: Scan the metamodels creating the concept-member list.
		ArrayList<ConceptMemberVO> conceptMemberList = new ArrayList<ConceptMemberVO>();
		for (EPackage ePackage : ePackages) {
			this.fillConceptMemberList(conceptMemberList, ePackage, ePackage.getName());
		}
		
		System.out.println("Step 1");
		for (ConceptMemberVO conceptMemberVO : conceptMemberList) {
			System.out.println(conceptMemberVO);
		}
		
		ArrayList<ConceptMembersGroupVO> conceptMemberGroupList = new ArrayList<ConceptMembersGroupVO>();
		for (ConceptMemberVO conceptMemberVO : conceptMemberList) {
			ConceptMembersGroupVO conceptMemberGroupLegacy = getConceptMemberGroup(conceptMemberGroupList, conceptMemberVO);
			if(conceptMemberGroupLegacy == null){
				ConceptMembersGroupVO conceptMemberGroupVO = new ConceptMembersGroupVO(conceptMemberVO.getConcept());
				conceptMemberGroupVO.getMemberGroup().add(conceptMemberVO.getMemberName());
				conceptMemberGroupList.add(conceptMemberGroupVO);
			}else{
				conceptMemberGroupLegacy.getMemberGroup().add(conceptMemberVO.getMemberName());
			}
		}
		System.out.println("Step 2");
		for (ConceptMembersGroupVO conceptMembersGroupVO : conceptMemberGroupList) {
			System.out.println(conceptMembersGroupVO);
		}
		
		ArrayList<ModuleConceptsVO> moduleConceptsList = new ArrayList<ModuleConceptsVO>();
		int i = 1;
		for (ConceptMembersGroupVO conceptMembersGroupVO : conceptMemberGroupList) {
			ModuleConceptsVO legacyModule = getLegacyFeature(moduleConceptsList, conceptMembersGroupVO);
			if(legacyModule == null){
				ModuleConceptsVO newModule = new ModuleConceptsVO("module" + i, conceptMembersGroupVO.getMemberGroup().toString());
				newModule.getConcepts().add(conceptMembersGroupVO.getConcept());
				moduleConceptsList.add(newModule);
				i++;
			}else{
				legacyModule.getConcepts().add(conceptMembersGroupVO.getConcept());
			}
		}
		
		System.out.println("Step 3");
		for (ModuleConceptsVO moduleConceptsVO : moduleConceptsList) {
			System.out.println(moduleConceptsVO);
		}
		
		buildModules(moduleConceptsList);
	}

	private void fillConceptMemberList(
			ArrayList<ConceptMemberVO> conceptMemberList, EPackage ePackage, String memberName) {
		for (EClassifier concept : ePackage.getEClassifiers()) {
			ConceptMemberVO conceptMember = new ConceptMemberVO(concept, memberName);
			conceptMemberList.add(conceptMember);
		}
		for (EPackage subPackage : ePackage.getESubpackages()) {
			this.fillConceptMemberList(conceptMemberList, subPackage, memberName);
		}
	}
	
	private ConceptMembersGroupVO getConceptMemberGroup(
			ArrayList<ConceptMembersGroupVO> conceptMemberGroupList,
			ConceptMemberVO conceptMemberVO) {
		for (ConceptMembersGroupVO conceptMembersGroupVO : conceptMemberGroupList) {
			if(conceptMembersGroupVO.getConcept().getName().equals(conceptMemberVO.getConcept().getName()))
				return conceptMembersGroupVO;
		}
		return null;
	}
	
	private ModuleConceptsVO getLegacyFeature(
			ArrayList<ModuleConceptsVO> featureConceptsList,
			ConceptMembersGroupVO conceptMembersGroupVO) {
		for (ModuleConceptsVO featureConceptsVO : featureConceptsList) {
			if(featureConceptsVO.getMembers().equals(conceptMembersGroupVO.getMemberGroup().toString()))
				return featureConceptsVO;
		}
		return null;
	}
	
	private void buildModules(ArrayList<ModuleConceptsVO> moduleConceptsList) throws CoreException {
		for (ModuleConceptsVO moduleConceptsVO : moduleConceptsList) {
			// Create the module project with the folders.
			IProject moduleProject = ProjectManagementServices.createEclipseProject("fr.inria.diverse.examples.breaking." + moduleConceptsVO.getModuleName().trim());
			String modelsFolderPath = ProjectManagementServices.createFolderByName(moduleProject, "models");
			
			// Build the module metamodel with the required interface.
			EPackage moduleEPackage = this.createEPackageByModule(moduleConceptsVO);
			
			// Serialize the module metamodel in the corresponding project. 
			ModelUtils.saveEcoreFile(modelsFolderPath + "/" + moduleConceptsVO.getModuleName() + ".ecore", moduleEPackage);
			
			// Create the genmodel and generate the code of the module.
			
			ProjectManagementServices.refreshProject(moduleProject);
		}
	}

	/**
	 * Creates a metamodel by module taking into consideration the corresponding dependencies with other modules
	 * by establishing the required interfaces.
	 * @param moduleConceptsVO
	 * @return
	 */
	private EPackage createEPackageByModule(ModuleConceptsVO moduleConceptsVO) {
		EcoreCloningServices.getInstance().resetClonedClassifiers();
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
		newPackage.setName(moduleConceptsVO.getModuleName().trim());
		newPackage.setNsPrefix(moduleConceptsVO.getModuleName().trim());
		newPackage.setNsURI(moduleConceptsVO.getModuleName().trim());
		
		for (EClassifier eClassifier : moduleConceptsVO.getConcepts()) {
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
		EcoreCloningServices.getInstance().resolveInterfaceReferences(newPackage);
		EcoreCloningServices.getInstance().resolveLocalSuperTypes(newPackage);
		return newPackage;
	}
}

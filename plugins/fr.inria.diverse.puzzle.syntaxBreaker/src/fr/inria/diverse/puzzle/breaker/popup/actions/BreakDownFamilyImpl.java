package fr.inria.diverse.puzzle.breaker.popup.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import fr.inria.diverse.k3.sle.common.comparisonOperators.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.NamingConceptComparison;
import fr.inria.diverse.k3.sle.common.tuples.EcoreGraph;
import fr.inria.diverse.k3.sle.common.tuples.EcoreVertex;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMembers;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;
import fr.inria.diverse.k3.sle.common.utils.EcoreCloningServices;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.k3.sle.common.utils.MelangeServices;
import fr.inria.diverse.k3.sle.common.utils.ModelUtils;
import fr.inria.diverse.k3.sle.common.utils.ProjectManagementServices;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * 
 * @author David Mendez Acuna
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
	
	public void breakDownFamily(ArrayList<Language> languages) throws Exception{
		ConceptComparison conceptComparisonOperator = NamingConceptComparison.class.newInstance();
		ArrayList<EPackage> ePackages = MelangeServices.getEPackagesByALanguagesList(languages);
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(ePackages);
		ArrayList<TupleConceptMembers> conceptMembersList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, conceptComparisonOperator);
		ArrayList<TupleMembersConcepts> membersConceptList = FamiliesServices.getInstance().getMembersGroupVsConceptVOList(conceptMembersList);
		EcoreGraph dependenciesGraph = new EcoreGraph(membersConceptList, conceptComparisonOperator);
		dependenciesGraph.groupGraphByFamilyMembership(membersConceptList, conceptComparisonOperator);
		buildModules(dependenciesGraph);
	}

	private void buildModules(EcoreGraph ecoreGraph) throws CoreException {
		for (ArrayList<EcoreVertex> group : ecoreGraph.getGroups()) {
			// Build the module metamodel with the required interface.
			EPackage moduleEPackage = this.createEPackageByModule(group);

			// Create the module project with the folders.
			IProject moduleProject = ProjectManagementServices.createEclipseProject("fr.inria.diverse.examples.breaking." + group.get(0).getClassifier().getName().trim());
			String modelsFolderPath = ProjectManagementServices.createFolderByName(moduleProject, "models");
						
			// Serialize the module metamodel in the corresponding project. 
			ModelUtils.saveEcoreFile(modelsFolderPath + "/" + group.get(0).getClassifier().getName() + ".ecore", moduleEPackage);
			
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
	private EPackage createEPackageByModule(ArrayList<EcoreVertex> group) {
		EcoreCloningServices.getInstance().resetClonedClassifiers();
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
		newPackage.setName(group.get(0).getClassifier().getName().trim());
		newPackage.setNsPrefix(group.get(0).getClassifier().getName().trim());
		newPackage.setNsURI(group.get(0).getClassifier().getName().trim());
		
		for (EcoreVertex vertex : group) {
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
		EcoreCloningServices.getInstance().resolveInterfaceReferences(newPackage);
		EcoreCloningServices.getInstance().resolveLocalSuperTypes(newPackage);
		return newPackage;
	}
}
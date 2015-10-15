package fr.inria.diverse.puzzle.metrics;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.DeepConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.NamingConceptComparison;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.melange.metamodel.melange.MelangeFactory;
import fr.inria.diverse.melange.metamodel.melange.Metamodel;
import fr.inria.diverse.puzzle.metrics.chartMetrics.SizeOfCommonality;

public class EmpiricalStudy {

	// ------------------------------------------------
	// Attributes
	// ------------------------------------------------
	
	private String dataFolder = "temp";
	private ArrayList<Language> languages;
	
	// ------------------------------------------------
	// Loading scenarios
	// ------------------------------------------------
	
	@Before
	public void loadScenario(){
		languages = new ArrayList<Language>();
		
		File dataFolderObject = new File(dataFolder);
		
		for (File file : dataFolderObject.listFiles()) {
			if(file.getName().endsWith("ecore")){
				System.out.println("file.getName(): " + file.getName());
				Language currentLanguage = MelangeFactory.eINSTANCE.createLanguage();
				Metamodel currentMetamodel = MelangeFactory.eINSTANCE.createMetamodel();
				currentMetamodel.setEcoreUri(file.getAbsolutePath());
				currentLanguage.setSyntax(currentMetamodel);
				currentLanguage.setName(file.getName());
				languages.add(currentLanguage);
			}
		}
	}

	// ------------------------------------------------
	// Test cases
	// ------------------------------------------------

	@Test
	public void lunchEmpiricalStudy() throws Exception{
		ConceptComparison comparisonOperator = new NamingConceptComparison();
		
//		ArrayList<Language> evaluatedLanguages = new ArrayList<Language>();
		
		ArrayList<FamilyDSLs> families = new ArrayList<FamilyDSLs>();
		int count = 0;
		for (Language language : languages) {
			
//			if(!evaluatedLanguages.contains(language)){
				ArrayList<Language> languagesWithCommonalities = this.findLanguagesWithCommonalities(language, comparisonOperator);
				
				// para hacer experimentos parciales. 
				if(languagesWithCommonalities.size() > 1 && languagesWithCommonalities.size() < 8){
					System.out.println();
					System.out.print("searching a family in a set of: " + languagesWithCommonalities.size() + " languages [" );
					for (Language language2 : languagesWithCommonalities) {
						System.out.print(language2.getName() + ", ");
					}
					System.out.print("}");
					this.identifyFamilies(families, languagesWithCommonalities, comparisonOperator);
					count++;
				}
//				evaluatedLanguages.addAll(languagesWithCommonalities);
//			}
		}
		
		System.out.println("Evaluations: " + count);
		System.out.println();
		for (FamilyDSLs familyDSLs : families) {
			System.out.print("family { ");
			System.out.print(" Size: " + familyDSLs.getMembers().size() + " - SoC: " + familyDSLs.getSoC() + " -> ");
			for (Language member : familyDSLs.getMembers()) {
				System.out.print(member.getName() + ", ");
			}
			System.out.print("}");
			System.out.println();
		}
		
		System.out.println("amount of families: " + families.size());
		System.out.println();
	}
	
	private ArrayList<Language> findLanguagesWithCommonalities(Language language, ConceptComparison comparisonOperator) throws Exception {
		ArrayList<Language> commonalities = new ArrayList<Language>();
		for (Language currentLanguage : languages) {
			if(!currentLanguage.getName().equals(language.getName())){
				ArrayList<Language> source = new ArrayList<Language>();
				source.add(language);
				source.add(currentLanguage);
				int SoC = SizeOfCommonality.evaluateForSyntax(source, comparisonOperator);
				if(SoC >= 4){
					commonalities.add(currentLanguage);
				}
			}
		}
		if(commonalities.size() != 0)
			commonalities.add(language);
		
		return commonalities;
	}

	
	private void identifyFamilies(ArrayList<FamilyDSLs> families, 
			ArrayList<Language> source, ConceptComparison comparisonOperator) throws Exception{
		if(source.size() > 1 && !this.contains(families, source)){
			int SoC = SizeOfCommonality.evaluateForSyntax(source, comparisonOperator);
			if(SoC > 0){
				FamilyDSLs family = new FamilyDSLs();
				family.setSoC(SoC);
				family.getMembers().addAll(source);
				families.add(family);
			}else{
				
				for (Language languageI : source) {
					ArrayList<Language> newSource = new ArrayList<Language>();
					for (Language languageJ : source) {
						if(!languageI.getName().equals(languageJ.getName())){
							newSource.add(languageJ);
						}
					}
					if(newSource.size() > 1)
						identifyFamilies(families, newSource, comparisonOperator);
				}
			}
		}
	}
	
	private boolean contains(ArrayList<FamilyDSLs> families, 
			ArrayList<Language> language){
		for (FamilyDSLs family : families) {
			if(family.getMembers().containsAll(language))
				return true;
		}
		return false;
	}
	
}

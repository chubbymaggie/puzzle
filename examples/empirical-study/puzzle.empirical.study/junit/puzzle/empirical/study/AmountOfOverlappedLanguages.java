package puzzle.empirical.study;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.DeepConceptComparison;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.melange.metamodel.melange.MelangeFactory;
import fr.inria.diverse.melange.metamodel.melange.Metamodel;
import fr.inria.diverse.puzzle.metrics.specialCharts.SyntacticNamingVennDiagram;

public class AmountOfOverlappedLanguages {

	// ------------------------------------------------
	// Attributes
	// ------------------------------------------------
	
	private String dataFolder = "githubmetamodels";
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
	public void computeOverlappingAverageSize() throws Exception{
		ConceptComparison conceptComparisonOperator = new DeepConceptComparison();
		SyntacticNamingVennDiagram metrics = new SyntacticNamingVennDiagram();
		int[][] theMatrix = metrics.getCommonalitiesMatrix(languages, conceptComparisonOperator);
		
		int mmsum = 0;
		
		for (int i = 0; i < theMatrix.length; i++) {
			int count = 0;
			for (int j = 0; j < theMatrix[0].length; j++) {
				if(theMatrix[i][j] > 0){
					count ++;
				}
			}
			
			if(count > 0){
				mmsum++;
			}
		}
		
		System.out.println("Overlapped Languages (SE): " + mmsum);
		System.out.println("Amount of Languages (S): " + theMatrix[0].length);
	}
}

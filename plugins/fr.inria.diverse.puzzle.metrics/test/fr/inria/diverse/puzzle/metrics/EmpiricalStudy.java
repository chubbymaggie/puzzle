package fr.inria.diverse.puzzle.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.DeepConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.NamingConceptComparison;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.melange.metamodel.melange.MelangeFactory;
import fr.inria.diverse.melange.metamodel.melange.Metamodel;
import fr.inria.diverse.puzzle.metrics.chartMetrics.SizeOfCommonality;
import fr.inria.diverse.puzzle.metrics.managers.FamilysMetricManager;
import fr.inria.diverse.puzzle.metrics.specialCharts.SpecialFamilySyntacticChart;
import fr.inria.diverse.puzzle.metrics.specialCharts.SyntacticNamingVennDiagram;

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

	@Ignore
	@Test
	public void generateMelangeScript() throws IOException{
		String script = "package bigFamily\n\n";
		for (Language language : languages) {
			script += " language " + language.getName().replace(".ecore", "") + " {\n";
			script += "     syntax \"" + language.getSyntax().getEcoreUri() + "\"\n";
			script += "     exactType " + language.getName().replace(".ecore", "") + "MT\n";
			script += " }\n\n";
		}
		
		File file = new File("./scripts/bigScript.melange");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(script);
		bw.close();
	}
	
	@Ignore
	@Test
	public void testIntersectionPairs(){
		SyntacticNamingVennDiagram syntacticVennDiagram = new SyntacticNamingVennDiagram();
		ArrayList<ArrayList<Language>> pairs = syntacticVennDiagram.intersectionPairs(languages);
		for (ArrayList<Language> arrayList : pairs) {
			for (Language ePackage : arrayList) {
				System.out.print(ePackage.getName() + ",");
			}
			System.out.println();
		}
	}
	
	@Ignore
	@Test
	public void drawHugeVennDiagram() throws Exception{
		ConceptComparison conceptComparisonOperator = new DeepConceptComparison();

		File syntacticVennData = new File("./libVenn/syntacticVennData.jsonp" );
		if(!syntacticVennData.exists())
			syntacticVennData.createNewFile();
		
		PrintWriter out = new PrintWriter( syntacticVennData );
		SpecialFamilySyntacticChart syntacticVennDiagram = new SyntacticNamingVennDiagram();
        out.print(syntacticVennDiagram.getVariablesDeclaration(languages, conceptComparisonOperator));
        out.close();
        
		FamilysMetricManager familysMetric = new FamilysMetricManager(null);
		familysMetric.createReport1LargeAnalysis(languages);
	}
	
//	@Ignore
	@Test
	public void computeHistograms() throws Exception{
		ConceptComparison conceptComparisonOperator = new NamingConceptComparison();

		SyntacticNamingVennDiagram metrics = new SyntacticNamingVennDiagram();
		int[][] theMatrix = metrics.getCommonalitiesMatrix(languages, conceptComparisonOperator);
		Hashtable<Integer, Integer> histogramByConstructs = metrics.computeConstructsCommonality(theMatrix);
		
		int max = -1;
		for (int i = 0; i < theMatrix.length; i++) {
			int current = histogramByConstructs.get(i);
			System.out.println(i + " - " + histogramByConstructs.get(i));
			if(current > max)
				max = current;
		}
		
		System.out.println("max: " + max);
		
		ArrayList<Integer> histogram = new ArrayList<Integer>();
		
		Iterator<Integer> it = histogramByConstructs.keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			Integer currentKey = (Integer) it.next();
			Integer currentValue = histogramByConstructs.get(currentKey);
			if(currentValue == 0){
				count ++;
			}
		}
		histogram.add(new Integer(count));
		
		int maxI = 0;
		for (int i = 1; i <= max; i+=100) {
			it = histogramByConstructs.keySet().iterator();
			count = 0;
			while (it.hasNext()) {
				Integer currentKey = (Integer) it.next();
				Integer currentValue = histogramByConstructs.get(currentKey);
				int proxI = i + 100;
				if(currentValue >= i && currentValue < proxI){
					count ++;
				}
			}
			histogram.add(new Integer(count));
			maxI = i;
		}
		
		it = histogramByConstructs.keySet().iterator();
		count = 0;
		while (it.hasNext()) {
			Integer currentKey = (Integer) it.next();
			Integer currentValue = histogramByConstructs.get(currentKey);
			if(currentValue >= maxI && currentValue <= max){
				count ++;
			}
		}
		histogram.add(new Integer(count));
		
		
		for (int i = 0; i < histogram.size(); i++) {
			System.out.println(i*100 + "," + histogram.get(i));
		}
	}
	
	@Ignore
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
				if(languagesWithCommonalities.size() > 1 && languagesWithCommonalities.size() <= 10){
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

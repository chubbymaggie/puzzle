package fr.inria.diverse.puzzle.metrics.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.metrics.chartMetrics.MaintananceCosts;
import fr.inria.diverse.puzzle.metrics.chartMetrics.PairwiseRelationshipRatio;
import fr.inria.diverse.puzzle.metrics.specialCharts.DependenciesGraph;
import fr.inria.diverse.puzzle.metrics.specialCharts.FamilyMembershipGraph;
import fr.inria.diverse.puzzle.metrics.specialCharts.SemanticalVariabilityTree;
import fr.inria.diverse.puzzle.metrics.specialCharts.TarjansAlgorithmGraph;

public class FamilysMetricManager extends MetricsManager {

	public FamilysMetricManager(IProject project) throws Exception{
		super(project);
	}
	
	public void createReport1FamilysShape(ArrayList<Language> languages) throws URISyntaxException, IOException{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-1-FamilysShape.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-1-FamilysShape.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	public void createReport2CostSaving(ArrayList<Language> languages) throws URISyntaxException, IOException{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-2-CostSavingsMetrics.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-2-CostSavingsMetrics.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	public void createReport2CostSavingData(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator, GraphPartition graphPartition) throws Exception{
        File fileReport = new File(project.getLocation().toString() + "/lib/costSavingMetrics.js" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print((new MaintananceCosts()).getVariablesDeclaration(languages, conceptComparisonOperator, methodComparisonOperator, graphPartition));
		outRileReport.close();
	}
	
	public void createReport3ReuseMetrics(ArrayList<Language> languages) throws URISyntaxException, IOException{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-3-ReuseMetrics.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        content = content.replace("<!-- Coucou! REPLACE ME WITH THE CORRECT PATTERN -->", (new PairwiseRelationshipRatio()).getTables(languages));
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-3-ReuseMetrics.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	/**
	 * Creates the file the the instructions to draw the graph that shows the graph grouping using the family membership criterion.
	 * @param targetProject. The project where the file should be created.
	 * @param languages. The set of languages belonging to the family under study.
	 * @throws Exception
	 */
	public void createDependenciesGraph(ArrayList<Language> languages) throws Exception{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-4a-DependenciesGraph.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-4a-DependenciesGraph.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	/**
	 * Creates the file with the data input of the graph that shows the graph grouping using the family membership criterion.
	 * @param targetProject. The project where the file should be created.
	 * @param languages. The set of languages belonging to the family under study.
	 * @param conceptComparisonOperator
	 * @param methodComparisonOperator
	 * @throws Exception
	 */
	public void createDependenciesGraphData(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator) throws Exception{
		File generalMetrics = new File(project.getLocation().toString() + "/lib/dependenciesGraph.js" );
		if(!generalMetrics.exists())
			generalMetrics.createNewFile();
		PrintWriter outMetrics = new PrintWriter( generalMetrics );
		outMetrics.print(DependenciesGraph.getVariablesDeclaration(languages, conceptComparisonOperator));
		outMetrics.close();
	}
	
	
	/**
	 * Creates the file the the instructions to draw the graph that shows the graph grouping using the family membership criterion.
	 * @param targetProject. The project where the file should be created.
	 * @param languages. The set of languages belonging to the family under study.
	 * @throws Exception
	 */
	public void createFamilyMembershipGraph(ArrayList<Language> languages) throws Exception{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-4b-FamilyMembershipGraph.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-4b-FamilyMembershipGraph.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	/**
	 * Creates the file with the data input of the graph that shows the graph grouping using the family membership criterion.
	 * @param targetProject. The project where the file should be created.
	 * @param languages. The set of languages belonging to the family under study.
	 * @param conceptComparisonOperator
	 * @param methodComparisonOperator
	 * @throws Exception
	 */
	public void createFamilyMembershipGraphData(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator) throws Exception{
		File generalMetrics = new File(project.getLocation().toString() + "/lib/graph.js" );
		if(!generalMetrics.exists())
			generalMetrics.createNewFile();
		PrintWriter outMetrics = new PrintWriter( generalMetrics );
		outMetrics.print(FamilyMembershipGraph.getVariablesDeclaration(languages, conceptComparisonOperator));
		outMetrics.close();
	}
	
	/**
	 * Creates the file the the instructions to draw the graph that shows the graph grouping using the family membership criterion.
	 * @param targetProject. The project where the file should be created.
	 * @param languages. The set of languages belonging to the family under study.
	 * @throws Exception
	 */
	public void createTarjansGraph(ArrayList<Language> languages) throws Exception{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-4c-TarjansGraph.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-4c-TarjansGraph.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	/**
	 * Creates the file with the data input of the graph that shows the graph grouping using the family membership criterion.
	 * @param targetProject. The project where the file should be created.
	 * @param languages. The set of languages belonging to the family under study.
	 * @param conceptComparisonOperator
	 * @param methodComparisonOperator
	 * @throws Exception
	 */
	public void createTarjansGraphData(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator) throws Exception{
		File generalMetrics = new File(project.getLocation().toString() + "/lib/tarjansGraph.js" );
		if(!generalMetrics.exists())
			generalMetrics.createNewFile();
		PrintWriter outMetrics = new PrintWriter( generalMetrics );
		outMetrics.print(TarjansAlgorithmGraph.getVariablesDeclaration(languages, conceptComparisonOperator));
		outMetrics.close();
	}
	
	public void copyAnalysisSemanticsData(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator) throws IOException{
		File generalMetrics = new File(project.getLocation().toString() + "/lib/semanticAnalysis.js" );
		if(!generalMetrics.exists())
			generalMetrics.createNewFile();
		PrintWriter outMetrics = new PrintWriter( generalMetrics );
		outMetrics.print(SemanticalVariabilityTree.getVariablesDeclaration(languages, conceptComparisonOperator, methodComparisonOperator));
		outMetrics.close();
	}
	
	public void copyAnalysisSemantics(ArrayList<Language> languages) throws URISyntaxException, IOException{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-5-SemanticVariability.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-5-SemanticVariability.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
}
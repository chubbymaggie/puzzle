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
import fr.inria.diverse.puzzle.metrics.componentsMetrics.CouplingMetricsTable;

public class ProductLinesMetricManager extends MetricsManager {

	public ProductLinesMetricManager(IProject project) throws Exception{
		super(project);
	}

	public void createReport1ProductLineCoupling(ArrayList<Language> languages) throws URISyntaxException, IOException{
		URL path = Platform.getBundle("fr.inria.diverse.puzzle.metrics").getEntry("/data/Report-1-Coupling.html");
        File file = new File(FileLocator.resolve(path).toURI());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String currentLine = br.readLine();
        while(currentLine != null){
        	content += currentLine + "\n";
        	currentLine = br.readLine();
        }
        br.close();
        
        File fileReport = new File(project.getLocation().toString() + "/Report-1-Coupling.html" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print(content);
		outRileReport.close();
	}
	
	public void createReport1ProductLineCouplingData(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator, GraphPartition graphPartition) throws Exception{
        File fileReport = new File(project.getLocation().toString() + "/lib/coupling.js" );
		if(!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter( fileReport );
		outRileReport.print((new CouplingMetricsTable()).getVariablesDeclaration(languages, conceptComparisonOperator, methodComparisonOperator, graphPartition));
		outRileReport.close();
	}
}
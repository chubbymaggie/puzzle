package fr.inria.diverse.puzzle.variabilityinferer.auxiliar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.k3.sle.common.utils.EcoreQueries;
import fr.inria.diverse.k3.sle.common.utils.MelangeServices;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Generates PCMs in diverse formats for the variability model inferrers. 
 * @author David Mendez-Acuna
 */
public class PCMsGenerator {

	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------
	
	public static int OPEN_COMPARE_FORMAT = 1;
	
	public static int FAMA_FORMAT = 2;
	
	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------
	
	private static PCMsGenerator instance;
	
	private Hashtable<String, String> modulesNameVsFamasIndex;
	
	private Hashtable<String, String> famasIndexVsModulesName;
	
	// --------------------------------------------------------
	// Constructor and singleton
	// --------------------------------------------------------
	
	private PCMsGenerator(){
		modulesNameVsFamasIndex = new Hashtable<String, String>();
		famasIndexVsModulesName = new Hashtable<String, String>();
	}
	
	public static PCMsGenerator getInstance(){
		if(instance == null)
			instance = new PCMsGenerator();
		return instance;
	}
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------
	
	/**
	 * Generates a PCM for the modularization graph in the parameter and according to the desired format. 
	 * @param properties Synthesis properties needed for the computation of the PCM. 
	 * @param modularizationGraph Modularization graph containing the language constructs.
	 * @param format Format in which the PCM should be described. 
	 * @throws Exception Throws an exception if the desired format is not supported. 
	 */
	public String generatePCM(SynthesisProperties properties, ArrayList<Language> languages, EcoreGraph modularizationGraph, int format) throws Exception{
		if(format == OPEN_COMPARE_FORMAT)
			return generatePCMOpenCompareFormat(properties, languages, modularizationGraph);
		else if(format == FAMA_FORMAT)
			return generatePCMFamaFormat(properties, languages, modularizationGraph);
		else
			throw new Exception("PCM format not supported");
	}
	
	/**
	 * Generates a PCM in the format accepted by open compare. 
	 * @param properties Synthesis properties needed for the computation of the PCM. 
	 * @param modularizationGraph Modularization graph containing the language constructs. 
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	private String generatePCMOpenCompareFormat(SynthesisProperties properties, ArrayList<Language> languages, EcoreGraph modularizationGraph) throws Exception{
		String pcm = "\"Product\"";
		for (int i = 0; i < modularizationGraph.getGroups().size(); i++) {
			ArrayList<EcoreVertex> module = modularizationGraph.getGroups().get(i).getVertex();
			String moduleName = EcoreGraph.getLanguageModuleName(module);
			// get name by module. 
			pcm += ",\"" + moduleName + "\"";
		}
		pcm += "\n";
		
		for (int i = 0; i < languages.size(); i++) {
			Language language = languages.get(i);
			pcm += "\"" + language.getName() + "\",";
			boolean first = true;
			for (int j = 0; j < modularizationGraph.getGroups().size(); j++) {
				if(!first) pcm += ",";
				ArrayList<EcoreVertex> module = modularizationGraph.getGroups().get(j).getVertex();
				boolean contained = moduleContainedInLanguage(properties.getConceptComparisonOperator(), language, module);
				if(contained)
					pcm += "\"YES\"";
				else
					pcm += "\"NO\"";
				first = false;
			}
			pcm += "\n";
		}
		return pcm;
	}
	
	/**
	 * Generates a PCM in the format accepted by FAMA. 
	 * @param properties Synthesis properties needed for the computation of the PCM. 
	 * @param modularizationGraph Modularization graph containing the language constructs. 
	 * @return
	 */
	private String generatePCMFamaFormat(SynthesisProperties properties, ArrayList<Language> languages, EcoreGraph modularizationGraph){
		this.initializeModulesAndFamasIndexMappings(properties, languages, modularizationGraph);
		String pcm = "";
		for (int i = 0; i < languages.size(); i++) {
			pcm += "root" + ";";
			Language language = languages.get(i);
			for (int j = 0; j < modularizationGraph.getGroups().size(); j++) {
				ArrayList<EcoreVertex> module = modularizationGraph.getGroups().get(j).getVertex();
				boolean contained = moduleContainedInLanguage(properties.getConceptComparisonOperator(), language, module);
				String moduleName = EcoreGraph.getLanguageModuleName(module);
				if(contained){
					pcm += this.modulesNameVsFamasIndex.get(moduleName) + ";";
				}
			}
			pcm += "\n";
		}
		return pcm;
	}
	
	private void initializeModulesAndFamasIndexMappings(SynthesisProperties properties, ArrayList<Language> languages, EcoreGraph modularizationGraph){
		int famasIndex = 1;
		for (int i = 0; i < languages.size(); i++) {
			Language language = languages.get(i);
			for (int j = 0; j < modularizationGraph.getGroups().size(); j++) {
				ArrayList<EcoreVertex> module = modularizationGraph.getGroups().get(j).getVertex();
				boolean contained = moduleContainedInLanguage(properties.getConceptComparisonOperator(), language, module);
				if(contained){
					String moduleName = EcoreGraph.getLanguageModuleName(module);
					String famasIndexString = "F" + famasIndex;
					if(modulesNameVsFamasIndex.get(moduleName) == null
							&& famasIndexVsModulesName.get(famasIndexString) == null){
						modulesNameVsFamasIndex.put(moduleName, famasIndexString);
						famasIndexVsModulesName.put(famasIndexString, moduleName);
						famasIndex ++;
					}
				}
			}
		}
	}
	
	/**
	 * Returns the mapping between the real modules names of the language modules and the
	 * indexed names required by FAMA. 
	 * @return
	 */
	public Hashtable<String, String> getModulesNameVsFamasIndex() {
		return modulesNameVsFamasIndex;
	}
	
	/**
	 * Returns the mapping between the indexed names required by FAMA and
	 * the real modules names of the language modules. 
	 * @return
	 */
	public Hashtable<String, String> getFamasIndexVsModulesName() {
		return famasIndexVsModulesName;
	}
	
	// --------------------------------------------------------
	// Auxiliary Methods
	// --------------------------------------------------------

	/**
	 * Indicates if a module is contained in a given language.
	 * @param conceptComparison. Concept comparison operator. 
	 * @param language. Language under study.
	 * @param module. Module under study.
	 * @return
	 * 
	 * TODO: Move this method to MelangeServices?
	 */
	private boolean moduleContainedInLanguage(ConceptComparison conceptComparison, Language language, ArrayList<EcoreVertex> module){
		EPackage ePackage = MelangeServices.getEPackageFromLanguage(language);
		for (EcoreVertex ecoreVertex : module) {
			EClassifier eClassifier = ecoreVertex.getClassifier();
			if(EcoreQueries.searchEClassifierByComparisonOperator(ePackage, eClassifier, conceptComparison) == null){
				return false;
			}
		}
		return true;
	}
}
package fr.inria.diverse.puzzle.validator.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.osgi.framework.Bundle;

import fr.inria.diverse.puzzle.match.vo.MatchingDiagnostic;
import fr.inria.diverse.puzzle.match.vo.MatchingDiagnosticItem;
import fr.inria.diverse.puzzle.validator.vos.PuzzleDiagnosis;
import fr.inria.diverse.puzzle.validator.vos.PuzzleDiagnosisItem;
import fr.inria.diverse.puzzle.validator.vos.PuzzleDiagnosisItemKind;

/**
 * Implementation for the command: validate language modules composability
 * @author David Mendez-Acuna
 *
 */
public class ValidatorImpl {

	// -----------------------------------------------------
	// Attributes
	// -----------------------------------------------------

	private static ValidatorImpl instance;
	private Hashtable<String, Integer> dataTypes;
	private int[][] compatibilityMatrix;

	// -----------------------------------------------------
	// Constructor and Singleton
	// -----------------------------------------------------

	private ValidatorImpl() {
		dataTypes = loadDataTypes();
		compatibilityMatrix = loadCompatibilityMatrix();
		printCompatibilityMatrix();
	}

	public static ValidatorImpl getInstance() {
		if (instance == null)
			instance = new ValidatorImpl();
		return instance;
	}

	// -----------------------------------------------------
	// Methods
	// -----------------------------------------------------
	
	/**
	 * Checks the compatibility of two interfaces. Returns a diagnosis with the
	 * incompatibilities or null if compatible.
	 * 
	 * @param providedInterface
	 *            Provided interface exposing the services offered by the base
	 *            language.
	 * @param requiredInterface
	 *            Required interface specifying the services required by the
	 *            extension language.
	 * @param binding
	 *            Mapping between the services of the required and provided
	 *            interfaces.
	 * @return
	 */
	public PuzzleDiagnosis checkCompatibility(EPackage providedInterface,
			EPackage requiredInterface, MatchingDiagnostic binding) {

		PuzzleDiagnosis errorsReport = new PuzzleDiagnosis();

		for (MatchingDiagnosticItem _mapping : binding.getItems()) {
			
			EObject _input = _mapping.getLeft();
			EObject _output = _mapping.getRight();

			if ((_input instanceof EClass) && (_output instanceof EClass)) {
				System.out.println("EClass _mapping: " + _input + " -- " + _output);
				EClass _inputEClass = (EClass) _input;
				EClass _outputEClass = (EClass) _output;

				// _inputEClass.abstract == false implies _outputEClass.asbtract == false
				if (_inputEClass.isAbstract() == false
						&& _outputEClass.isAbstract() == true) {
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INVALID_ABSTRACT_IMPLEMENTATION,
							_inputEClass, _outputEClass);
					errorsReport.addItem(errorItem);
				}
				
				// _inputEClass.interface == false implies _outputEClass.interface == false
				if(_inputEClass.isInterface() == false 
						&& _outputEClass.isInterface() == true){
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INVALID_INTERFACE_IMPLEMENTATION,
							_inputEClass, _outputEClass);
					errorsReport.addItem(errorItem);
				}
			} else if ((_input instanceof EReference)
					&& (_output instanceof EReference)) {
				System.out.println("EReference _mapping: " + _input + " -- "
						+ _output);
				
				EReference _inputEReference = (EReference) _input;
				EReference _outputEReference = (EReference) _output;
				
				//_inputEReference.lowerBound == _outputEReference.lowerBound &&
				//	_inputEReference.upperBound == _outputEReference.upperBound
				if(_inputEReference.getLowerBound() != _outputEReference.getLowerBound() ||
						_inputEReference.getUpperBound() != _outputEReference.getUpperBound()){
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.MULTIPLICITY_MISMATCH,
							_inputEReference, _outputEReference);
					errorsReport.addItem(errorItem);
				}
				
				//_inputEReference.ordered == true implies _outputEReference.ordered == true
				if(_inputEReference.isOrdered() == true &&
						_outputEReference.isOrdered() == false){
					
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INCOHERENT_ORDERED_FEATURES,
							_inputEReference, _outputEReference);
					errorsReport.addItem(errorItem);
				}
				
				//_inputEReference.unique == true implies _outputEReference.unique == true
				if(_inputEReference.isUnique() == true &&
						_outputEReference.isUnique() == false){
					
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INCOHERENT_UNIQUE_FEATURES,
							_inputEReference, _outputEReference);
					errorsReport.addItem(errorItem);
				}
				
				//_inputEReference.containment == _outputEReference.containment
				if(_inputEReference.isContainment() != _outputEReference.isContainment()){
					
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INCOHERENT_CONTAINMENT_REFERENCES,
							_inputEReference, _outputEReference);
					errorsReport.addItem(errorItem);
				}
				
			} else if ((_input instanceof EAttribute)
					&& (_output instanceof EAttribute)) {
				System.out.println("EAttribute _mapping: " + _input + " -- "
						+ _output);
				
				EAttribute _inputEAttribute = (EAttribute) _input;
				EAttribute _outputEAttribute = (EAttribute) _output;
				
				//_inputEAttribute.lowerBound == _outputEAttribute.lowerBound &&
				//	_inputEAttribute.upperBound == _outputEAttribute.upperBound
				if(_inputEAttribute.getLowerBound() != _outputEAttribute.getLowerBound() ||
						_inputEAttribute.getUpperBound() != _outputEAttribute.getUpperBound()){
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.MULTIPLICITY_MISMATCH,
							_inputEAttribute, _outputEAttribute);
					errorsReport.addItem(errorItem);
				}
				
				//_inputEReference.ordered == true implies _outputEReference.ordered == true
				if(_inputEAttribute.isOrdered() == true &&
						_outputEAttribute.isOrdered() == false){
					
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INCOHERENT_ORDERED_FEATURES,
							_inputEAttribute, _outputEAttribute);
					errorsReport.addItem(errorItem);
				}
				
				//_inputEReference.unique == true implies _outputEReference.unique == true
				if(_inputEAttribute.isUnique() == true &&
						_outputEAttribute.isUnique() == false){
					
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INCOHERENT_UNIQUE_FEATURES,
							_inputEAttribute, _outputEAttribute);
					errorsReport.addItem(errorItem);
				}
				
				//_outputEAttribute.type isSubtypeOf _inputEAttribute.type
				int _inputDataTypeIndex = dataTypes.get(_inputEAttribute.getEAttributeType().getName());
				int _outputDataTypeIndex = dataTypes.get(_outputEAttribute.getEAttributeType().getName());
				
				
				
				if(compatibilityMatrix[_outputDataTypeIndex][_inputDataTypeIndex] == 0){
					PuzzleDiagnosisItem errorItem = new PuzzleDiagnosisItem(
							PuzzleDiagnosisItemKind.INCOHERENT_ATTRIBUTE_DATATYPES,
							_inputEAttribute, _outputEAttribute);
					errorsReport.addItem(errorItem);
				}
			}
		}

		if (errorsReport.getItems().isEmpty())
			return null;
		else
			return errorsReport;
	}
	
	// -----------------------------------------------------
	// Private services of the class
	// -----------------------------------------------------

	/**
	 * Initializes the table of data types. 
	 * TODO: Get this information from a configuration file.  
	 * @return
	 */
	private Hashtable<String, Integer> loadDataTypes() {
		Hashtable<String, Integer> answer = new Hashtable<String, Integer>();
		answer.put("EBigDecimal", 1);
		answer.put("EBigInteger", 2);
		answer.put("EBoolean", 3);
		answer.put("EBooleanObject", 4);
		answer.put("EByte", 5);
		answer.put("EByteArray", 6);
		answer.put("EByteObject", 7);
		answer.put("EChar", 8);
		answer.put("ECharacterObject", 9);
		answer.put("EDate", 10);
		answer.put("EDiagnosticChain", 11);
		answer.put("EDouble", 12);
		answer.put("EDoubleObject", 13);
		answer.put("EEList", 14);
		answer.put("EEnumerator", 15);
		answer.put("EFeatureMap", 16);
		answer.put("EFeatureMapEntry", 17);
		answer.put("EFloat", 18);
		answer.put("EFloatObject", 19);
		answer.put("EInt", 20);
		answer.put("EIntegerObject", 21);
		answer.put("EInvocationTarget", 22);
		answer.put("EJavaClass", 23);
		answer.put("EJavaObject", 24);
		answer.put("ELong", 25);
		answer.put("ELongObject", 26);
		answer.put("EMap", 27);
		answer.put("EResource", 28);
		answer.put("EResourceSet", 29);
		answer.put("EShort", 30);
		answer.put("EShortObject", 31);
		answer.put("EString", 32);
		answer.put("ETreeIterator", 33);
		
		return answer;
	}
	
	/**
	 * Initializes the compatibility matrix from the source file. 
	 */
	private int[][] loadCompatibilityMatrix(){
		int[][] answer = new int[ValidatorImplConstants.DataTypesAmount + 1][ValidatorImplConstants.DataTypesAmount + 1];
		
		Bundle bundle = Platform.getBundle("fr.inria.diverse.puzzle.validator");
		URL fileURL = bundle.getEntry("data/compatibilityMatrix.txt");
		File file = null;
		try {
		    file = new File(FileLocator.resolve(fileURL).toURI());
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String currentLine = br.readLine();
		    
		    int i = 1;
		    while(i <= ValidatorImplConstants.DataTypesAmount){
		    	
		    	currentLine = br.readLine();
		    	String[] currentLineArray = currentLine.split(",");
		    	
		    	int j = 1;
		    	while(j <= ValidatorImplConstants.DataTypesAmount){
		    		answer[i][j] = Integer.parseInt(currentLineArray[j]);
		    		j++;
		    	}
		    	i++;
		    }
		    
		    br.close();
		    
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
		
		return answer;
	}
	
	/**
	 * Prints the compatibility matrix in the console. 
	 */
	private void printCompatibilityMatrix() {
		int i = 1;
	    while(i <= ValidatorImplConstants.DataTypesAmount){
	    	
	    	int j = 1;
	    	while(j <= ValidatorImplConstants.DataTypesAmount){
	    		System.out.print(compatibilityMatrix[i][j] + "|");
	    		j++;
	    	}
	    	System.out.println();
	    	i++;
	    }
		
	}

//	public void checkCompatibility(Object null1, Object null2, Object null3) {
//	  throw new UnsupportedOperationException("TODO: auto-generated method stub");
//	}
}

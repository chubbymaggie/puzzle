package fr.inria.diverse.puzzle.match.impl;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.puzzle.match.vo.MatchingDiagnostic;
import fr.inria.diverse.puzzle.match.vo.MatchingDiagnosticItem;

/**
 * Implementation of matching for metamodels-based DSLs
 * DiverSE Team - INRIA/IRISA Rennes.
 * 
 * @author David Mendez-Acuna
 */
public class PuzzleMatch {

	// -----------------------------------------------------
	// Attributes
	// -----------------------------------------------------
	
	private static PuzzleMatch instance;
	
	// -----------------------------------------------------
	// Constructor and singleton
	// -----------------------------------------------------
	
	private PuzzleMatch(){}
	
	public static PuzzleMatch getInstance(){
		if(instance == null)
			instance = new PuzzleMatch();
		return instance;
	}
	
	// -----------------------------------------------------
	// Methods
	// -----------------------------------------------------
	
	public MatchingDiagnostic match(EObject left, EObject right){
		MatchingDiagnostic diagnostic = new MatchingDiagnostic();
		ArrayList<MatchingDiagnosticItem> matches = new ArrayList<MatchingDiagnosticItem>();
		
		if(left instanceof EPackage && right instanceof EPackage){
			matchEPackage((EPackage)left, (EPackage)right, matches);
		}
		diagnostic.getItems().addAll(matches);
		return diagnostic;
	}
	
	private void matchEPackage(EPackage left, EPackage right, ArrayList<MatchingDiagnosticItem> matches){
		if(left.getName().equals(right.getName())){
			MatchingDiagnosticItem match = new MatchingDiagnosticItem(left, right);
			matches.add(match);
		}
		
		for (int i = 0; i < left.getEClassifiers().size(); i++) {
			for (int j = 0; j < right.getEClassifiers().size(); j++) {
				EClassifier candiateI = left.getEClassifiers().get(i);
				EClassifier candiateJ = right.getEClassifiers().get(j);
				
				if(matchEClassifier(candiateI, candiateJ)){
					MatchingDiagnosticItem match = new MatchingDiagnosticItem(candiateI, candiateJ);
					matches.add(match);
				}
			}
		}
		
		for (int i = 0; i < left.getESubpackages().size(); i++) {
			for (int j = 0; j < right.getESubpackages().size(); j++) {
				EPackage candiateI = left.getESubpackages().get(i);
				EPackage candiateJ = right.getESubpackages().get(j);
				this.matchEPackage(candiateI, candiateJ, matches);
			}
		}
	}
	
	private boolean matchEClassifier(EClassifier left, EClassifier right){
		return left.getName().equals(right.getName());
	}
}
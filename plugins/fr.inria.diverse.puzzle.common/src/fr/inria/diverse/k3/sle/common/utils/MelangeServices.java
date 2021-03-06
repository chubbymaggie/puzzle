package fr.inria.diverse.k3.sle.common.utils;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Services for interacting with Melange scripts.
 * @author David Mendez-Acuna
 */
public class MelangeServices {

	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	/**
	 * Extracts the ePackage corresponding to a language in Melange. 
	 * @param language
	 * @return
	 */
	public static EPackage getEPackageFromLanguage(Language language){
		if(language.getSyntax() != null){
			if(language.getSyntax().getEcoreUri().startsWith("platform:/resource/"))
				return  ModelUtils.loadEcoreResource(language.getSyntax().getEcoreUri());
			else
				return ModelUtils.loadEcoreFile(language.getSyntax().getEcoreUri());
		}
		else
			return null;
	}
	
	/**
	 * Extracts the list of ePackages from a list of Melange languages.
	 * @param languages
	 * @return
	 */
	public static ArrayList<EPackage> getEPackagesByALanguagesList(ArrayList<Language> languages){
		ArrayList<EPackage> ePackages = new ArrayList<EPackage>();
		for (Language language : languages) {
			if(language.getSyntax() != null){
				if(language.getSyntax().getEcoreUri().startsWith("platform:/resource/")){
					EPackage currentPackage = ModelUtils.loadEcoreResource(language.getSyntax().getEcoreUri());
					ePackages.add(currentPackage);
				}else{
					EPackage currentPackage = ModelUtils.loadEcoreFile(language.getSyntax().getEcoreUri());
					ePackages.add(currentPackage);
				}
				
				
			}
		}
		return ePackages;
	}
	
	public static int countLanguageConstructs(Language language){
		EPackage _package = getEPackageFromLanguage(language);
		return countMetaclasses(_package);
	}
	
	private static int countMetaclasses(EPackage _package){
		int answer = _package.getEClassifiers().size();
		for (EPackage subPackage : _package.getESubpackages()) {
			answer += countMetaclasses(subPackage);
		}
		return answer;
	}
}
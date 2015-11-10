package fr.inria.diverse.k3.sle.common.vos;

import fr.inria.diverse.melange.metamodel.melange.Aspect;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class AspectLanguageMapping {

	// ----------------------------------------------
	// Attributes
	// ----------------------------------------------
	
	private Aspect aspect;
	private Language language;
	private String languagesList;
	
	// ----------------------------------------------
	// Constructor
	// ----------------------------------------------
	
	public AspectLanguageMapping(Aspect aspect, Language language){
		this.aspect = aspect;
		this.language = language;
		this.languagesList = language.getName();
	}
	
	// ----------------------------------------------
	// Getters and setters
	// ----------------------------------------------

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getLanguagesList() {
		return languagesList;
	}

	public void setLanguagesList(String languagesList) {
		this.languagesList = languagesList;
	}
}
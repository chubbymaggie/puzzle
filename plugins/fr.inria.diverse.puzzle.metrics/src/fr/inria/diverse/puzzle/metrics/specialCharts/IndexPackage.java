package fr.inria.diverse.puzzle.metrics.specialCharts;

import java.util.ArrayList;

import fr.inria.diverse.melange.metamodel.melange.Language;

public class IndexPackage {

	private int index;
	private Language language;
	
	public IndexPackage(int index, Language language) {
		super();
		this.index = index;
		this.language = language;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public static int getIndexByEPackage(ArrayList<IndexPackage> source, Language language){
		for (IndexPackage indexLanguage : source) {
			if(indexLanguage.getLanguage().equals(language))
				return indexLanguage.index;
		}
		return -1;
	}
}

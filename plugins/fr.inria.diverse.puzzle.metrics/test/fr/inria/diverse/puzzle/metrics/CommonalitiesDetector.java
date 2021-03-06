package fr.inria.diverse.puzzle.metrics;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.NamingConceptComparison;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMembers;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.melange.metamodel.melange.MelangeFactory;
import fr.inria.diverse.melange.metamodel.melange.Metamodel;

public class CommonalitiesDetector {

	public ArrayList<String> obtainCommonalities(String[] ecoreFiles) throws Exception{
		ArrayList<String> commonality = new ArrayList<String>();
		ArrayList<Language> languages = new ArrayList<Language>();
		for (String ecoreFile : ecoreFiles) {
			Metamodel metamodel = MelangeFactory.eINSTANCE.createMetamodel();
			metamodel.setEcoreUri(ecoreFile);
			Language language = MelangeFactory.eINSTANCE.createLanguage();
			language.setName(ecoreFile);
			language.setSyntax(metamodel);
			languages.add(language);
		}
		
		ConceptComparison comparisonOperator = new NamingConceptComparison();
		
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(languages);
		ArrayList<TupleConceptMembers> conceptMemberGroupList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, comparisonOperator);
		
		for (TupleConceptMembers conceptMembersGroupVO : conceptMemberGroupList) {
			if(this.commonality(conceptMembersGroupVO, languages)){
				commonality.add(conceptMembersGroupVO.getConcept().getName());
			}
		}
		return commonality;
	}
	
	private boolean commonality(TupleConceptMembers conceptMembersGroupVO,
			ArrayList<Language> languages) {
		boolean allContained = true;
		for (Language language : languages) {
			allContained = allContained && conceptMembersGroupVO.getMembers().contains(language.getSyntax().getEcoreUri());
			
			if(!allContained)
				continue;
		}
		return allContained && languages.size() == conceptMembersGroupVO.getMembers().size();
	}

	public static void main(String[] args) {
		
		CommonalitiesDetector detector = new CommonalitiesDetector();
		try {
			ArrayList<String> commonalities = detector.obtainCommonalities(args);
			for (String commonality : commonalities) {
				System.out.println(commonality);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

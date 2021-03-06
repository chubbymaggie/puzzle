package de.trier.cs.jccd.tests;

import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.JCCDFile;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.data.SimilarityGroupManager;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.detectors.APipeline;
import org.eposoft.jccd.detectors.ASTDetector;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodArgumentTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodReturnTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableDeclarationTypes;

public class Main {

	public void test() {
		APipeline<ANode> detector = new ASTDetector();
		JCCDFile[] files = { new JCCDFile("data/StatementAspectOne.java"),
				new JCCDFile("data/StatementAspectTwo.java") };
		detector.setSourceFiles(files);
		
		detector.addOperator(new GeneralizeMethodArgumentTypes());
		detector.addOperator(new GeneralizeMethodReturnTypes());
		detector.addOperator(new GeneralizeVariableDeclarationTypes());
		
		SimilarityGroupManager manager = detector.process();
		APipeline.printSimilarityGroups(manager);
		
		SimilarityGroup[] simGroups = manager.getSimilarityGroups();
		if ((null != simGroups) && (0 < simGroups.length)) {
			for (int i = 0; i < simGroups.length; i++) {
				final ASourceUnit[] nodes = simGroups[i].getNodes();
				for (int j = 0; j < nodes.length; j++) {
					System.out.println(nodes[j].getText());
				}
			}
		}
	}
	
	public static void main(String[] args){
		(new Main()).test();
	}
}

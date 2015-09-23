package fr.inria.diverse.puzzle.vmsynthesis.tests;

import java.util.ArrayList;

import vm.PBinaryExpression;
import vm.PConstraint;
import vm.PFeature;
import vm.PFeatureGroup;
import vm.PFeatureModel;
import vm.PFeatureRef;
import vm.PUnaryExpression;
import vm.PUninaryOperator;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.fama.PluginQuestionTrader;
import fr.inria.diverse.puzzle.vmsynthesis.impl.FromPFeatureModelToFAMA;
import fr.inria.diverse.puzzle.vmsynthesis.impl.FromPFeatureModelToFAMAAttributed;

public class TestServices {

	// -------------------------------------------------
	// Auxiliary Methods
	// -------------------------------------------------
	
	public static void printFM(PFeatureModel fm){
		System.out.println(fm.getName());
		printFeature("", " + ", fm.getRootFeature());
		
		System.out.println(fm.getConstraints().size());
		for (PConstraint constraint : fm.getConstraints()) {
			if(constraint.getExpression() instanceof PBinaryExpression){
				PBinaryExpression pBinaryExpression = (PBinaryExpression) constraint.getExpression();
				 if(pBinaryExpression.getLeft() instanceof PFeatureRef && 
						 pBinaryExpression.getRight() instanceof PFeatureRef){
					 PFeatureRef left = (PFeatureRef) pBinaryExpression.getLeft();
					 PFeatureRef right = (PFeatureRef) pBinaryExpression.getRight();
					 System.out.println(left.getRef().getName() + " " + pBinaryExpression.getOperator().getName() + " " + right.getRef().getName());
				 }
				 
				 if(pBinaryExpression.getLeft() instanceof PFeatureRef &&
							pBinaryExpression.getRight() instanceof PUnaryExpression){
					PUnaryExpression not = (PUnaryExpression) pBinaryExpression.getRight();
					if(not.getOperator().getName().equals(PUninaryOperator.NOT.getName())){
						if(not.getExpr() instanceof PFeatureRef){
							 PFeatureRef left = (PFeatureRef) pBinaryExpression.getLeft();
							 PFeatureRef right = (PFeatureRef) not.getExpr();
							 System.out.println(left.getRef().getName() + " " + pBinaryExpression.getOperator().getName() + " not " + right.getRef().getName());
						}
					}
				}
			}
		}
	}
	
	public static void printFeature(String space, String groupString, PFeature feature){
		System.out.println(space + groupString + feature.getName() + " [mandatory: " + feature.isMandatory() + "]");
		int i = 1;
		for (PFeatureGroup group : feature.getGroups()) {
			for (PFeature childFeature : group.getFeatures()) {
				printFeature("    " + space, " Group " + i + " (" + 
						group.getCardinality().getLowerBound() + "," + group.getCardinality().getUpperBound() 
							+ "): ", childFeature);
			}
			i++;
		}
	}
	
	public static double printAllValidProducts(PFeatureModel fm, String PCM){
//		FAMAFeatureModel famaFm = FromPFeatureModelToFAMA.getInstance().fromPFeatureModelToFAMA(fm);
		FAMAAttributedFeatureModel famaFm = FromPFeatureModelToFAMAAttributed.getInstance().fromPFeatureModelToFAMA(fm);
		
		PluginQuestionTrader qt = new PluginQuestionTrader();
		qt.setVariabilityModel(famaFm);
		
		NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		System.out.println(npq);
		qt.ask(npq);
		double numberOfProducts = npq.getNumberOfProducts();
		System.out.println("The number of products is: " + numberOfProducts);
		
		ProductsQuestion pq = (ProductsQuestion) qt.createQuestion("Products");
		qt.ask(pq);
		
		String[] products = PCM.split("\n");
		
		int i = 1;
		for (int k = 1; k < products.length; k++) {
			String[] features = products[k].split(",");
			ArrayList<String> featuresString = new ArrayList<String>();
			featuresString.add("Root");
			
			int j = 1;
			while ( j < features.length ) {
				
				if(features[j].equals("\"YES\"")){
					featuresString.add("F" + j);
				}
				j++;
			}
			
			String[] featuresStringArray = new String[featuresString.size()];
			for (int l = 0; l < featuresString.size(); l++) {
				featuresStringArray[l] = featuresString.get(l);
			}
			
			boolean product1Exists = productExists(featuresStringArray, pq);
			System.out.println("P" + i + ": " + product1Exists);
			
			i++;
		}
		
		return numberOfProducts;
		
//		for (GenericProduct product : pq.getAllProducts()) {
//			System.out.print("Product: ");
//			for (VariabilityElement element : product.getElements()) {
//				System.out.print(element + ", ");	
//			}
//			System.out.println();
//		}
	}
	
	public static boolean productExists(String[] productFeatures, ProductsQuestion pq){
		for (GenericProduct product : pq.getAllProducts()) {
			if(productsAreEqual(product, productFeatures)){
				return true;
			}
		}
		return false;
	}

	public static boolean productsAreEqual(GenericProduct product,
			String[] productFeatures) {
		if(product.getElements().size() != productFeatures.length){
			return false;
		}
		
		for (String feature : productFeatures) {
			boolean featureExists = false;
			
			for (VariabilityElement element : product.getElements()) {
				if(element.getName().equals(feature)){
					featureExists = true;
					break;
				}
			}
			
			if(!featureExists)
				return false;
		}
		return true;
	}
}

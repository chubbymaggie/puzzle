package fr.inria.diverse.puzzle.vmsynthesis.impl;

import java.util.ArrayList;
import java.util.List;

import fr.inria.diverse.generator.pcm.PCMQueryServices;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import vm.BinaryExpression;
import vm.BinaryOperator;
import vm.BooleanExpression;
import vm.LanguageConstraint;
import vm.LanguageFeature;
import vm.LanguageFeatureGroup;
import vm.LanguageFeatureGroupCardinality;
import vm.LanguageFeatureModel;
import vm.LanguageFeatureRef;
import vm.UnaryExpression;
import vm.UninaryOperator;
import vm.VmFactory;

/**
 * Algorithm for synthesizing feature models from PCMs and dependencies graphs.
 * 
 * @author David Mendez-Acuna
 *
 */
public class VmSynthesis {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------

	private static VmSynthesis instance;

	// ----------------------------------------------------------
	// Constructor and singleton
	// ----------------------------------------------------------

	private VmSynthesis() {

	}

	public static VmSynthesis getInstance() {
		if (instance == null)
			instance = new VmSynthesis();
		return instance;
	}

	// ----------------------------------------------------------
	// Methods
	// ----------------------------------------------------------

	/**
	 * Synthesizes an open feature model from the given dependencies graph.
	 * 
	 * @param dependenciesGraph
	 * @return
	 */
	public LanguageFeatureModel synthesizeOpenFeatureModel(Graph<Vertex, Arc> dependenciesGraph) {
		LanguageFeatureModel featureModel = VmFactory.eINSTANCE.createLanguageFeatureModel();

		// TODO Put a real family name to the feature model.
		featureModel.setName("FeatureModel");

		LanguageFeature rootFeature = VmFactory.eINSTANCE.createLanguageFeature();
		rootFeature.setMandatory(true);
		rootFeature.setName("Root");

		// Step 2: The first level features are those vertex in the graph such
		// that they have not dependencies with other vertex.
		List<Vertex> firstLevelVertex = dependenciesGraph.getIndendependentVertex();
		for (Vertex dependencyVertex : firstLevelVertex) {
			dependencyVertex.setIncluded(true);

			LanguageFeature feature = VmFactory.eINSTANCE.createLanguageFeature();
			feature.setName(dependencyVertex.getIdentifier());
			feature.setParent(rootFeature);

			LanguageFeatureGroup featureGroup = VmFactory.eINSTANCE.createLanguageFeatureGroup();
			featureGroup.getFeatures().add(feature);

			LanguageFeatureGroupCardinality cardinality = VmFactory.eINSTANCE.createLanguageFeatureGroupCardinality();
			cardinality.setLowerBound(0);
			cardinality.setUpperBound(1);
			featureGroup.setCardinality(cardinality);
			rootFeature.getGroups().add(featureGroup);
		}

		List<Vertex> currentLevel = firstLevelVertex;

		while (!dependenciesGraph.allIncluded()) {
			List<Vertex> directDependentVertex = dependenciesGraph.getDirectDependentVertex(currentLevel);
			for (Vertex dependencyVertex : directDependentVertex) {
				if (!dependencyVertex.isIncluded()) {
					LanguageFeature feature = VmFactory.eINSTANCE.createLanguageFeature();
					feature.setName(dependencyVertex.getIdentifier());
					dependencyVertex.setIncluded(true);

					boolean first = true;
					for (Arc dependencyArc : dependencyVertex.getOutgoingArcs()) {
						if (currentLevel.contains(dependencyArc.getTo())) {
							if (first) {
								// Esta feature no tiene padre. Asignelo.
								LanguageFeature parent = this.getLanguageFeatureByName(dependencyArc.getTo().getIdentifier(),
										rootFeature);
								feature.setParent(parent);

								LanguageFeatureGroup featureGroup = VmFactory.eINSTANCE.createLanguageFeatureGroup();
								featureGroup.getFeatures().add(feature);

								LanguageFeatureGroupCardinality cardinality = VmFactory.eINSTANCE
										.createLanguageFeatureGroupCardinality();
								cardinality.setLowerBound(0);
								cardinality.setUpperBound(1);
								featureGroup.setCardinality(cardinality);

								parent.getGroups().add(featureGroup);
							}
							first = false;
						}
					}
				}
			}
			currentLevel = directDependentVertex;
		}

		featureModel.setRootFeature(rootFeature);
		this.addCrosslevelRequires(featureModel, dependenciesGraph);
		return featureModel;
	}

	private void addCrosslevelRequires(LanguageFeatureModel featureModel, Graph<Vertex, Arc> dependenciesGraph) {
		for (Arc arc : dependenciesGraph.getArcs()) {
			if (!this.isFather(featureModel.getRootFeature(), arc.getTo().getIdentifier(),
					arc.getFrom().getIdentifier())) {
				LanguageFeature requiredFeature = this.getLanguageFeatureByName(arc.getTo().getIdentifier(),
						featureModel.getRootFeature());
				LanguageConstraint constraint = VmFactory.eINSTANCE.createLanguageConstraint();
				BinaryExpression binaryExpression = VmFactory.eINSTANCE.createBinaryExpression();
				LanguageFeatureRef left = VmFactory.eINSTANCE.createLanguageFeatureRef();
				left.setRef(this.getLanguageFeatureByName(arc.getFrom().getIdentifier(), featureModel.getRootFeature()));
				LanguageFeatureRef right = VmFactory.eINSTANCE.createLanguageFeatureRef();
				right.setRef(requiredFeature);
				binaryExpression.setLeft(left);
				binaryExpression.setRight(right);
				binaryExpression.setOperator(BinaryOperator.IMPLIES);
				constraint.setExpression(binaryExpression);
				constraint.setName(left.getRef().getName() + " implies " + right.getRef().getName());
				featureModel.getConstraints().add(constraint);
			}
		}

	}

	private boolean isFather(LanguageFeature root, String father, String child) {
		if (root.getName().equals(father)) {
			for (LanguageFeature children : root.getChildren()) {
				if (children.getName().equals(child)) {
					return true;
				}
			}
		} else {
			for (LanguageFeature children : root.getChildren()) {
				boolean isFather = this.isFather(children, father, child);
				if (isFather) {
					return true;
				}
			}
		}
		return false;
	}

	public LanguageFeatureModel synthesizeClosedFeatureModel(String PCM, LanguageFeatureModel openFeatureModel) throws Exception {
		LanguageFeatureModel closedFeatureModel = this.cloneFeatureModel(openFeatureModel);
		PCMQueryServices.getInstance().loadPCM(PCM);

		this.identifyMandatoryFeatures(closedFeatureModel);
		this.identifyXORs(closedFeatureModel);
		this.identifyORs(closedFeatureModel);
		this.addAdditionalImpliesConstraints(closedFeatureModel);
		this.addAdditionalExcludesConstraints(closedFeatureModel);
//		this.groupImplicationsByLeftSide(closedFeatureModel);
		
		return closedFeatureModel;
	}



	/**
	 * Identifies the mandatory features in the feature model received in the
	 * parameter.
	 * 
	 * @param fm.
	 *            Feature model under study.
	 */
	public void identifyMandatoryFeatures(LanguageFeatureModel fm) {
		for (LanguageFeature child : fm.getRootFeature().getChildren()) {
			this.identifyMandatoryFeatures(child, true);
		}
	}

	/**
	 * Identifies the mandatory features recursively starting by the feature in
	 * the parameter.
	 * 
	 * @param rootFeature.
	 *            The root feature of the hierarchy.
	 * @param isRoot.
	 *            Indicates if the current feature is the feature of the feature
	 *            model.
	 */
	private void identifyMandatoryFeatures(LanguageFeature rootFeature, boolean isRoot) {
		if (isRoot) {
			if (PCMQueryServices.getInstance().existsProductWithoutFeature(rootFeature.getName()))
				rootFeature.setMandatory(false);
			else {
				rootFeature.setMandatory(true);
				if (rootFeature.getParent() != null) {
					for (LanguageFeatureGroup parentGroup : rootFeature.getParent().getGroups()) {
						if (parentGroup.getFeatures().get(0).getName().equals(rootFeature.getName())) {
							parentGroup.getCardinality().setLowerBound(1);
							parentGroup.getCardinality().setUpperBound(1);
						}
					}
				}
			}
		} else {
			boolean optional = PCMQueryServices.getInstance()
					.existsProductWithFeatureAWithoutFeatureB(rootFeature.getParent().getName(), rootFeature.getName());

			if (optional) {
				rootFeature.setMandatory(false);
			} else {
				rootFeature.setMandatory(true);
				if (rootFeature.getParent() != null) {
					for (LanguageFeatureGroup parentGroup : rootFeature.getParent().getGroups()) {
						if (parentGroup.getFeatures().get(0).getName().equals(rootFeature.getName())) {
							parentGroup.getCardinality().setLowerBound(1);
							parentGroup.getCardinality().setUpperBound(1);
						}
					}
				}
			}
		}

		for (LanguageFeature child : rootFeature.getChildren()) {
			this.identifyMandatoryFeatures(child, false);
		}
	}

	/**
	 * Identifies the XOR groups in the feature model received in the parameter.
	 * 
	 * @param fm.
	 *            Feature model under study.
	 */
	public void identifyXORs(LanguageFeatureModel fm) {
		this.identifyXORs(fm.getRootFeature());
	}

	/**
	 * Identifies the XOR groups recursively starting by the feature in the
	 * parameter.
	 * 
	 * @param rootFeature.
	 *            The root feature of the hierarchy.
	 * @param allXORs.
	 *            A collection where the XOR groups will be stored.
	 */
	private void identifyXORs(LanguageFeature rootFeature) {
		ArrayList<ArrayList<LanguageFeature>> allXORs = new ArrayList<ArrayList<LanguageFeature>>();
		ArrayList<LanguageFeature> initialGroup = new ArrayList<LanguageFeature>();
		for (LanguageFeatureGroup group : rootFeature.getGroups()) {
			if (group.getCardinality().getLowerBound() == 0 && group.getCardinality().getUpperBound() == 1
					&& group.getFeatures().size() > 0) {
				initialGroup.add(group.getFeatures().get(0));
			}
		}
		this.identifyXORByCombination(initialGroup, allXORs);
		this.sortBySize(allXORs);
		ArrayList<LanguageFeature> added = new ArrayList<LanguageFeature>();

		for (ArrayList<LanguageFeature> xor : allXORs) {
			if (this.isStillValid(added, xor)) {
				// Create new XOR group
				for (LanguageFeature pFeature : xor) {
					LanguageFeatureGroup featureGroup = this.getGroupByFeature(rootFeature, pFeature);
					rootFeature.getGroups().remove(featureGroup);
				}
				this.createXORGroup(rootFeature, xor);
				// Add to the already considered elements.
				if(xor.size() >= 2)
					added.addAll(xor);
			}
		}

		for (LanguageFeature child : rootFeature.getChildren()) {
			this.identifyXORs(child);
		}
	}

	/**
	 * Searches the group containing the feature in the parameter starting by
	 * the given root feature.
	 * 
	 * @param rootFeature
	 * @param feature
	 * @return
	 */
	private LanguageFeatureGroup getGroupByFeature(LanguageFeature rootFeature, LanguageFeature feature) {
		for (LanguageFeatureGroup currentGroup : rootFeature.getGroups()) {
			if (currentGroup.getFeatures().contains(feature))
				return currentGroup;
		}
		return null;
	}

	/**
	 * Creates an XOR group with the features in the parameter and add it to the
	 * root feature.
	 * 
	 * @param rootFeature
	 * @param xor
	 */
	private void createXORGroup(LanguageFeature rootFeature, ArrayList<LanguageFeature> xorFeatures) {
		LanguageFeatureGroup XORGroup = VmFactory.eINSTANCE.createLanguageFeatureGroup();
		XORGroup.getFeatures().addAll(xorFeatures);

		LanguageFeatureGroupCardinality cardinality = VmFactory.eINSTANCE.createLanguageFeatureGroupCardinality();
		ArrayList<String> features = new ArrayList<String>();
		for (LanguageFeature pFeature : xorFeatures) {
			features.add(pFeature.getName());
		}

		ArrayList<String> consideredProducts = null;
		if (rootFeature.getName().equals("Root"))
			consideredProducts = PCMQueryServices.getInstance().getAllProducts();
		else
			consideredProducts = PCMQueryServices.getInstance().getProductsContainingFeature(rootFeature.getName());

		cardinality.setLowerBound(PCMQueryServices.getInstance().minFeaturesOccurrences(features, consideredProducts));
		cardinality.setUpperBound(1);
		XORGroup.setCardinality(cardinality);
		rootFeature.getGroups().add(XORGroup);
	}

	/**
	 * Sorts the array of groups in the parameter by size.
	 * 
	 * @param group
	 */
	private void sortBySize(ArrayList<ArrayList<LanguageFeature>> group) {
		// TODO Implement it!!
	}

	/**
	 * Returns true if the XOR in the parameter does not contains any of the
	 * features in the array
	 * 
	 * @param features
	 * @param xor
	 * @return
	 */
	private boolean isStillValid(ArrayList<LanguageFeature> features, ArrayList<LanguageFeature> xor) {
		for (int i = 0; i < features.size(); i++) {
			for (int j = 0; j < xor.size(); j++) {
				if (features.get(i).getName().equals(xor.get(j).getName()))
					return false;
			}
		}
		return true;
	}

	/**
	 * Identifies the XOR groups existing in the feature in the parameter. This
	 * method considers all the possible group combinations.
	 * 
	 * @param group
	 * @param allXORs
	 */
	private void identifyXORByCombination(ArrayList<LanguageFeature> group, ArrayList<ArrayList<LanguageFeature>> allXORs) {
		ArrayList<String> features = new ArrayList<String>();
		for (LanguageFeature feature : group) {
			features.add(feature.getName());
		}
		boolean xor = PCMQueryServices.getInstance().xor(features);
		if (xor && group.size() >= 2) {
			allXORs.add(group);
			return;
		} else {
//			for (LanguageFeature feature : group) {
//				ArrayList<LanguageFeature> recursiveFeatures = new ArrayList<LanguageFeature>();
//				for (LanguageFeature recursiveFeature : group) {
//					if (!recursiveFeature.getName().equals(feature.getName()))
//						recursiveFeatures.add(recursiveFeature);
//				}
//				if (recursiveFeatures.size() >= 2) {
//					this.identifyXORByCombination(recursiveFeatures, allXORs);
//				}
//			}
		}
	}

	/**
	 * Identifies the OR groups in the feature model received in the parameter.
	 * 
	 * @param fm.
	 *            Feature model under study.
	 */
	public void identifyORs(LanguageFeatureModel fm) {
		this.identifyORs(fm.getRootFeature());
	}

	/**
	 * Identifies the OR groups recursively starting by the feature in the
	 * parameter.
	 * 
	 * @param rootFeature.
	 *            The root feature of the hierarchy.
	 */
	private void identifyORs(LanguageFeature rootFeature) {
		ArrayList<LanguageFeature> or = new ArrayList<LanguageFeature>();
		for (LanguageFeatureGroup group : rootFeature.getGroups()) {
			if (group.getCardinality().getLowerBound() == 0 && group.getCardinality().getUpperBound() == 1
					&& group.getFeatures().size() == 1) {
				or.add(group.getFeatures().get(0));
			}
		}

		if (or.size() > 1) {
			for (LanguageFeature pFeature : or) {
				LanguageFeatureGroup featureGroup = this.getGroupByFeature(rootFeature, pFeature);
				rootFeature.getGroups().remove(featureGroup);
			}
			this.createORGroup(rootFeature, or);
		}

		for (LanguageFeature child : rootFeature.getChildren()) {
			this.identifyORs(child);
		}
	}

	/**
	 * Creates an OR group with the features in the parameter and adds it to the
	 * root feature.
	 * 
	 * @param rootFeature
	 * @param or
	 */
	private void createORGroup(LanguageFeature rootFeature, ArrayList<LanguageFeature> orFeatures) {
		LanguageFeatureGroup ORGroup = VmFactory.eINSTANCE.createLanguageFeatureGroup();
		ORGroup.getFeatures().addAll(orFeatures);

		LanguageFeatureGroupCardinality cardinality = VmFactory.eINSTANCE.createLanguageFeatureGroupCardinality();
		ArrayList<String> features = new ArrayList<String>();
		for (LanguageFeature pFeature : orFeatures) {
			features.add(pFeature.getName());
		}

		ArrayList<String> consideredProducts = null;
		if (rootFeature.getName().equals("Root"))
			consideredProducts = PCMQueryServices.getInstance().getAllProducts();
		else
			consideredProducts = PCMQueryServices.getInstance().getProductsContainingFeature(rootFeature.getName());

		cardinality.setLowerBound(PCMQueryServices.getInstance().minFeaturesOccurrences(features, consideredProducts));
		cardinality.setUpperBound(PCMQueryServices.getInstance().maxFeaturesOccurrences(features, consideredProducts));
		ORGroup.setCardinality(cardinality);
		rootFeature.getGroups().add(ORGroup);
	}

	/**
	 * Identifies the implications existing between the features of the feature model given in the parameter.
	 * @param fm
	 */
	public void addAdditionalImpliesConstraints(LanguageFeatureModel fm) {
		ArrayList<LanguageFeature> features = new ArrayList<LanguageFeature>();
		this.collectLanguageFeatures(features, fm.getRootFeature());

		for (int i = 1; i < features.size(); i++) {
			for (int j = 1; j < features.size(); j++) {
				LanguageFeature origin = features.get(i);
				LanguageFeature destination = features.get(j);

				if (!origin.getName().equals(destination.getName())) {
					boolean child = this.featureAIsParentOfB(destination, origin);
					if (!child && !destination.isMandatory() && PCMQueryServices.getInstance()
							.allProductsWithFeatureAHaveAlsoFeatureB(origin.getName(), destination.getName()) &&
							!this.implicationExists(origin, destination, fm)) {
						this.createImplies(origin, destination, fm);
					}
				}
			}
		}
	}

	/**
	 * Indicates if in the feature model in the parameter there is already an implication between the origin and the destination. 
	 * @param origin
	 * @param destination
	 * @param fm
	 * @return
	 */
	private boolean implicationExists(LanguageFeature origin, LanguageFeature destination,
			LanguageFeatureModel fm) {
		
		LanguageConstraint implies = VmFactory.eINSTANCE.createLanguageConstraint();
		LanguageFeatureRef left = VmFactory.eINSTANCE.createLanguageFeatureRef();
		left.setRef(origin);
		LanguageFeatureRef right = VmFactory.eINSTANCE.createLanguageFeatureRef();
		right.setRef(destination);

		BinaryExpression pBinaryExpression = VmFactory.eINSTANCE.createBinaryExpression();
		pBinaryExpression.setLeft(left);
		pBinaryExpression.setRight(right);
		pBinaryExpression.setOperator(BinaryOperator.IMPLIES);

		implies.setExpression(pBinaryExpression);
		
		for (LanguageConstraint constraint : fm.getConstraints()) {
			if(pBooleanExpressionEquals(constraint.getExpression(), implies.getExpression())){
				return true;
			}
		}
		return false;
	}

	private boolean featureAIsParentOfB(LanguageFeature A, LanguageFeature B) {
		if (B.getParent() == null)
			return false;
		else if (A.getName().equals(B.getParent().getName()))
			return true;
		else {
			return this.featureAIsParentOfB(A, B.getParent());
		}
	}

	/**
	 * Creates an implies constraint from the origin to the destination in the feature model given in the parameter.
	 * @param origin
	 * @param destination
	 * @param fm
	 */
	private void createImplies(LanguageFeature origin, LanguageFeature destination,
			LanguageFeatureModel fm) {
		
		LanguageConstraint implies = VmFactory.eINSTANCE.createLanguageConstraint();
		LanguageFeatureRef left = VmFactory.eINSTANCE.createLanguageFeatureRef();
		left.setRef(origin);
		LanguageFeatureRef right = VmFactory.eINSTANCE.createLanguageFeatureRef();
		right.setRef(destination);

		BinaryExpression pBinaryExpression = VmFactory.eINSTANCE.createBinaryExpression();
		pBinaryExpression.setLeft(left);
		pBinaryExpression.setRight(right);
		pBinaryExpression.setOperator(BinaryOperator.IMPLIES);

		implies.setExpression(pBinaryExpression);
		implies.setName(left.getRef().getName() + " implies " + right.getRef().getName());
	
		fm.getConstraints().add(implies);
	}

	/**
	 * Adds the additional excludes constraints to the closed feature model.
	 * 
	 * @param fm
	 */
	public void addAdditionalExcludesConstraints(LanguageFeatureModel fm) {
		ArrayList<LanguageFeature> features = new ArrayList<LanguageFeature>();
		this.collectLanguageFeatures(features, fm.getRootFeature());

		for (int i = 1; i < features.size(); i++) {
			for (int j = 1; j < features.size(); j++) {
				LanguageFeature origin = features.get(i);
				LanguageFeature destination = features.get(j);

				if (!origin.getName().equals(destination.getName())) {
					if (!origin.getParentGroup().equals(destination.getParentGroup()) && PCMQueryServices.getInstance()
							.allProductsWithFeatureAExcludeFeatureB(origin.getName(), destination.getName())) {
						this.createExcludes(origin, destination, fm);
					}
				}
			}
		}
	}

	
	/**
	 * Creates an excludes (implies not) constraint from the origin to the destination in the feature model given in the parameter.
	 * @param origin
	 * @param destination
	 * @param fm
	 */
	private void createExcludes(LanguageFeature origin, LanguageFeature destination,
			LanguageFeatureModel fm) {
		

		LanguageConstraint excludes = VmFactory.eINSTANCE.createLanguageConstraint();
		LanguageFeatureRef left = VmFactory.eINSTANCE.createLanguageFeatureRef();
		left.setRef(origin);

		LanguageFeatureRef right = VmFactory.eINSTANCE.createLanguageFeatureRef();
		right.setRef(destination);

		UnaryExpression notImplies = VmFactory.eINSTANCE.createUnaryExpression();
		notImplies.setExpr(right);
		notImplies.setOperator(UninaryOperator.NOT);

		BinaryExpression pBinaryExpression = VmFactory.eINSTANCE.createBinaryExpression();
		pBinaryExpression.setLeft(left);
		pBinaryExpression.setRight(notImplies);
		pBinaryExpression.setOperator(BinaryOperator.IMPLIES);

		excludes.setExpression(pBinaryExpression);
		excludes.setName(left.getRef().getName() + " excludes " + right.getRef().getName());
		fm.getConstraints().add(excludes);
	}


	/**
	 * Groups the includes constraints in the given feature model that have the same right side. 
	 * @param featureModel. 
	 */
	public void groupImplicationsByRightSide(LanguageFeatureModel featureModel) {
		
		// Step 1. Collect the implication groups in the variable 'groups'
		ArrayList<RightImplicationsGroup> groups = new ArrayList<RightImplicationsGroup>();
		for (LanguageConstraint constraintI : featureModel.getConstraints()) {
			if(constraintI.getExpression() instanceof BinaryExpression
					&& ((BinaryExpression)constraintI.getExpression()).getOperator().getName().equals(BinaryOperator.IMPLIES.getName())){
				
				BinaryExpression impliesI = (BinaryExpression) constraintI.getExpression();
				
				if(impliesI.getRight() instanceof LanguageFeatureRef){
					LanguageFeatureRef rightFeatureRefI = (LanguageFeatureRef) impliesI.getRight();
					if(!this.existsImplicationGroup(groups, rightFeatureRefI)){
						RightImplicationsGroup newGroup = new RightImplicationsGroup(rightFeatureRefI, false);
						
						for (LanguageConstraint constraintJ : featureModel.getConstraints()) {
							if(constraintJ.getExpression() instanceof BinaryExpression
									&& ((BinaryExpression)constraintJ.getExpression()).getOperator().getName().equals(BinaryOperator.IMPLIES.getName())
									&& constraintI != constraintJ){
								
								BinaryExpression impliesJ = (BinaryExpression) constraintJ.getExpression();
								
								if(impliesI.getRight() instanceof LanguageFeatureRef &&
										impliesJ.getRight() instanceof LanguageFeatureRef){
									LanguageFeatureRef leftFeatureRef = (LanguageFeatureRef) impliesJ.getLeft();
									LanguageFeatureRef rightFeatureRefJ = (LanguageFeatureRef) impliesJ.getRight();
									
									if(rightFeatureRefI.getRef().getName().equals(rightFeatureRefJ.getRef().getName())){
										if(!this.containsFeatureRef(newGroup.getLeftSide(), leftFeatureRef))
											newGroup.getLeftSide().add(leftFeatureRef);
									}
								}
							}
						}
						if(newGroup.getLeftSide().size() >= 2)
							groups.add(newGroup);
					}
				}
			}
		}
		
		// Step 2. Filtering the real groups!
		ArrayList<RightImplicationsGroup> realGroups = new ArrayList<RightImplicationsGroup>();
		for (RightImplicationsGroup group : groups) {
			this.searchRealRightImplicationsGroupByCombinatory(realGroups, group);
		}
		
		// Step 3. Creating one constraint for each real group. 
		for (RightImplicationsGroup group : realGroups) {
			if(group.getRightSide() != null){
				LanguageConstraint constraint = this.fromRightImplicationToConstraint(group);
				featureModel.getConstraints().add(constraint);
			}
		}
	}
	
	/**
	 * Groups the excludes constraints contained in the given feature model that have the same right side. 
	 * @param featureModel
	 */
	public void groupNotImplicationsByRightSide(LanguageFeatureModel featureModel) {
		// Step 1. Collect the implication groups in the variable 'groups'
		ArrayList<RightImplicationsGroup> groups = new ArrayList<RightImplicationsGroup>();
		for (LanguageConstraint constraintI : featureModel.getConstraints()) {
			if(constraintI.getExpression() instanceof BinaryExpression
					&& ((BinaryExpression)constraintI.getExpression()).getOperator().getName().equals(BinaryOperator.IMPLIES.getName())){
			
				BinaryExpression impliesI = (BinaryExpression) constraintI.getExpression();
			
				if(impliesI.getRight() instanceof UnaryExpression){
					UnaryExpression unaryExprssionI = (UnaryExpression) impliesI.getRight();
					LanguageFeatureRef rightFeatureRefI = (LanguageFeatureRef) unaryExprssionI.getExpr();
					if(!this.existsImplicationGroup(groups, rightFeatureRefI)){
						RightImplicationsGroup newGroup = new RightImplicationsGroup(rightFeatureRefI, true);
						
						for (LanguageConstraint constraintJ : featureModel.getConstraints()) {
							if(constraintJ.getExpression() instanceof BinaryExpression
									&& ((BinaryExpression)constraintJ.getExpression()).getOperator().getName().equals(BinaryOperator.IMPLIES.getName())
									&& constraintI != constraintJ){
								
								BinaryExpression impliesJ = (BinaryExpression) constraintJ.getExpression();
								
								if(impliesI.getRight() instanceof UnaryExpression &&
										impliesJ.getRight() instanceof UnaryExpression){
									
									LanguageFeatureRef leftFeatureRef = (LanguageFeatureRef) impliesJ.getLeft();
									
									UnaryExpression unaryExprssionJ = (UnaryExpression) impliesJ.getRight();
									LanguageFeatureRef rightFeatureRefJ = (LanguageFeatureRef) unaryExprssionJ.getExpr();
									
									if(rightFeatureRefI.getRef().getName().equals(rightFeatureRefJ.getRef().getName())){
										if(!this.containsFeatureRef(newGroup.getLeftSide(), leftFeatureRef))
											newGroup.getLeftSide().add(leftFeatureRef);
									}
								}
							}
						}
						if(newGroup.getLeftSide().size() >= 2)
							groups.add(newGroup);
					}
				}
			}
		}
		
		// Step 2. Filtering the real groups!
		ArrayList<RightImplicationsGroup> realGroups = new ArrayList<RightImplicationsGroup>();
		for (RightImplicationsGroup group : groups) {
			this.searchRealRightNotImplicationsGroupByCombinatory(realGroups, group);
		}
		
		// Step 3. Creating one constraint for each real group. 
		for (RightImplicationsGroup group : realGroups) {
			LanguageConstraint constraint = this.fromRightNotImplicationToConstraint(group);
			featureModel.getConstraints().add(constraint);
		}
	}
	
	/**
	 * Searches all the possible implications in the implications group in the parameter. 
	 * It explores all the possible combinations of the involved features.
	 * @param realGroups. The parameter in which the result is stored.
	 * @param group. The group under study.
	 */
	private void searchRealRightImplicationsGroupByCombinatory(
			ArrayList<RightImplicationsGroup> realGroups,
			RightImplicationsGroup group) {
		
		if(group.getLeftSide().size() >= 2){
			ArrayList<String> leftFeatures = new ArrayList<String>();
			for (LanguageFeatureRef featureRef : group.getLeftSide()) {
				leftFeatures.add(featureRef.getRef().getName());
			}
			
			// Base case: The current group is a real group
			if(PCMQueryServices.getInstance().allProductsWithFeaturesSetAHaveAlsoFeatureB(leftFeatures, 
					group.getRightSide().getRef().getName())){
				realGroups.add(group);
			}
			
			// Recursive case: Now we need to search for real groups in the combinations of the features of the current real group. 
			for (LanguageFeatureRef currentFeature : group.getLeftSide()) {
				RightImplicationsGroup newGroup = new RightImplicationsGroup(group.getRightSide(), false);
				for (LanguageFeatureRef fRef : group.getLeftSide()) {
					if(!fRef.getRef().getName().equals(currentFeature.getRef().getName()))
						newGroup.getLeftSide().add(fRef);
				}
				this.searchRealRightImplicationsGroupByCombinatory(realGroups, newGroup);
			}
		}
	}
	
	/**
	 * Searches all the possible not-implications in the implications group in the parameter. 
	 * It explores all the possible combinations of the involved features.
	 * @param realGroups. The parameter in which the result is stored.
	 * @param group. The group under study.
	 */
	private void searchRealRightNotImplicationsGroupByCombinatory(
			ArrayList<RightImplicationsGroup> realGroups,
			RightImplicationsGroup group) {
		
		if(group.getLeftSide().size() >= 2){
			ArrayList<String> leftFeatures = new ArrayList<String>();
			for (LanguageFeatureRef featureRef : group.getLeftSide()) {
				leftFeatures.add(featureRef.getRef().getName());
			}
			
			// Base case: The current group is a real group
			if(PCMQueryServices.getInstance().allProductsWithFeaturesSetAExcludeFeatureB(leftFeatures, 
					group.getRightSide().getRef().getName())){
				realGroups.add(group);
			}
			
			// Recursive case: Now we need to search for real groups in the combinations of the features of the current real group. 
			for (LanguageFeatureRef currentFeature : group.getLeftSide()) {
				RightImplicationsGroup newGroup = new RightImplicationsGroup(group.getRightSide(), true);
				for (LanguageFeatureRef fRef : group.getLeftSide()) {
					if(!fRef.getRef().getName().equals(currentFeature.getRef().getName()))
						newGroup.getLeftSide().add(fRef);
				}
				this.searchRealRightNotImplicationsGroupByCombinatory(realGroups, newGroup);
			}
		}
	}

	/**
	 * Creates an implies constraint from the right implication group in the paramter.
	 * @param group
	 * @return
	 */
	private LanguageConstraint fromRightImplicationToConstraint(
			RightImplicationsGroup group) {
		
		LanguageConstraint constraint = VmFactory.eINSTANCE.createLanguageConstraint();
		constraint.setName(group.toString());
		
		BinaryExpression implies = VmFactory.eINSTANCE.createBinaryExpression();
		implies.setOperator(BinaryOperator.IMPLIES);
		implies.setRight(this.cloneLanguageFeatureRef(group.getRightSide()));
		
		BooleanExpression currentExpression = this.cloneLanguageFeatureRef(group.getLeftSide().get(0));
		for (int i = 1; i < group.getLeftSide().size(); i++) {
			BinaryExpression binary = VmFactory.eINSTANCE.createBinaryExpression();
			binary.setOperator(BinaryOperator.AND);
			binary.setLeft(currentExpression);
			binary.setRight(this.cloneLanguageFeatureRef(group.getLeftSide().get(i)));
			currentExpression = binary;
		}
		
		implies.setLeft(currentExpression);
		constraint.setExpression(implies);
		return constraint;
	}

	/**
	 * Creates a not-implies constraint from the right implication group in the paramter.
	 * @param group
	 * @return
	 */
	private LanguageConstraint fromRightNotImplicationToConstraint(
			RightImplicationsGroup group) {
		LanguageConstraint constraint = VmFactory.eINSTANCE.createLanguageConstraint();
		constraint.setName(group.toString());
		
		BinaryExpression implies = VmFactory.eINSTANCE.createBinaryExpression();
		implies.setOperator(BinaryOperator.IMPLIES);
		
		UnaryExpression notImplies = VmFactory.eINSTANCE.createUnaryExpression();
		notImplies.setOperator(UninaryOperator.NOT);
		notImplies.setExpr(this.cloneLanguageFeatureRef(group.getRightSide()));
		implies.setRight(notImplies);
		
		BooleanExpression currentExpression = this.cloneLanguageFeatureRef(group.getLeftSide().get(0));
		for (int i = 1; i < group.getLeftSide().size(); i++) {
			BinaryExpression binary = VmFactory.eINSTANCE.createBinaryExpression();
			binary.setOperator(BinaryOperator.AND);
			binary.setLeft(currentExpression);
			binary.setRight(this.cloneLanguageFeatureRef(group.getLeftSide().get(i)));
			currentExpression = binary;
		}
		
		implies.setLeft(currentExpression);
		constraint.setExpression(implies);
		return constraint;
	}
	
	
	public void groupImplicationsByLeftSide(LanguageFeatureModel featureModel) throws Exception {
		ArrayList<LeftImplicationsGroup> groups = new ArrayList<LeftImplicationsGroup>();
		for (LanguageConstraint constraintI : featureModel.getConstraints()) {
			
			if(constraintI.getExpression() instanceof BinaryExpression
					&& ((BinaryExpression)constraintI.getExpression()).getOperator().getName().equals(BinaryOperator.IMPLIES.getName())){
				
				BinaryExpression impliesI = (BinaryExpression) constraintI.getExpression();
				if(!this.existsGroup(groups, impliesI.getLeft())){
					LeftImplicationsGroup newGroup = new LeftImplicationsGroup(impliesI.getLeft());
					newGroup.getRightSide().add(impliesI.getRight());
					
					for (LanguageConstraint constraintJ : featureModel.getConstraints()) {
						BinaryExpression impliesJ = (BinaryExpression) constraintJ.getExpression();
						if(this.pBooleanExpressionEquals(impliesI.getLeft(), impliesJ.getLeft()) && constraintI != constraintJ){
							newGroup.getRightSide().add(impliesJ.getRight());
						}
					}
					if(newGroup.getRightSide().size() > 0)
						groups.add(newGroup);
				}
			}
		}
		
		featureModel.getConstraints().clear();
		
		for (LeftImplicationsGroup group : groups) {
			LanguageConstraint constraint = this.fromLeftImplicationToConstraint(featureModel.getRootFeature(), group);
			featureModel.getConstraints().add(constraint);
		}
	}
	
	private LanguageConstraint fromLeftImplicationToConstraint(LanguageFeature root, LeftImplicationsGroup group) throws Exception {
		
		LanguageConstraint constraint = VmFactory.eINSTANCE.createLanguageConstraint();
		constraint.setName(group.toString());
		
		BinaryExpression implies = VmFactory.eINSTANCE.createBinaryExpression();
		implies.setOperator(BinaryOperator.IMPLIES);
		implies.setLeft(this.cloneExpression(root, group.getLeftSide()));
		
		BooleanExpression currentExpression = this.cloneExpression(root, group.getRightSide().get(0));
		for (int i = 1; i < group.getRightSide().size(); i++) {
			BinaryExpression binary = VmFactory.eINSTANCE.createBinaryExpression();
			binary.setOperator(BinaryOperator.AND);
			binary.setLeft(currentExpression);
			binary.setRight(this.cloneExpression(root, group.getRightSide().get(i)));
			currentExpression = binary;
		}
		
		implies.setRight(currentExpression);
		constraint.setExpression(implies);
		return constraint;
	}


	// ----------------------------------------------------------
	// Auxiliary Methods
	// ----------------------------------------------------------
	
	private boolean existsGroup(ArrayList<LeftImplicationsGroup> groups,
			BooleanExpression expr) {
		for (LeftImplicationsGroup group : groups) {
			if(this.pBooleanExpressionEquals(group.getLeftSide(), expr)){
				return true;
			}
		}
		return false;
	}
	
	private boolean pBooleanExpressionEquals(BooleanExpression o1, BooleanExpression o2){
		if(o1 instanceof BinaryExpression && o2 instanceof BinaryExpression){
			BinaryExpression o1Binary = (BinaryExpression) o1;
			BinaryExpression o2Binary = (BinaryExpression) o2;
			boolean operator = o1Binary.getOperator().getName().equals(o2Binary.getOperator().getName());
			boolean leftSideEquals = this.pBooleanExpressionEquals(o1Binary.getLeft(), o2Binary.getLeft());
			boolean rightSideEquals = this.pBooleanExpressionEquals(o1Binary.getRight(), o2Binary.getRight());
			return operator && leftSideEquals && rightSideEquals;
		}
		else if(o1 instanceof UnaryExpression && o2 instanceof UnaryExpression){
			UnaryExpression o1Unary = (UnaryExpression) o1;
			UnaryExpression o2Unary = (UnaryExpression) o2;
			boolean operator = o1Unary.getOperator().getName().equals(o2Unary.getOperator().getName());
			boolean expr = this.pBooleanExpressionEquals(o1Unary.getExpr(), o2Unary.getExpr());
			return operator && expr;
		}
		else if(o1 instanceof LanguageFeatureRef && o2 instanceof LanguageFeatureRef){
			LanguageFeatureRef o1FeatureRef = (LanguageFeatureRef) o1;
			LanguageFeatureRef o2FeatureRef = (LanguageFeatureRef) o2;
			return o1FeatureRef.getRef().getName().equals(o2FeatureRef.getRef().getName());
		}
		else{
			return false;
		}
	}
	
	/**
	 * Returns true if there is an implication group in the given list and for the given feature ref.
	 * @param groups
	 * @param featureRef
	 * @return
	 */
	private boolean existsImplicationGroup(
			ArrayList<RightImplicationsGroup> groups,
			LanguageFeatureRef featureRef) {
		for (RightImplicationsGroup group : groups) {
			if(group.getRightSide().getRef().getName().equals(featureRef.getRef().getName()))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the feature reference in the parameter exists within the given list of feature references.
	 * @param refs
	 * @param featureRef
	 * @return
	 */
	private boolean containsFeatureRef(ArrayList<LanguageFeatureRef> refs,
			LanguageFeatureRef featureRef) {
		for (LanguageFeatureRef pFeatureRef : refs) {
			if(pFeatureRef.getRef().getName().equals(featureRef.getRef().getName()))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns an exact clone of the feature reference given in the parameter.
	 * @param pFeatureRef
	 * @return
	 */
	private BooleanExpression cloneLanguageFeatureRef(LanguageFeatureRef pFeatureRef) {
		LanguageFeatureRef clone = VmFactory.eINSTANCE.createLanguageFeatureRef();
		clone.setRef(pFeatureRef.getRef());
		return clone;
	}
	
	/**
	 * Collects all the features of the feature model whose root is given in the parameter.
	 * The collection is returned in the parameter 'arrayList'.
	 * 
	 * @param arrayList
	 * @param root
	 */
	private void collectLanguageFeatures(ArrayList<LanguageFeature> arrayList, LanguageFeature root){
		arrayList.add(root);
		for (LanguageFeature pFeature : root.getChildren()) {
			this.collectLanguageFeatures(arrayList, pFeature);
		}
	}
	

	/**
	 * Clones the feature model in the parameter.
	 * 
	 * @param openFeatureModel
	 * @return
	 * @throws Exception
	 */
	public LanguageFeatureModel cloneFeatureModel(LanguageFeatureModel openFeatureModel) throws Exception {
		LanguageFeatureModel clone = VmFactory.eINSTANCE.createLanguageFeatureModel();
		clone.setName(openFeatureModel.getName());
		clone.setRootFeature(this.cloneFeature(openFeatureModel.getRootFeature()));

		for (LanguageConstraint constraint : openFeatureModel.getConstraints()) {
			LanguageConstraint clonedConstraint = this.cloneConstraint(clone.getRootFeature(), constraint);
			clone.getConstraints().add(clonedConstraint);
		}
		return clone;
	}

	/**
	 * Clones (recursively) the feature in the parameter.
	 * 
	 * @param rootFeature
	 * @return
	 */
	private LanguageFeature cloneFeature(LanguageFeature rootFeature) {
		LanguageFeature clone = VmFactory.eINSTANCE.createLanguageFeature();
		clone.setMandatory(rootFeature.isMandatory());
		clone.setName(rootFeature.getName());

		for (LanguageFeature child : rootFeature.getChildren()) {
			LanguageFeature cloneChild = this.cloneFeature(child);
			clone.getChildren().add(cloneChild);
		}

		for (LanguageFeatureGroup group : rootFeature.getGroups()) {
			LanguageFeatureGroup newGroup = this.cloneFeatureGroup(group);
			clone.getGroups().add(newGroup);

			for (LanguageFeature feature : group.getFeatures()) {
				LanguageFeature groupFeature = this.getLanguageFeatureByName(feature.getName(), clone);
				newGroup.getFeatures().add(groupFeature);
			}
		}
		return clone;
	}

	/**
	 * Clones the group in the parameter.
	 * 
	 * @param group
	 * @return
	 */
	private LanguageFeatureGroup cloneFeatureGroup(LanguageFeatureGroup group) {
		LanguageFeatureGroup clone = VmFactory.eINSTANCE.createLanguageFeatureGroup();
		LanguageFeatureGroupCardinality clonedCardinality = this.cloneFeatureGroupCardinality(group.getCardinality());
		clone.setCardinality(clonedCardinality);
		return clone;
	}

	/**
	 * Clones the feature group cardinality in the parameter.
	 * 
	 * @param cardinality
	 * @return
	 */
	private LanguageFeatureGroupCardinality cloneFeatureGroupCardinality(LanguageFeatureGroupCardinality cardinality) {
		LanguageFeatureGroupCardinality clone = VmFactory.eINSTANCE.createLanguageFeatureGroupCardinality();
		clone.setLowerBound(cardinality.getLowerBound());
		clone.setUpperBound(cardinality.getUpperBound());
		return clone;
	}

	/**
	 * Clones the constraint in the parameter.
	 * 
	 * @param root
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	private LanguageConstraint cloneConstraint(LanguageFeature root, LanguageConstraint constraint) throws Exception {
		LanguageConstraint clone = VmFactory.eINSTANCE.createLanguageConstraint();
		clone.setName(constraint.getName());
		clone.setExpression(this.cloneExpression(root, constraint.getExpression()));
		return clone;
	}

	/**
	 * Clones the expression in the parameter.
	 * 
	 * @param root
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private BooleanExpression cloneExpression(LanguageFeature root, BooleanExpression expression) throws Exception {
		if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) expression;
			BinaryExpression clone = VmFactory.eINSTANCE.createBinaryExpression();
			clone.setLeft(this.cloneExpression(root, binaryExpression.getLeft()));
			clone.setRight(this.cloneExpression(root, binaryExpression.getRight()));
			clone.setOperator(this.getOperator(binaryExpression.getOperator()));
			return clone;
		} else if (expression instanceof LanguageFeatureRef) {
			LanguageFeatureRef featureRef = (LanguageFeatureRef) expression;
			LanguageFeatureRef clone = VmFactory.eINSTANCE.createLanguageFeatureRef();
			clone.setRef(this.getLanguageFeatureByName(featureRef.getRef().getName(), root));
			return clone;
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression punaryExpression = (UnaryExpression) expression;
			UnaryExpression clone = VmFactory.eINSTANCE.createUnaryExpression();
			clone.setExpr(this.cloneExpression(root, punaryExpression.getExpr()));
			clone.setOperator(this.getOperator(punaryExpression.getOperator()));
			return clone;
		} else {
			throw new Exception("Type does not supported!");
		}
	}

	private BinaryOperator getOperator(BinaryOperator operator) {
		if (operator.getName().equals(BinaryOperator.AND.getName()))
			return BinaryOperator.AND;
		else if (operator.getName().equals(BinaryOperator.OR.getName()))
			return BinaryOperator.OR;
		else if (operator.getName().equals(BinaryOperator.IMPLIES.getName()))
			return BinaryOperator.IMPLIES;
		else if (operator.getName().equals(BinaryOperator.XOR.getName()))
			return BinaryOperator.XOR;
		else
			return null;
	}

	private UninaryOperator getOperator(UninaryOperator operator) {
		if (operator.getName().equals(UninaryOperator.NOT))
			return UninaryOperator.NOT;
		else
			return null;
	}

	/**
	 * Finds a LanguageFeature by the name in the features model in the parameter.
	 * 
	 * @param featureName.
	 *            Name of the feature.
	 * @param featuresModelRoot.
	 *            Root of the features model where the feature should be
	 *            searched.
	 * @return
	 */
	private LanguageFeature getLanguageFeatureByName(String featureName, LanguageFeature featureModelRoot) {
		if (featureModelRoot.getName().equals(featureName)) {
			return featureModelRoot;
		}
		for (LanguageFeature feature : featureModelRoot.getChildren()) {
			LanguageFeature found = this.getLanguageFeatureByName(featureName, feature);
			if (found != null)
				return found;
		}
		return null;
	}
}
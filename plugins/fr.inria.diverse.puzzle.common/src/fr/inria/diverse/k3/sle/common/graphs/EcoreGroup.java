package fr.inria.diverse.k3.sle.common.graphs;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;

import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;

/**
 * Class representing a set of vertexes in an ecore graph. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class EcoreGroup {
	
	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private String name;
	private ArrayList<EcoreVertex> vertex;
	private ArrayList<EcoreVertex> requiredVertex;
	private Vertex dependenciesGraphVertex;
	private String metamodelPath;
	private String requiredInterfacePath;
	private String providedInterfacePath;
	private String implementationProjectName;
	private String implementationProjectLocation;

	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public EcoreGroup(String name){
		this.name = name;
		this.vertex = new ArrayList<EcoreVertex>();
		this.requiredVertex = new ArrayList<EcoreVertex>();
	}

	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<EcoreVertex> getVertex() {
		return this.vertex;
	}
	
	public ArrayList<EcoreVertex> getRequiredVertex() {
		return this.requiredVertex;
	}

	public Vertex getDependenciesGraphVertex() {
		return dependenciesGraphVertex;
	}

	public void setDependenciesGraphVertex(Vertex dependenciesGraphVertex) {
		this.dependenciesGraphVertex = dependenciesGraphVertex;
	}
	
	public String getMetamodelPath() {
		return metamodelPath;
	}

	public void setMetamodelPath(String metamodelPath) {
		this.metamodelPath = metamodelPath;
	}

	public String getRequiredInterfacePath() {
		return requiredInterfacePath;
	}

	public void setRequiredInterfacePath(String requiredInterfacePath) {
		this.requiredInterfacePath = requiredInterfacePath;
	}
	
	public String getProvidedInterfacePath() {
		return providedInterfacePath;
	}

	public void setProvidedInterfacePath(String providedInterfacePath) {
		this.providedInterfacePath = providedInterfacePath;
	}
	
	public String getImplementationProjectName() {
		return implementationProjectName;
	}

	public void setImplementationProjectName(String implementationProjectName) {
		this.implementationProjectName = implementationProjectName;
	}
	
	public String getImplementationProjectLocation() {
		return implementationProjectLocation;
	}

	public void setImplementationProjectLocation(
			String implementationProjectLocation) {
		this.implementationProjectLocation = implementationProjectLocation;
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------

	/**
	 * Finds the vertex that matches the ecore type given in the parameter.
	 * @param eType
	 * @param conceptComparisonOperator
	 * @return
	 * 
	 * FIXME: You are not identifying correctly the vertex!
	 */
	public EcoreVertex findVertexByEcoreReference(EClassifier eType,
			ConceptComparison conceptComparisonOperator) {
		for (EcoreVertex ecoreVertex : vertex) {
			if(conceptComparisonOperator.equals(eType, ecoreVertex.getClassifier())){
				return ecoreVertex;
			}
		}
		return null;
	}
}
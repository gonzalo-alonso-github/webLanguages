package com.loqua.business.services.impl.utils.nlp;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un arbol sintactico de un texto determinado; es decir,
 * la descripcion del analisis sintactico de un texto, presentada
 * en estructura de arbol. <br>
 * Si bien la aplicacion hace uso de la API Standford CoreNLP para generar
 * arboles sintacticos (objetos Tree), el arbol representado por clase
 * se distingue del tipo Tree de dicha API, puesto que aqui se
 * omiten las palabras originales del texto analizado, que en los objetos Tree
 * son incluidas como nodos hoja.
 * @author Gonzalo
 */
public class NodeParsedSentence {
	
	/** Nodo padre del arbol (o subarbol) sintactico
	 * representado por esta clase */
	private NodeParsedSentence parent;
	/** Lista de todos los nodos hijos del arbol sintactico
	 * representado por esta clase */
	private List<NodeParsedSentence> children 
			= new ArrayList<NodeParsedSentence>();
	/** Peso del arbol sintactico representado por esta clase; es decir,
	 * el numero de nodos que cuelgan de el */
	private int nodeSize;
	/** Cadena de texto que incluye todos los nodos del arbol
	 * sintactico representado por esta clase. <br>
	 * No tiene saltos de linea, ni retornos de carro, ni tabulaciones. Cada
	 * nodo esta envuelto entre parentesis. */
	private String nodeString;

	// // // // // //
	// CONSTRUCTORES
	// // // // // //
	
	/** Constructor sin parametros. */
	public NodeParsedSentence(){}
	
	/**
	 * Constructor que inicializa los atributos de la clase
	 * @param size el peso del nodo (es decir, del arbol) que se va a generar;
	 * o lo que es igual: el numero de nodos que cuelgan de el
	 * @param str el valor del texto del nodo que se va a generar
	 */
	public NodeParsedSentence(int size, String str){
		this.nodeSize = size;
		this.nodeString = str;
	}
	
	public NodeParsedSentence(int size, String str, NodeParsedSentence parent) {
		this.nodeSize = size;
		this.nodeString = str;
		parent.addChild(this);
    }
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Agrega un nodo (un subarbol) al arbol representado por esta
	 * clase.
	 * @param child nodo que se agrega
	 */
	public void addChild(NodeParsedSentence child){
		child.setParent(this);
        children.add(child);
	}
	
	/**
	 * Obtiene uno de los nodos hijos (uno de los subarboles) del arbol
	 * representado por esta clase.
	 * @param index indice que indica el nodo hijo que se va a devolver
	 * @return el nodo hijo obtenido
	 */
	public NodeParsedSentence getChild(int index) {
		return children.get(index);
	}
	
	/**
	 * Convierte una frase analizada sintacticamente en una estructura de arbol.
	 * @param parentNode nodo arbol (o subarbol) que se construye
	 * @param stringTree la frase que se va a convertir. Solo debe contener
	 * etiquetas POS y parentesis, en el formato de CoreNLP.
	 * @return el arbol construido a partir de la frase indicada
	 */
	public NodeParsedSentence convertStringToTree(NodeParsedSentence parentNode,
			String stringTree) {
		NodeParsedSentence currentNode = new NodeParsedSentence();
		String textNode = "";
		for(int i=0; i<stringTree.length(); i++){
			if( stringTree.charAt(i)=='(' ){
				NodeParsedSentence childNode = convertStringToTree(
						currentNode,stringTree.substring(i+1));
				i += childNode.nodeString.length()-1;
				currentNode.addChild(childNode);
			}
			else if( stringTree.charAt(i)==')' ){
				int currentNodeSize = 1;
				for( NodeParsedSentence childNode : currentNode.getChildren() ){
					textNode += " " + childNode.nodeString;
					currentNodeSize += childNode.getNodeSize();
				}
				currentNode.setNodeString("("+textNode+")");
				currentNode.setNodeSize(currentNodeSize);
				return currentNode;
			}
			else if( stringTree.charAt(i)!=' ' ){
				textNode += stringTree.charAt(i);
			}
		}
		return currentNode;
	}

	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public NodeParsedSentence getParent() {
		return parent;
	}
	public void setParent(NodeParsedSentence parent) {
		this.parent = parent;
	}

	public List<NodeParsedSentence> getChildren() {
		return children;
	}
	public void setChildren(List<NodeParsedSentence> children) {
		this.children = children;
	}

	public int getNodeSize() {
		return nodeSize;
	}
	public void setNodeSize(int nodeSize) {
		this.nodeSize = nodeSize;
	}
	public NodeParsedSentence setNodeSizeThis(int nodeSize) {
		this.nodeSize = nodeSize;
		return this;
	}

	public String getNodeString() {
		return nodeString;
	}
	public void setNodeString(String nodeString) {
		this.nodeString = nodeString;
	}
	public NodeParsedSentence setNodeStringThis(String nodeString) {
		this.nodeString = nodeString;
		return this;
	}
	
	// // // // //
	// TO_SRING
	// // // // //
	
	@Override
	public String toString(){
		String treeString = getStringNodesRecursively(this);
		return treeString;
	}
	
	/**
	 * Halla una cadena de texto que incluye todos los nodos del arbol
	 * sintactico dado, separados por un salto de linea
	 * @param node arbol (o subarbol) cuyos nodos se consultan
	 * @return la cadena obtenida
	 */
	private String getStringNodesRecursively( NodeParsedSentence node ){
		String currentNode = node.getNodeString();
		for( NodeParsedSentence child : node.getChildren() ){
			currentNode += "\n" + getStringNodesRecursively(child);
		}
		return currentNode;
	}
}

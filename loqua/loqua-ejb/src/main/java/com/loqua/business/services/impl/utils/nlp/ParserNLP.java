package com.loqua.business.services.impl.utils.nlp;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.loqua.model.Suggestion;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;

/**
 * Aporta los metodos necesarios para analizar sintacticamente un texto segun
 * su idioma, haciendo uso de la API de Stanford CoreNLP. <br>
 * Ya que los lenguages soportados por la aplicacion (los lenguajes de las
 * noticias) solo son ingles, frances y epa&ntilde;ol, el analizador solo
 * utilizara esos idiomas, con el fin de aprovechar al maximo los recursos
 * consumidos por la aplicacion.
 * @author Gonzalo
 */
public class ParserNLP{
	
	/** Instancia de la clase, para implementar el patron Singleton */
	private static ParserNLP instance = new ParserNLP();
	/** Objeto de la API CoreNLP que permite crear el objeto Document
	 * de un texto (es decir, todas las anotaciones necesarias para su analisis)
	 * en idioma epa&ntilde;ol */
	private StanfordCoreNLP pipelineSpanish;
	/** Objeto de la API CoreNLP que permite crear el objeto Document
	 * de un texto (es decir, todas las anotaciones necesarias para su analisis)
	 * en idioma frances */
	private StanfordCoreNLP pipelineFrench;
	
	/** Mapa de pares clave-valor &lt;String, Integer&gt; del fichero
	 * 'posTagsCoreNLP.properties donde cada elemento
	 * representa una etiqueta de Part-Of-Speech junto a su identificador. <br>
	 * Se utiliza para facilitar la conversion de frases anlizadas en valores
	 * decimales, como hace el metodo {@link #convertParsedToDecimal}*/
	private static Map<String, Integer> mapSupportedTAGsForPOS;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Implementa el patron Singleton para hallar la misma instancia de esta
	 * clase desde cualquier parte del codigo y desde cualquier hilo de
	 * ejecucion
     * @return unica instancia de la propia clase
     */
	public static ParserNLP getInstance() {
		return instance;
	}
	
	/** Constructor sin parametros que inicializa los atributos de la clase */
	public ParserNLP(){
		initPipelineSpanish();
		initPipelineFrench();
		loadMapSupportedTAGsForPOS();
	}
	
	/**
	 * Inicializa la propiedad {@link #pipelineSpanish}. Requiere dependencias
	 * adicionales de la API, que por defecto solo soporta idioma ingles.
	 */
	private void initPipelineSpanish() {
		/*Properties props = StringUtils.argsToProperties(
				"-props", "StanfordCoreNLP-spanish.properties");*/
		/* Para consumir menos memoria no se cargan las propiedades
		por defecto predefinidas en la libreria, sino que se configuran
		'programaticamente' a continuacion. No se necesitan cargar todos los
		tipos de anotaciones, sino solo estos tres: 'tokenize', 'ssplit' y 'pos'
		*/
		Properties props = new Properties();
		// Anotaciones de part-of-speech (y 'tokenizacion'),
		// requeridas para el analisis sintactico por etiquetas (POS tagging):
		props.setProperty("annotators", "tokenize,ssplit,pos");
		// Se 'tokeniza' cada palabra segun las reglas del idioma espanol:
		props.setProperty("tokenize.language", "es");
		// Cargar el modelo sintactico espanol (Spanish POS tagger model):
		props.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/"
			+ "spanish/spanish-distsim.tagger");
		pipelineSpanish = new StanfordCoreNLP(props);
	}
	
	/**
	 * Inicializa la propiedad {@link #pipelineFrench}. Requiere dependencias
	 * adicionales de la API, que por defecto solo soporta idioma frances.
	 */
	private void initPipelineFrench() {
		/*Properties props = StringUtils.argsToProperties(
				"-props", "StanfordCoreNLP-french.properties");*/
		/* Para consumir menos memoria no se cargan las propiedades
		por defecto predefinidas en la libreria, sino que se configuran
		'programaticamente' a continuacion. No se necesitan cargar todos los
		tipos de anotaciones, sino solo estos tres: 'tokenize', 'ssplit' y 'pos'
		*/
		Properties props = new Properties();
		// Anotaciones de part-of-speech (y 'tokenizacion'),
		// requeridas para el analisis sintactico por etiquetas (POS tagging):
		props.setProperty("annotators", "tokenize,ssplit,pos");
		// Se 'tokeniza' cada palabra segun las reglas del idioma frances:
		props.setProperty("tokenize.language", "fr");
		// Cargar el modelo sintactico frances (French POS tagger model):
		props.setProperty("pos.model",
		    "edu/stanford/nlp/models/pos-tagger/french/french.tagger");
		pipelineFrench = new StanfordCoreNLP(props);
	}
	
	/**
	 * Inicializa la lista de etiquetas Part-Of-Speech,
	 * a partir de los datos del fichero 'posTagsCoreNLP.properties'.
	 */
	private void loadMapSupportedTAGsForPOS(){
		mapSupportedTAGsForPOS = new HashMap<String, Integer>();
		String FILE_LOCATION = "/posTagsCoreNLP.properties";
		Properties properties = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(FILE_LOCATION);
			properties.load(in);
			in.close();
			Set<Object> keys = properties.keySet();
			for (Object k : keys) {
				String key = (String) k;
				Integer value = 0;
				value = Integer.parseInt(properties.getProperty(key));
				mapSupportedTAGsForPOS.put(key, value);
			}
		} catch( Exception e) {
			throw new RuntimeException("Properties file cannot be loaded", e);
		}
	}
	
	// // // // // // // // // // // // // // //
	// METODOS DE ANALISIS SINTACTICO EN INGLES
	// // // // // // // // // // // // // // //
	
	/**
	 * Analiza sintacticamente la frase dada en ingles, hallando el resultado
	 * en forma de cadena de texto. <br>
	 * La cadena hallada no contiene tambien las palabras originales
	 * de la frase, sino solo las palabras clave del analisis sintactico.
	 * @param sentence la frase que se analiza
	 * @return la cadena de texto que describe la frase analizada
	 */
	public String parseToStringEnglish(String sentence){
		Tree treeSentence = parseToTreeEnglish(sentence);
		return cleanStringNode( treeSentence.toString() );
	}
	/**
	 * Analiza sintacticamente la frase dada en ingles, hallando el resultado
	 * en una estructura de arbol adaptada a las necesidades de la aplicacion.
	 * <br>Difiere del metodo {@link #parseToTreeEnglish} en que este arbol
	 * no contiene tambien las palabras originales de la frase, sino solo
	 * las palabras clave del analisis sintactico. Ademas, cada nodo almacena
	 * el valor de su peso.
	 * @param sentence la frase que se analiza
	 * @see #parseToTreeEnglish
	 * @return el nodo raiz del arbol que describe la frase analizada
	 */
	public NodeParsedSentence parseToCustomTreeEnglish(String sentence){
		Tree treeSentence = parseToTreeEnglish(sentence);
		return buildCustomTreeRecursively(treeSentence, null);
	}
	/**
	 * Analiza sintacticamente la frase dada en ingles, hallando el resultado
	 * en una estructura de arbol. <br> Cada uno de los nodos hoja
	 * es una de las palabras originales del texto.
	 * @param sentence la frase que se analiza
	 * @return el nodo raiz del arbol que describe la frase analizada
	 */
	private Tree parseToTreeEnglish(String sentence){
		Document doc = tokenizeSentenceEnglish(sentence);
		return parseUniqueSentenceOfDocument(doc);
	}
	/**
	 * Genera el objeto Document, del texto dado en ingles,
	 * necesario para realizar el analisis sintactico.
	 * @param sentence el texto en ingles que se va a analizar
	 * @return el objeto Document obtenido
	 */
	private Document tokenizeSentenceEnglish(String sentence) {
	    Document document = new Document(sentence);
		return document;
	}
	
	// // // // // // // // // // // // // // //
	// METODOS DE ANALISIS SINTACTICO EN ESPANOL
	// // // // // // // // // // // // // // //
	
	/**
	 * Analiza sintacticamente la frase dada en espa&ntilde;ol,
	 * hallando el resultado en forma de cadena de texto. <br>
	 * La cadena hallada no contiene tambien las palabras originales
	 * de la frase, sino solo las palabras clave del analisis sintactico.
	 * @param sentence la frase que se analiza
	 * @return la cadena de texto que describe la frase analizada
	 */
	public String parseToStringSpanish(String sentence){
		Tree treeSentence = parseToTreeSpanish(sentence);
		return cleanStringNode( treeSentence.toString() );
	}
	/**
	 * Analiza sintacticamente la frase dada en espa&ntilde;ol,
	 * hallando el resultado en una estructura de arbol adaptada a
	 * las necesidades de la aplicacion.
	 * <br>Difiere del metodo {@link #parseToTreeSpanish} en que este arbol
	 * no contiene tambien las palabras originales de la frase, sino solo
	 * las palabras clave del analisis sintactico. Ademas, cada nodo almacena
	 * el valor de su peso.
	 * @param sentence la frase que se analiza
	 * @see #parseToTreeSpanish
	 * @return el nodo raiz del arbol que describe la frase analizada
	 */
	public NodeParsedSentence parseToCustomTreeSpanish(String sentence){
		Tree treeSentence = parseToTreeSpanish(sentence);
		return buildCustomTreeRecursively(treeSentence, null);
	}
	/**
	 * Analiza sintacticamente la frase dada en espa&ntilde;ol,
	 * hallando el resultado en una estructura de arbol. <br>
	 * Cada uno de los nodos hoja es una de las palabras originales del texto.
	 * @param sentence la frase que se analiza
	 * @return el nodo raiz del arbol que describe la frase analizada
	 */
	private Tree parseToTreeSpanish(String sentence){
		Document doc = tokenizeSentenceSpanish(sentence);
		return parseUniqueSentenceOfDocument(doc);
	}
	/**
	 * Genera el objeto Document, del texto dado en espa&ntilde;ol,
	 * necesario para realizar el analisis sintactico.
	 * @param sentence el texto en ingles que se va a analizar
	 * @return el objeto Document obtenido
	 */
	private Document tokenizeSentenceSpanish(String sentence) {
		Annotation annotationDoc = new Annotation( sentence );
	    pipelineSpanish.annotate(annotationDoc);
	    Document document = new Document(annotationDoc);
		return document;
	}
	
	// // // // // // // // // // // // // // //
	// METODOS DE ANALISIS SINTACTICO EN FRANCES
	// // // // // // // // // // // // // // //
	
	/**
	 * Analiza sintacticamente la frase dada en frances, hallando el resultado
	 * en forma de cadena de texto. <br>
	 * La cadena hallada no contiene tambien las palabras originales
	 * de la frase, sino solo las palabras clave del analisis sintactico.
	 * @param sentence la frase que se analiza
	 * @return la cadena de texto que describe la frase analizada
	 */
	public String parseToStringFrench(String sentence){
		Tree treeSentence = parseToTreeFrench(sentence);
		return cleanStringNode( treeSentence.toString() );
	}
	/**
	 * Analiza sintacticamente la frase dada en frances, hallando el resultado
	 * en una estructura de arbol adaptada a las necesidades de la aplicacion.
	 * <br>Difiere del metodo {@link #parseToTreeFrench} en que este arbol
	 * no contiene tambien las palabras originales de la frase, sino solo
	 * las palabras clave del analisis sintactico. Ademas, cada nodo almacena
	 * el valor de su peso.
	 * @param sentence la frase que se analiza
	 * @see #parseToTreeFrench
	 * @return el nodo raiz del arbol que describe la frase analizada
	 */
	public NodeParsedSentence parseToCustomTreeFrench(String sentence){
		Tree treeSentence = parseToTreeFrench(sentence);
		return buildCustomTreeRecursively(treeSentence, null);
	}
	/**
	 * Analiza sintacticamente la frase dada en frances, hallando el resultado
	 * en una estructura de arbol. <br> Cada uno de los nodos hoja
	 * es una de las palabras originales del texto.
	 * @param sentence la frase que se analiza
	 * @return el nodo raiz del arbol que describe la frase analizada
	 */
	private Tree parseToTreeFrench(String sentence){
		Document doc = tokenizeSentenceFrench(sentence);
		return parseUniqueSentenceOfDocument(doc);
	}
	/**
	 * Genera el objeto Document, del texto dado en frances,
	 * necesario para realizar el analisis sintactico.
	 * @param sentence el texto en ingles que se va a analizar
	 * @return el objeto Document obtenido
	 */
	private Document tokenizeSentenceFrench(String sentence) {
		Annotation annotationDoc = new Annotation( sentence );
	    pipelineFrench.annotate(annotationDoc);
	    Document document = new Document(annotationDoc);
		return document;
	}

	// // // // // // // // // // // //
	// METODOS DE ANALISIS SINTACTICO
	// // // // // // // // // // // //
	
	/**
	 * Analiza sintacticamente la frase dada (ya sea en ingles, frances
	 * o espa&ntilde;ol) hallando el resultado en forma de cadena de texto.
	 * @param textToParse texto que se aniliza
	 * @param language idioma del texto dado
	 * @return Si el idioma indicado es ingles, frances o espa&ntilde;ol,
	 * devuelve el texto dado, una vez analizado. <br>
	 * Si el idioma indicado no es ninguno de los susodichos,
	 * devuelve una cadena vacia.
	 */
	public String parseToPOSstring(String textToParse, String language) {
		String result = "";
		if( language.equals("en") ){
			result = ParserNLP.getInstance().parseToStringEnglish(textToParse);
		}else if( language.equals("es") ){
			result = ParserNLP.getInstance().parseToStringSpanish(textToParse);
		}else if( language.equals("fr") ){
			result = ParserNLP.getInstance().parseToStringFrench(textToParse);
		}
		return result;
	}
	
	/**
	 * Analiza sintacticamente la frase dada (ya sea en ingles, frances
	 * o espa&ntilde;ol) hallando el resultado en una estructura de arbol.
	 * @param text texto que se aniliza
	 * @param language idioma del texto dado
	 * @return Si el idioma indicado es ingles, frances o espa&ntilde;ol,
	 * devuelve el arbol obtenido. <br>
	 * Si el idioma indicado no es ninguno de los susodichos,
	 * devuelve valor 'null'.
	 */
	public NodeParsedSentence parseToPOStree(String text, String language) {
		NodeParsedSentence result = null;
		if( language.equals("en") ){
			result = ParserNLP.getInstance().parseToCustomTreeEnglish(text);
		}else if( language.equals("es") ){
			result = ParserNLP.getInstance().parseToCustomTreeSpanish(text);
		}else if( language.equals("fr") ){
			result = ParserNLP.getInstance().parseToCustomTreeFrench(text);
		}
		return result;
	}
	
	/**
	 * Halla el objeto Tree de la API CoreNLP que describe el analisis
	 * sintactico de la informacion dada por el parametro recibido.
	 * @param doc objeto Document de la API CoreNLP, que contiene
	 * el texto que se va a analizar y las propiedades necesarias para ello
	 * @return el objeto Tree obtenido
	 */
	private Tree parseUniqueSentenceOfDocument(Document doc) {
		Sentence docSentence = doc.sentence(0);
		// El '.getChild(0)' es para que el arbol no incluya el nodo ROOT,
		// que no aporta nada:
		return docSentence.parse().getChild(0);
	}
	
	/**
	 * Realiza una copia alterada del arbol (objeto Tree) que se recibe
	 * por parametro, omitiendo sus nodos hoja (que son las palabras
	 * del texto original analizado del arbol), y calculando asi el peso
	 * de cada nodo.
	 * @param currentLevelNodes objeto Tree de CoreNLP que representa
	 * el arbol sintactico que se desea copiar
	 * @param parentNode objeto {@link NodeParsedSentence} que contiene la
	 * informacion ya introducida del subarbol que se agrega al arbol devuelto 
	 * @return objeto {@link NodeParsedSentence} que contiene toda la
	 * informacion del objeto Tree indicado, omitiendo sus nodos hoja
	 */
	private NodeParsedSentence buildCustomTreeRecursively(
			Tree currentLevelNodes, NodeParsedSentence parentNode ) {
		NodeParsedSentence currentNode = new NodeParsedSentence();
		String currentLevelString = 
				cleanStringNode( currentLevelNodes.pennString() ).trim();
		// Con este 'if' se evita introducir en el Nodo aquellas palabras:
		if( currentLevelString.charAt(0)=='(' ){
			int correctSize = calculateCorrectSize(currentLevelNodes);
			currentNode
				.setNodeStringThis(currentLevelString)
				.setNodeSizeThis(correctSize);
			if(parentNode!=null){ parentNode.addChild( currentNode ); }
			for(Tree childLevelNodes : currentLevelNodes.children() ){
				buildCustomTreeRecursively( childLevelNodes, currentNode );
			}
		}
		return currentNode;
	}
	
	/**
	 * Puesto que se espera que la cadena indicada sea el valor string de un
	 * objeto Tree, elimina sus saltos de linea, retornos de carro
	 * y tabulaciones. <br>
	 * Dado que el objeto Tree en cada nodo hoja presenta una
	 * de las palabras originales del texto analizado, este metodo tambien
	 * se encarga de eliminar esas palabras.
	 * @param stringNode valor en forma de texto de un objeto Tree de CoreNLP
	 * @return la cadena dada, una vez modificada
	 */
	private String cleanStringNode(String stringNode) {
		// Elimina todas las tabulaciones y saltos de linea de la cadena:
		String regExpTabs = "([\t\n\r ]*[\t\n\r ])";
		stringNode = stringNode.replaceAll(regExpTabs, " ");
		// Elimina el texto original de cada nodo de la cadena: casualmente son
		// todas las palabras que no empiezan por el caracter '(',
		// y que no contienen el caracter ')'
		String regExpText = "( [^\\(][^\\).]*)";
		stringNode = stringNode.replaceAll(regExpText, "");
		return stringNode;
	}
	
	/**
	 * Calcula el peso del arbol o subarbol dado si se omiten sus nodos hoja
	 * @param currentLevelNodes el arbol cuyo peso se calcula
	 * @return el valor calculado
	 */
	private int calculateCorrectSize(Tree currentLevelNodes) {
		int leaveNodesToSubstract = currentLevelNodes.getLeaves().size();
		int incorrectSize = currentLevelNodes.size();
		int correctSize = incorrectSize - leaveNodesToSubstract;
		return correctSize;
	}

	// // // // // // // // // // // // // // // //
	// METODOS PARA ESTIMAR LA SIMILITUD SINTACTICA
	// // // // // // // // // // // // // // // //
	
	public float getMaxSimilarity(Suggestion suggToAdd,
			String parsedSentenceStr) {
		float similToWrongText =
				getSimilarityMeasure(
						parsedSentenceStr,suggToAdd.getParsedWrongText());
		float similToCorrectText =
				getSimilarityMeasure(
						parsedSentenceStr,suggToAdd.getParsedCorrectText());
		float similarity = (similToWrongText>=similToCorrectText) ?
				similToWrongText : similToCorrectText;
		return similarity;
	}
	/**
	 * Estima la medida de la similitud gramatical entre dos frases
	 * ya analizadas sintacticamente. Esa medida es un valor porcentual
	 * que depende del numero de componentes sintacticos que son identicos
	 * entre ambas frases.
	 * @param insertedSentence la frase indicada, ya en formato
	 * de cadena 'Part-Of-Speech' (ya analizada sintacticamente)
	 * @param suggestedSentence la frase que se compara, ya en formato
	 * de cadena 'Part-Of-Speech' (ya analizada sintacticamente). Cuantos mas
	 * componentes sintacticos de esta frase esten tambien dentro de la otra
	 * frase, mayor similitud hay entre ambas.
	 * @return el valor obtenido
	 */
	private float getSimilarityMeasure(String insertedSentence,
			String suggestedSentence) {
		NodeParsedSentence insertedSentenceTree = new NodeParsedSentence()
				.convertStringToTree(null, insertedSentence).getChild(0);
		float similarityMeasure = 0;
		int sizeOfTree = insertedSentenceTree.getNodeSize();
		int sumSizesOfMatchedNodes = getSumSizesRecursively(
				insertedSentenceTree, suggestedSentence, 0);
		similarityMeasure = ( sumSizesOfMatchedNodes*100 )/sizeOfTree;
		similarityMeasure = Math.round(similarityMeasure*100/100);
		
		return similarityMeasure;
	}
	
	/**
	 * Halla el numero maximo de nodos del arbol dado (objeto
	 * {@link NodeParsedSentence}) que tambien se encuentran dentro de
	 * la frase que se compara
	 * @param insertedSentence la frase que se compara, ya en formato
	 * de cadena 'Part-Of-Speech' (ya analizada sintacticamente)
	 * @param suggestedSentenceNode el arbol o subarbol que se evalua
	 * @param sumSizesOfMatchedNodes sumatorio que almacena el numero de
	 * nodos del subarbol dado que han sido encontrados tambien
	 * en la frase que se compara
	 * @return el numero de nodos obtenido
	 */
	private int getSumSizesRecursively(NodeParsedSentence insertedSentenceNode, 
			String suggestedSentence,
			int sumSizesOfMatchedNodes ){
		if( suggestedSentence.contains(insertedSentenceNode.getNodeString()) ){
			sumSizesOfMatchedNodes += insertedSentenceNode.getNodeSize();
		}else{
			for(NodeParsedSentence child : insertedSentenceNode.getChildren()){
				sumSizesOfMatchedNodes = getSumSizesRecursively(
						child, suggestedSentence, sumSizesOfMatchedNodes);
			}
		}
		return sumSizesOfMatchedNodes;
	}
	
	// // // // // //
	// OTROS METODOS
	// // // // // //
	
	/**
	 * Codifica una frase previamente analizada, convirtiendola
	 * en un valor decimal. La frase debe constar unicamente de etiquetas
	 * Part-Of-Speech y de parentesis de apertura y cierre.
	 * @param sentence frase, previamente analizada, que se codifica
	 * @return la frase indicada, una vez convertida a decimal.
	 */
	public BigInteger convertParsedToDecimal(String sentence){
		// Agregar un espacio despues de cada '('
		sentence = sentence.replaceAll("\\(", "\\( ");
		// Agregar un espacio antes de cada ')'
		sentence = sentence.replaceAll("\\)", " \\)");
		/* Se obtiene el array de TAGs. Para ello en vez de split(" ")
		se usa split("( )+") para evitar posibles espacios consecutivos */
		String[] allSentenceTags = sentence.split("( )+");
		String str_sentenceValue = "0";
		for(String tag : allSentenceTags){
			if( mapSupportedTAGsForPOS.get(tag)!=null ){
				int tagValue = mapSupportedTAGsForPOS.get(tag);
				str_sentenceValue += tagValue;
			}
		}
		BigInteger num_sentenceValue = new BigInteger(str_sentenceValue);
		return num_sentenceValue;
	}
}

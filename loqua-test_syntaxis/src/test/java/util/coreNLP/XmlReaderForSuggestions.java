package test.java.util.coreNLP;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.loqua.model.Suggestion;

public class XmlReaderForSuggestions {

	private Document doc;
	private NodeList nodeList;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	public XmlReaderForSuggestions(){
		loadXmlFile();
	}

	private void loadXmlFile(){
		/* Curiosamente el sentences-en.xml tiene que estar ubicado en el raiz
		del proyecto, mientras que el .properties que usa ParserCoreNLP esta
		en el directorio src */
		try{
			File file = new File(".\\suggestions-en.xml");
			DocumentBuilder builder =
					DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.parse(file);
			//nodeList = doc.getChildNodes();
			nodeList = doc.getElementsByTagName("sugg");
		}catch (Exception e) {
			e.printStackTrace();
	    }
	}
	
	// // // // // // // // // // // // // // // //
	// METODOS PARA DEVOLVER LISTA DE SUGERENCIAS
	// // // // // // // // // // // // // // // //
	
	public List<Suggestion> loadSuggestions() {
		List<Suggestion> result = new ArrayList<Suggestion>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node sugg = nodeList.item(i);
			if (sugg.getNodeType() == Node.ELEMENT_NODE) {
				String wrongText = getSentence(sugg, "wrong");
				String wrongParsed = getSentence(sugg, "wrong-parsed");
				String wrongCodeStr = getSentence(sugg, "wrong-code");
				BigInteger wrongCode = new BigInteger(wrongCodeStr);
				String correctText = getSentence(sugg, "correct");
				String correctParsed = getSentence(sugg, "correct-parsed");
				String correctCodeStr = getSentence(sugg, "correct-code");
				BigInteger correctCode = new BigInteger(correctCodeStr);
				
				Suggestion suggToAdd = new Suggestion();
				suggToAdd.setWrongText(wrongText);
				suggToAdd.setParsedWrongText(wrongParsed);
				suggToAdd.setDecimalWrongText(wrongCode);
				suggToAdd.setCorrectText(correctText);
				suggToAdd.setParsedCorrectText(correctParsed);
				suggToAdd.setDecimalCorrectText(correctCode);
				suggToAdd.setLanguage("en");
				suggToAdd.setGeneratedByAdmin(true);
				
				result.add( suggToAdd );
			}
		}
		return result;
	}

	private String getSentence(Node sentencePair, String sentenceType) {
		String strSentence = "";
		for (int j = 0; j < sentencePair.getChildNodes().getLength(); j++) {
			Node sentence = sentencePair.getChildNodes().item(j);
			if( sentence!=null && sentence.getNodeName().equals(sentenceType) ){
				//System.out.println( sentence.getNodeName() );
				strSentence = sentence.getTextContent();
			}
		}
		return strSentence;
	}
}

package testBusiness;

//import junit.framework.TestCase;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.business.services.ServiceSuggestion;
import com.loqua.business.services.locator.LocatorRemoteEjbServices;
import com.loqua.model.Suggestion;

import logging.LoquaLogger;

public class TestSuggestionCRUD {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static Suggestion suggestion;
	private static ServiceSuggestion serviceSugg;
	private static List<Suggestion> suggList;
	
	@BeforeClass
	public static void setUpClass(){
		suggestion = new Suggestion();
		serviceSugg=new LocatorRemoteEjbServices().getServiceSuggestion();
		suggList = new ArrayList<Suggestion>();
	}
	
	@Test
	public void testCreateSuggestion(){
		try {
			// Analizar frase
			suggestion = generateSuggestion();
			String decimalCodeStr=suggestion.getDecimalCorrectText().toString();
			suggList = serviceSugg.getSuggestionsByDecimalCode(
					0, 1, decimalCodeStr);
			int numSuggsBeforeCreate = suggList.size();
			
			// Crear sugerencia
			suggestion = serviceSugg.createSuggestionForTest(suggestion);
			suggList = serviceSugg.getSuggestionsByDecimalCode(
					0, 1, decimalCodeStr);
			int numSuggsAfterCreate = suggList.size();
			assertTrue( numSuggsAfterCreate==numSuggsBeforeCreate+1 );
			/*
			// Solicitar sugerencias
			String sentence = "It is in the center of a remote galaxy.";
			BeanSuggestionToTest beanSugg =
					new BeanSuggestionToTest(sentence, "en");
			beanSugg.loadBestSuggestions();
			Suggestion bestSugg = beanSugg.getBestSuggToReturn();
			assertTrue( bestSugg.getCorrectText()==suggestion.getCorrectText() );
			*/
			// Eliminar sugerencia
			serviceSugg.deleteSuggestionByIdForTest(suggestion.getId());
			suggList=serviceSugg.getSuggestionsByDecimalCode(0,1,decimalCodeStr);
			int numSuggsAfterDelete = suggList.size();
			assertTrue( numSuggsAfterDelete==numSuggsAfterCreate-1 );
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'testCreateSuggestion()'");
		}
	}
	
	private Suggestion generateSuggestion(){
		String wrongText = "It is at center of a remote galaxy.";
		String correctText = "It is in the center of a remote galaxy.";
		/*Suggestion sugg = new Suggestion(wrongText, correctText,
				null, "en", true);*/
		String parsedWrongText =
				"(S (NP (PRP))"
				+ " (VP (VBZ) (PP (IN) (NP (NP (NN))"
				+ " (PP (IN) (NP (DT) (JJ) (NN)))))) (.))";
		String parsedCorrectText =
				"(S (NP (PRP))"
				+ " (VP (VBZ) (PP (IN) (NP (NP (DT) (NN))"
				+ " (PP (IN) (NP (DT) (JJ) (NN)))))) (.))";
		BigInteger decimalWrongText = 
				new BigInteger("1113525601553111130155311362930");
		BigInteger decimalCorrectText =
				new BigInteger("111352560155311113630155311362930");
		Suggestion sugg = new Suggestion();
		sugg.setWrongTextThis(wrongText).setCorrectTextThis(correctText)
			.setParsedWrongTextThis(parsedWrongText)
			.setParsedCorrectTextThis(parsedCorrectText)
			.setDecimalWrongTextThis(decimalWrongText)
			.setDecimalCorrectTextThis(decimalCorrectText)
			.setLanguageThis("en").setGeneratedByAdminThis(true);
		return sugg;
	}
	
	@After
	public void tearDown(){
		suggList.clear();
	}
}

package test.java.testSuggestions;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loqua.model.Suggestion;

import test.java.util.logging.LoquaLogger;
import test.java.util.classesToTest.BeanSuggestionToTest;

public class TestSuggestion {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	private static List<Suggestion> suggList;
	
	@BeforeClass
	public static void setUpClass(){
		suggList = new ArrayList<Suggestion>();
	}
	
	@Test
	public void testCreateSuggestion(){
		try {
			String sentence = "";
			String expectedSugg = "";
			// Solicitar sugerencias
			sentence = "He is in middle of any desperate fight.";
			BeanSuggestionToTest beanSugg =
					new BeanSuggestionToTest(sentence, "en");
			beanSugg.loadBestSuggestions();
			Suggestion bestSugg = beanSugg.getBestSuggToReturn();
			expectedSugg = "It is in the center of a remote galaxy.";
			assertTrue(bestSugg.getCorrectText().equals(expectedSugg));
		} catch (Exception e) {
			e.printStackTrace();
    		log.error("Unexpected Exception at 'testCreateSuggestion()'");
		}
	}
	
	@After
	public void tearDown(){
		suggList.clear();
	}
}

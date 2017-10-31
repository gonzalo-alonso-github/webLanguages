package com.loqua.crawler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.loqua.scheduler.SchedulerCrawler;

import com.loqua.logging.LoquaLogger;
import com.loqua.model.Feed;
import com.loqua.model.ForumThread;

/** Convierte las noticias descargadas por el componente {@link Gatherer}
 * en objetos {@link ForumThread} y los agrupa en una lista
 */
public class Parser {

	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Lista de hilos del foro creados en la ultima hora (es decir,
	 * en la anterior ejecucion programada por el {@link SchedulerCrawler}).
	 * Se utiliza para comprobar que no se procesan noticias repetidas */
	private List<ForumThread> threadsParsedInLastJob;
	
	/** Lista de hilos del foro que se van creando en la actual
	 * ejecucion programada por el {@link SchedulerCrawler}. Se utiliza para
	 * comprobar que no se procesan noticias repetidas, y tambien para devolver
	 * a la vez todos los hilos obtenidos durante esta ejecucion */
	private List<ForumThread> threadsParsedInCurrentJob;
	
	/** Fuente de la que se descargan las noticias */
	private Feed feedToParse;
	
	/** Campos de la noticia original que deben verificarse y procesarse */
	private String[] fieldsToParse = {
			"title","guid","link","pubDate","description" };
	
	/** Array que contiene los patrones de fecha con los que se verifica si
	 * la fecha de las noticias esta bien formada. <br> 
	 * En general todos los Feeds utilizan el mismo patron para la fecha:
	 * "EEE, dd MMM yyyy HH:mm:ss Z". <br>
	 * Ejemplo: "Wed, 4 Jul 2001 12:08:56 -0700".<br>
	 * Pero algunos usan "d" en lugar de "dd",
	 * y otros (el de "Pour La Science") usan "yy" en vez de "yyyy".
	 * Por tanto se ha elegido un patron al que todos se suelen adaptar.
	 * Aun asi, por si acaso, se agregan otros patrones de fecha. */
	private static final String[] datePatterns = { 
		 "EEE, d MMM yy HH:mm:ss Z", //Wed, 4 Jul 2001 12:08:56 -0700
		 "yyyy.MM.dd G 'at' HH:mm:ss z", //2001.07.04 AD at 12:08:56 PDT
		 "EEE, MMM d, ''yy", //Wed, Jul 4, '01
		 "yyyyy.MMMMM.dd GGG hh:mm aaa", //02001.July.04 AD 12:08 PM
		 "yyMMddHHmmssZ", //010704120856-0700
		 "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", //2001-07-04T12:08:56.235-0700
		 "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", //2001-07-04T12:08:56.235-07:00
		 };
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/**
	 * Constructor que inicializa los atributos de la clase
	 * @param threadsLastJob lista de objetos {@link ForumThread}
	 * con la que se inicializa el atributo {@link #threadsParsedInLastJob}
	 */
	public Parser(List<ForumThread> threadsLastJob){
		threadsParsedInLastJob = threadsLastJob;
		threadsParsedInCurrentJob = new ArrayList<ForumThread>();
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Genera una lista de objetos {@link ForumThread} a partir de todas
	 * las noticias dadas de una fuente
	 * @param newsOfFeed objeto NodeList, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene todas las noticias de la fuente
	 * @throws Exception
	 */
	public void parseRawNewsOfFeed( NodeList newsOfFeed )
			throws Exception{
		for( int i = 0; i < newsOfFeed.getLength(); i++ ){
			org.w3c.dom.Element element = (Element) newsOfFeed.item(i);
			ForumThread forumThread = verifiedForumThread(element);
			if( forumThread!=null ){
				threadsParsedInCurrentJob.add(forumThread);
			}
		}
	}

	/** Comprueba que todos los campos de la noticia dada son considerados
	 * aceptables; en tal caso genera un objeto {@link ForumThread} a partir
	 * de ellos.
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de una noticia
	 * @return Si se verifican los datos, devuelve el objeto ForumThread
	 * generado.<br> Si los datos no son aceptables, devuelve null.
	 */
	private ForumThread verifiedForumThread(Element element){
		ForumThread result = null;
		Date verifiedDate = verifyDate(getElementValue(element, "pubDate"));
		if( verifiedDate==null
				|| !verifyNotEmptyFields(element, fieldsToParse)
				|| !verifyNotRepeated(element) ){
			return null;
		}
		result = generateForumThread(element, verifiedDate);
		return result;
	}
	
	/**
	 * Comprueba que la fecha dada de una noticia se considera aceptable.
	 * No se considera aceptable en los siguientes casos: <ul>
	 * <li>La cadena de texto que se recibe como fecha de la noticia, es null
	 * o esta vacia</li>
	 * <li>La fecha dada no cumple el formato de los patrones indicados en 
	 * el atributo {@link #datePatterns}</li>
	 * <li>La fecha dada de la noticia tiene una antiguedad superior a
	 * una hora (es decir, es mas antigua que la anterior ejecucion
	 * programada por el {@link SchedulerCrawler})</li></ul>
	 * @param dateString fecha que se verifica, perteneciente a una noticia
	 * @return fecha de la noticia, o null si esta no se considera aceptable
	 */
	private Date verifyDate(String dateString){
		Date result = null;
		if( dateString==null || dateString.isEmpty() ) return null;
		boolean parsedDate = false;
		for( int i=0; i<datePatterns.length && parsedDate==false; i++ ){
			SimpleDateFormat sdf=new SimpleDateFormat(datePatterns[i],Locale.US);
			try{
				result = sdf.parse( dateString );
				parsedDate=true;
			}catch( ParseException e ){}
		}
		if(	parsedDate==false ){
			// Noticia descartada por formato de fecha incorrecto
			String msg = "date format not recognized. The news was discarded. "
					+ "The Feed was '" + feedToParse.getName() + "'";
			log.debug("'verifyDate()': " + msg);
			return null;
		}
		if( (new Date().getTime()) - result.getTime() >= 3600000 ){
			// Noticia descartada por fecha anterior a una hora
			String msg = "date exceeds the scheduler period, so the news "
					+ "does not belong to this job. The news was discarded. "
					+ "The Feed was '" + feedToParse.getName() + "'";
			log.debug("'verifyDate()': " + msg);
			return null;
		}
		return result;
	}
	
	/**
	 * Comprueba que los campos indicados de la noticia dada no son null
	 * ni estan vacios
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de una noticia
	 * @param fieldsToParse lista de campos de la noticia que se comprueban
	 * @return
	 * 'true' si ninguno de los campos indicados es null o esta vacio <br>
	 * 'false' si alguno de los campos indicados es null o esta vacio
	 */
	private boolean verifyNotEmptyFields(Element element,
			String[] fieldsToParse) {
		try{
		for( int i = 0; i < fieldsToParse.length; i++ ){
			String field = fieldsToParse[i];
			String value = getElementValue(element, field);
			if( value==null || value.isEmpty() ){
				// Noticia descartada por dato vacio
				String msg = "field " + field + " is null or empty. "
						+ "The news was discarded. "
						+ "The Feed was '" + feedToParse.getName() + "'";
				log.debug("'verifyNotEmptyFields()': " + msg);
				return false;
			}
		}
		}catch(Exception e){
			log.error("Unexpected Exception at 'verifyNotEmptyFields()'");
		}
		return true;
	}
	
	/**
	 * 
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de una noticia
	 * @return
	 * 'true' si el campo 'guid' no coincide con el de ninguna noticia anterior
	 * <br>
	 * 'false' si el campo 'guid' coincide con el de alguna noticia anterior
	 */
	private boolean verifyNotRepeated(Element element) {
		if( verifyNotRepeatedGuid(element) && verifyNotRepeatedTitle(element) ){
			return true;
		}
		return false;
	}

	/**
	 * Halla si se considera que la noticia esta repetida, comprobando si
	 * los campos 'guid' y 'title' de la noticia dada no coinciden con
	 * el de ninguna de las noticias ya procesadas en esta ejecucion
	 * ni en la ejecucion anterior.
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de una noticia
	 * @return
	 * 'true' si la noticia no esta repetida <br>
	 * 'false' si la noticia esta repetida
	 */
	private boolean verifyNotRepeatedGuid(Element element) {
		/* La lista "threadsParsedInLastJob" se ha tomado de la bdd
		y contiene solo los Threads que se introdujeron en bdd en la ultima hora.
		No es necesario comprobar repeticiones de Threads mas antiguos que eso,
		porque el metodo "verifyDate" descarto las noticias de mas de una hora*/
		
		String guid = getElementValue(element, "guid");
		String msg = getMsgForRepeatedField("guid");
		for(ForumThread threadOfFeedAlreadyParsed : threadsParsedInCurrentJob){
			if( threadOfFeedAlreadyParsed.getGuid().equals(guid) ){
				// Noticia descartada por repetida
				log.debug("'verifyNotRepeatedGuid()': " + msg);
				return false;
			}
		}
		for(ForumThread threadAlreadyParsedLastHour : threadsParsedInLastJob){
			if( threadAlreadyParsedLastHour.getGuid().equals(guid) ){
				// Noticia descartada por repetida
				log.debug("'verifyNotRepeatedGuid()': " + msg);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Comprueba que el campo 'title' de la noticia dada no coincide con
	 * el de ninguna de las noticias ya procesadas en esta ejecucion
	 * ni en la ejecucion anterior
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de una noticia
	 * @return
	 * 'true' si el campo 'title' no coincide con el de ninguna noticia anterior
	 * <br>
	 * 'false' si el campo 'title' coincide con el de alguna noticia anterior
	 */
	private boolean verifyNotRepeatedTitle(Element element) {
		/* La lista "threadsParsedInLastJob" se ha tomado de la bdd
		y contiene solo los Threads que se introdujeron en bdd en la ultima hora.
		No es necesario comprobar repeticiones de Threads mas antiguos que eso,
		porque el metodo "verifyDate" descarto las noticias de mas de una hora*/
		
		String title = getElementValue(element, "title");
		String msg = getMsgForRepeatedField("title");
		for(ForumThread threadOfFeedAlreadyParsed : threadsParsedInCurrentJob){
			if( threadOfFeedAlreadyParsed.getTitle().equals(title) ){
				// Noticia descartada por repetida
				log.debug("'verifyNotRepeatedTitle()': " + msg);
				return false;
			}
		}
		for(ForumThread threadAlreadyParsedLastHour : threadsParsedInLastJob){
			if( threadAlreadyParsedLastHour.getTitle().equals(title) ){
				// Noticia descartada por repetida
				log.debug("'verifyNotRepeatedTitle()': " + msg);
				return false;
			}
		}
		return true;
	}

	/**
	 * Crea un mensaje de log que indica que una noticia se descarta debido a
	 * que el campo dado coincide con el de otra noticia ya procesada.
	 * @param field campo de la noticia que causa que esta se descarte,
	 * por coincidir con otra noticia. Predeciblemente deberia ser un campo
	 * clave, como el 'guid' o el 'title' de la noticia.
	 * @return mensaje de log generado
	 */
	private String getMsgForRepeatedField(String field) {
		return "Another Thread with the same '" + field
				+ "' has already been Parsed. The news was discarded. "
				+ "The Feed was '" + feedToParse.getName() + "'";
	}

	/**
	 * Genera un objeto {@link ForumThread} a partir de los datos
	 * de la noticia dada
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de una noticia
	 * @param date fecha, ya verificada, de la noticia
	 * @return el objeto ForumThread generado
	 */
	private ForumThread generateForumThread(
			Element element, Date date) {
		ForumThread thread = new ForumThread();
		String guid = limitTextLength(
				190, getElementValue(element, "guid").trim() );
		String title = limitTextLength(
				190, getElementValue(element, "title").trim() );
		String url = limitTextLength(
				190, getElementValue(element, "link").trim() );
		String content = limitTextLength(
				3490, getElementValue(element, "description").trim() );
		thread.setGuidThis(guid).setTitleThis(title).setUrlThis(url)
			.setContentThis(content)
			.setFeedThis(feedToParse).setFeedNameThis(feedToParse.getName())
			.setDateThis(date).setDateLastCommentThis(date)
			.setDateAggregatedThis(new Date());
		return thread;
	}
	
	/**
	 * Halla el valor del campo dado de la noticia indicada
	 * @param parent element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de la noticia
	 * @param field campo de la noticia del cual se obtiene el valor
	 * @return valor de la etiqueta indicda por el parametro 'field' del
	 * elemennto indicado por el parametro 'parent'
	 */
	private String getElementValue(Element parent, String field) {
		NodeList label = parent.getElementsByTagName(field);
		if( label==null ) return null;
		Element element = (Element) label.item(0);
		if( element==null ) return null;
		return getCharacterDataFromElement(element);
	}

	/**
	 * Convierte a una cadena de texto el valor del elemento dado.
	 * @param element objeto Element, extraido de la respuesta xml de la
	 * fuente de noticias, que contiene los datos de un campo de la noticia
	 * @return cadena de texto extraida a partir del valr del elemento dado
	 */
	private String getCharacterDataFromElement(Element element){
		Node child = element.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			// DOMException es una RuntimeException, asi que no se relanza
			return cd.getData();
		}
		return "";
	}
	
	/**
	 * Recorta una cadena de texto limitando su longitud al numero de
	 * caracteres indicado.
	 * @param limit limite maximo de caracteres permitidos en el texto dado,
	 * contando espacios en blanco
	 * @param text texto que se va a recortar
	 * @return todos los primeros caracteres del texto
	 * hasta incluir la posicion indicada por limit
	 */
	private String limitTextLength(int limit, String text){
		if( text.length()>limit ){
			text = text.substring(0, limit);
			text += "...";
		}
		return text;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public List<ForumThread> getThreadsParsedInCurrentJob() {
		return threadsParsedInCurrentJob;
	}
	
	public void setFeed(Feed feed) {
		this.feedToParse = feed;
	}
}

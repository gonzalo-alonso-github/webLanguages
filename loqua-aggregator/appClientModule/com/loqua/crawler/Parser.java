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

import com.loqua.remote.model.Feed;
import com.loqua.remote.model.ForumThread;

public class Parser {

	private List<ForumThread> threadsParsedInLastJob;
	private List<ForumThread> threadsParsedInCurrentJob;
	private Feed feedToParse;
	private String[] fieldsToParse = {
			"title","guid","link","pubDate","description" };
	
	/* En general todos los Feeds utilizan el mismo patron para la fecha:
	"EEE, dd MMM yyyy HH:mm:ss Z". Ejemplo: "Wed, 4 Jul 2001 12:08:56 -0700"
	Pero algunos usan "d" en lugar de "dd",
	y otros (el de "Pour La Science") usan "yy" en vez de "yyyy".
	Por tanto se ha elegido un patron al que todos se suelen adaptar.
	Aun asi, por si acaso, se agregan otros patrones de fecha */
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
	
	public Parser(List<ForumThread> threadsLastJob){
		threadsParsedInLastJob = threadsLastJob;
		threadsParsedInCurrentJob = new ArrayList<ForumThread>();
	}
	
	// // // //
	// METODOS
	// // // //
	
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

	private ForumThread verifiedForumThread(Element element){
		ForumThread result = null;
		Date verifiedDate = verifyDate(getElementValue(element, "pubDate"));
		if( verifiedDate==null
				|| !verifyNotEmptyFields(element, fieldsToParse)
				|| !verifyNotRepeated(getElementValue(element, "guid")) ){
			return null;
		}
		result = generateForumThread(element, verifiedDate);
		return result;
	}
	
	
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
			//TODO Log: noticia es descartada por formato de fecha incorrecto
			return null;
		}
		if( (new Date().getTime()) - result.getTime() >= 3600000 ){
			//TODO Log: noticia es descartada por fecha anterior a una hora
			return null;
		}
		return result;
	}
	
	private boolean verifyNotEmptyFields(Element element,
			String[] fieldsToParse) {
		try{
		for( int i = 0; i < fieldsToParse.length; i++ ){
			String field = fieldsToParse[i];
			String value = getElementValue(element, field);
			if( value==null || value.isEmpty() ){
				//TODO Log: noticia descartada por dato vacio
				return false;
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean verifyNotRepeated(String guid) {
		for(ForumThread threadOfFeedAlreadyParsed : threadsParsedInCurrentJob){
			if( threadOfFeedAlreadyParsed.getGuid().equals(guid) ){
				//TODO Log: noticia descartada por repetida
				return false;
			}
		}
		for(ForumThread threadAlreadyParsedLastHour : threadsParsedInLastJob){
			if( threadAlreadyParsedLastHour.getGuid().equals(guid) ){
				//TODO Log: noticia descartada por repetida
				return false;
			}
		}
		/* La lista "threadsParsedInLastJob" se ha tomado de la bdd
		y contiene solo los Threads que se introdujeron en bdd en la ultima hora.
		No es necesario comprobar repeticiones de Threads mas antiguos que eso,
		porque el metodo "verifyDate" ya descarta las noticias de mas de una hora */
		return true;
	}

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
		/*thread.setGuid(guid);
		thread.setTitle(title);
		thread.setUrl(url);
		thread.setContent(content);
		thread.setFeed(feedToParse);
		thread.setFeedName(feedToParse.getName());
		thread.setDate(date);
		thread.setDateLastComment(date);
		thread.setDateAggregated(new Date());*/
		return thread;
	}
	
	private String getElementValue(Element parent, String field) {
		NodeList label = parent.getElementsByTagName(field);
		if( label==null ) return null;
		Element element = (Element) label.item(0);
		if( element==null ) return null;
		return getCharacterDataFromElement(element);
	}

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
	/*public void setThreadsParsedInCurrentJob(List<ForumThread> threads) {
		threadsParsedInCurrentJob = threads;
	}*/

	public void setFeed(Feed feed) {
		this.feedToParse = feed;
	}
}

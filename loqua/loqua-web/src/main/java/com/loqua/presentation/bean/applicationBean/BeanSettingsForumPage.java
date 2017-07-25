package com.loqua.presentation.bean.applicationBean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;

/**
 * Administra la configuracion de propiedades de la aplicacion,
 * que son iguales para todas las sesiones de usuario,
 * relativas al uso y visualizacion de las paginas del foro.
 * @author Gonzalo
 */
public class BeanSettingsForumPage implements Serializable {
	
	/**
	 * Numero de version de la clase serializable.
	 * @see Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Numero maximo de noticias mostradas en cada pagina del foro.
	 */
	private static final int DEFAULT_NUM_NEWS_PER_PAGE = 20;
	
	/**
	 * Numero maximo de comentarios mostrados en cada hilo (noticia) del foro.
	 */
	private static final int DEFAULT_NUM_COMMENTS_PER_NEWTHREAD = 20;
	
	/**
	 * Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'forum.properties'.
	 */
	private static Map<String, Integer> mapForumProperties;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/**
	 * Construccion del bean.
	 */
	public BeanSettingsForumPage() {
		loadMapForumProperties();
	}

	/**
	 * Destruccion del bean
	 */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Llena la lista de propiedades utilizadas en las vistas relativas al foro,
	 * a partir de los datos del fichero 'forum.properties'.
	 */
	private void loadMapForumProperties() {
		mapForumProperties = new HashMap<String, Integer>();
		String FILE_LOCATION = "/forum.properties";
		Properties properties = new Properties();
		try {
			InputStream in = getClass().getResourceAsStream(FILE_LOCATION);
			properties.load(in);
			in.close();
			Set<Object> keys = properties.keySet();

			for (Object k : keys) {
				String key = (String) k;
				Integer value = 0;
				try{
					value = Integer.parseInt(properties.getProperty(key));
					mapForumProperties.put(key, value);
				}catch (NumberFormatException e) {
					// TODO log
					// No introduce esta propiedad en el Map y continua el bucle
				}
			}
		} catch( Exception e) {
			//throw new RuntimeException("Propeties file can not be loaded", e);
			// TODO log
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/**
	 * Comprueba que la propiedad 'numberOfNewThreadsPerPage'
	 * del fichero 'forum.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: DEFAULT_NUM_NEWS_PER_PAGE.
	 * @return
	 * numero maximo de noticias mostradas en cada pagina del foro.
	 */
	public int getNumNewsPerPage() {
		Integer result = DEFAULT_NUM_NEWS_PER_PAGE;
		if( mapForumProperties != null
				&& ! mapForumProperties.isEmpty() 
				&& mapForumProperties.get("numberOfNewThreadsPerPage")!=null ){
			result = mapForumProperties.get("numberOfNewThreadsPerPage");
			if( result<5 || result>40 ) result = DEFAULT_NUM_NEWS_PER_PAGE;
		}
		return result;
	}
	
	/**
	 * Comprueba que la propiedad 'numberOfNewThreadsPerPage'
	 * del fichero 'forum.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto: DEFAULT_NUM_NEWS_PER_PAGE.
	 * @return
	 * numero maximo de noticias mostradas en cada pagina del foro.
	 */
	public static int getNumNewsPerPageStatic() {
		Integer result = DEFAULT_NUM_NEWS_PER_PAGE;
		if( mapForumProperties != null
				&& ! mapForumProperties.isEmpty() 
				&& mapForumProperties.get("numberOfNewThreadsPerPage")!=null ){
			result = mapForumProperties.get("numberOfNewThreadsPerPage");
			if( result<5 || result>40 ) result = DEFAULT_NUM_NEWS_PER_PAGE;
		}
		return result;
	}

	/**
	 * Comprueba que la propiedad 'numberOfCommentsPerNewThread'
	 * del fichero 'forum.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto:
	 * DEFAULT_NUM_COMMENTS_PER_NEWTHREAD.
	 * @return
	 * numero maximo de comentarios mostrados en cada hilo (noticia) del foro.
	 */
	public int getNumCommentsPerPage() {
		Integer result = DEFAULT_NUM_COMMENTS_PER_NEWTHREAD;
		if( mapForumProperties != null
				&& ! mapForumProperties.isEmpty() 
				&& mapForumProperties.get("numberOfCommentsPerNewThread")
				!=null ){
			result = mapForumProperties.get("numberOfCommentsPerNewThread");
			if( result<1 || result>40 ) result = DEFAULT_NUM_NEWS_PER_PAGE;
		}
		return result;
	}
	
	/**
	 * Comprueba que la propiedad 'numberOfCommentsPerNewThread'
	 * del fichero 'forum.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto:
	 * DEFAULT_NUM_COMMENTS_PER_NEWTHREAD.
	 * @return
	 * numero maximo de comentarios mostrados en cada hilo (noticia) del foro.
	 */
	public static int getNumCommentsPerPageStatic() {
		Integer result = DEFAULT_NUM_COMMENTS_PER_NEWTHREAD;
		if( mapForumProperties != null
				&& ! mapForumProperties.isEmpty() 
				&& mapForumProperties.get("numberOfCommentsPerNewThread")
				!=null ){
			result = mapForumProperties.get("numberOfCommentsPerNewThread");
			if( result<1 || result>40 ) result = DEFAULT_NUM_NEWS_PER_PAGE;
		}
		return result;
	}
	
	/**
	 * Obtiene el Map de propiedades utilizadas en las vistas del foro
	 * @return
	 * Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada
	 * en el fichero 'forum.properties'.
	 */
	public Map<String, Integer> getMapForumProperties(){
		return Collections.unmodifiableMap(mapForumProperties);
	}
	
	public int getNumPagesOfThread(int totalNumberOfListElements){
		// Este metodo se usa por ejemplo en el snippet 'forum_thread.xhtml'
		Integer numberOfListElementsPerPage = getNumCommentsPerPage();
		int sizePaginationBar = ((int)Math.ceil(
				(float)totalNumberOfListElements/numberOfListElementsPerPage));
		return sizePaginationBar;
	}
	
	public static int getNumPagesOfThreadStatic(int totalNumberOfListElements){
		// Este metodo se usa por ejemplo en el filtro 'FilterForumThread'
		Integer numberOfListElementsPerPage = getNumCommentsPerPageStatic();
		int sizePaginationBar = ((int)Math.ceil(
				(float)totalNumberOfListElements/numberOfListElementsPerPage));
		return sizePaginationBar;
	}
}
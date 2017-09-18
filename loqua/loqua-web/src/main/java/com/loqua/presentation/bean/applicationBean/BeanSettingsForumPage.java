package com.loqua.presentation.bean.applicationBean;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.loqua.presentation.logging.LoquaLogger;

/**
 * Administra la configuracion de propiedades de la aplicacion,
 * que son iguales para todas las sesiones de usuario,
 * relativas al uso y visualizacion de las paginas del foro.
 * @author Gonzalo
 */
public class BeanSettingsForumPage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Numero maximo de noticias mostradas en cada pagina del foro */
	private static final int DEFAULT_NUM_NEWS_PER_PAGE = 20;
	
	/** Numero maximo de comentarios mostrados en cada hilo (noticia) del foro
	 */
	private static final int DEFAULT_NUM_COMMENTS_PER_NEWTHREAD = 20;
	
	/** Mapa de pares clave-valor &lt;String, Integer&gt; donde cada elemento
	 * representa una propiedad especificada en el fichero 'forum.properties' */
	private static Map<String, Integer> mapForumProperties;

	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el atributo
	 * {@link #mapForumProperties} */
	public BeanSettingsForumPage() {
		loadMapForumProperties();
	}

	/** Destructor del bean */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Carga la lista de propiedades utilizadas en las vistas
	 * relativas al foro, a partir de los datos del fichero 'forum.properties'.
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
					// No introduce esta propiedad en el Map y continua el bucle
					log.error("NumberFormatException at "
							+ "'loadMapForumProperties()'");
				}
			}
		} catch( Exception e) {
			//throw new RuntimeException("Propeties file can not be loaded", e);
			String msg = "Properties file cannot be loaded";
			log.error("Unexpected Exception at"
					+ "'loadMapForumProperties()': " + msg);
		}
	}
	
	/**
	 * Comprueba que la propiedad 'numberOfNewThreadsPerPage'
	 * del fichero 'forum.properties' esta inicializada con un valor aceptable,
	 * y si es asi devuelve su valor.<br/>
	 * Si no es asi, devuelve el valor por defecto:
	 * {@link #DEFAULT_NUM_NEWS_PER_PAGE}.
	 * @return
	 * numero maximo de noticias mostradas en cada pagina del foro.
	 */
	public int getNumNewsPerPage() {
		return getNumNewsPerPageStatic();
	}
	
	/**
	 * Es la version estatica del metodo {@link #getNumNewsPerPage}.
	 * @return
	 * numero maximo de noticias mostradas en cada pagina del foro
	 * @see #getNumNewsPerPage
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
	 * {@link #DEFAULT_NUM_COMMENTS_PER_NEWTHREAD}.
	 * @return
	 * numero maximo de comentarios mostrados en cada hilo (noticia) del foro.
	 */
	public int getNumCommentsPerPage() {
		return getNumCommentsPerPageStatic();
	}
	
	/**
	 * Es la version estatica del metodo {@link #getNumCommentsPerPage}.
	 * @return
	 * numero maximo de comentarios mostrados en cada hilo (noticia) del foro
	 * @see #getNumCommentsPerPage
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
	 * en el fichero 'forum.properties'
	 */
	public Map<String, Integer> getMapForumProperties(){
		return Collections.unmodifiableMap(mapForumProperties);
	}
	
	/**
	 * Calcula el numero de paginas que deberia mostrar un hilo del foro,
	 * a partir del numero de comentarios que posee (recibido por parametro) y
	 * del numero de comentarios por pagina indicado en el fichero
	 * 'forum.properties'.
	 * @param totalNumberOfComments numero total de comentarios de la noticia
	 * que contiene el hilo del foro
	 * @return el numero de paginas calculado
	 */
	public int getNumPagesOfThread(int totalNumberOfComments){
		// Este metodo se usa por ejemplo en el snippet 'forum_thread.xhtml'
		return getNumPagesOfThreadStatic(totalNumberOfComments);
	}
	
	/**
	 * Es la version estatica del metodo {@link #getNumPagesOfThread}.
	 * @param totalNumberOfComments numero total de comentarios de la noticia
	 * que contiene el hilo del foro
	 * @return el numero de paginas calculado
	 * @see #getNumPagesOfThread
	 */
	public static int getNumPagesOfThreadStatic(int totalNumberOfListElements){
		// Este metodo se usa por ejemplo en el filtro 'FilterForumThread'
		Integer numberOfListElementsPerPage = getNumCommentsPerPageStatic();
		int sizePaginationBar = ((int)Math.ceil(
				(float)totalNumberOfListElements/numberOfListElementsPerPage));
		return sizePaginationBar;
	}
}
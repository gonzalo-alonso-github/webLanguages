package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.FeedCategory;
import com.loqua.model.ForumThread;
import com.loqua.model.Language;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;
import com.loqua.presentation.bean.applicationBean.BeanUtils;
import com.loqua.presentation.logging.LoquaLogger;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de la pagina principal del foro.
 * @author Gonzalo
 */
public class BeanForum implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Manejador de logging */
	private final LoquaLogger log = new LoquaLogger(getClass().getSimpleName());
	
	/** Parametro 'category' recibido en la URL, que indica la categoria
	 * de noticias (o 'hilos') que se desean consultar. <br/>
	 * Se inicializa en la vista 'forum.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private Long category;
	
	/** Parametro 'page' recibido en la URL, que indica el numero de la pagina
	 * que se desea consultar dentro del hilo del foro. <br/>
	 * Se inicializa en la vista 'forum_thread.xhtml',
	 * mediante el &lt;f:viewParam&gt; que invoca al metodo set del atributo. */
	private Integer offsetPage;
	
	/** Numero maximo de noticias (o 'hilos') en cada pagina del foro. */
	private Integer numNewsPerPage;
	
	/** Numero total de noticias (o 'hilos') del foro, pertenecientes
	 * a la categoria dada (ver {@link #category}). */
	private Integer numNewsTotal;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	/** Inyeccion de dependencia del {@link BeanUserData} */
	@ManagedProperty(value="#{beanUserData}")
	private BeanUserData beanUserData;
	
	/** Inyeccion de dependencia del {@link BeanSettingsForumPage} */
	@ManagedProperty(value="#{beanSettingsForumPage}")
	private BeanSettingsForumPage beanSettingsForumPage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los beans inyectados:
	 * {@link BeanLogin}, {@link BeanUser} y {@link BeanSettingsForumPage}
	 */
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanUser();
		initBeanSettingsForum();
		numNewsPerPage = beanSettingsForumPage.getNumNewsPerPage();
	}
	
	/** Inicializa el objeto {@link BeanLogin} inyectado */
	private void initBeanLogin() {
		// Buscamos el BeanLogin en la sesion
		beanLogin = null;
		beanLogin = (BeanLogin)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanLogin");
		// si no existe lo creamos e inicializamos:
		if( beanLogin == null ){
			beanLogin = new BeanLogin();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanLogin", beanLogin);
		}
	}
	
	/** Inicializa el objeto {@link BeanUserData} inyectado */
	private void initBeanUser() {
		// Buscamos el beanUserData en la sesion.
		beanUserData = null;
		beanUserData = (BeanUserData)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get("beanUserData");
		// si no existe lo creamos e inicializamos:
		if (beanUserData == null) { 
			beanUserData = new BeanUserData();
			beanUserData.init();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put("beanUserData", beanUserData);
		}
	}
	
	/** Inicializa el objeto {@link BeanSettingsForum} inyectado */
	private void initBeanSettingsForum() {
		// Buscamos el BeanSettings en la sesion.
		beanSettingsForumPage = null;
		beanSettingsForumPage = (BeanSettingsForumPage)
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("beanSettingsForumPage");
		// si no existe lo creamos e inicializamos:
		if (beanSettingsForumPage == null) { 
			beanSettingsForumPage = new BeanSettingsForumPage();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put(
						"beanSettingsForumPage", beanSettingsForumPage);
		}
	}

	/** Destructor del bean. */
	@PreDestroy
	public void end(){
		clearStatus();
	}

	// // // //
	// METODOS
	// // // //
	
	/**
	 * Consulta todas las categorias de noticias (Feed) disponibles
	 * @return lista de todos los FeedCategories disponibles
	 */
	public List<FeedCategory> getAllFeedCategories(){
		List<FeedCategory> result = new ArrayList<FeedCategory>();
		try{
			result = Factories.getService().getServiceFeed()
					.getAllFeedCategoriesFromDB(); //FromCache()
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getAllFeedCategories()'");
		}
		return result;
	}
	
	/**
	 * Halla los ultimos hilos del foro mas recientes que pertenecen a la categoria dada
	 * @param categoryId atributo 'name' del FeedCategory al que pertenecen
	 * los ForumThread que se consultan
	 * @return lista de los ForumThread cuya fecha es mas reciente,
	 * segun su atributo 'date', pertenecientes al FeedCategory dado
	 */
	public List<ForumThread> getLastNewsByCategory(Long categoryID){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = Factories.getService().getServiceThread()
					.getLastThreadsByCategoryFromDB(categoryID); //FromCache
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getLastNewsByCategory()'");
		}
		return result;
	}
	
	/**
	 * Halla los hilos del foro mas votados por los usuarios en el ultimo mes
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene los mayores valores
	 * del atributo 'countVotes'
	 */
	public List<ForumThread> getMostValuedNewsOfTheMonth(){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = Factories.getService().getServiceThread()
					.getMostValuedThreadsOfTheMonthFromDB();
		}catch( Exception e ){
			log.debug(
					"Unexpected Exception at "
					+ "'getMostValuedNewsOfTheMonth()'");
		}
		return result;
	}
	
	/**
	 * Halla los hilos del foro que tienen mas comentarios de usuarios
	 * desde el ultimo mes
	 * @return lista de los ForumThread publicados en el ultimo mes,
	 * cuyo ForumThreadInfo asociado tiene los mayores valores
	 * del atributo 'countComments'
	 */
	public List<ForumThread> getMostCommentedNewsOfTheMonth(){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = Factories.getService().getServiceThread()
					.getMostCommentedThreadsOfTheMonthFromDB();
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getMostCommentedNewsOfTheMonth()'");
		}
		return result;
	}
	
	/**
	 * Halla los usuarios que mas puntos han obtenido en el foro
	 * en el ultimo mes
	 * @return
	 * Halla los usuarios que mas puntos han obtenido en el foro
	 * en el ultimo mes
	 */
	public List<User> getMostValuedUsersOfTheMonth(){
		List<User> result = new ArrayList<User>();
		try{
			result = Factories.getService().getServiceUser()
					.getMostValuedUsersOfTheMonthFromDB(); // FromCache
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getMostValuedUsersOfTheMonth()'");
		}
		return result;
	}
	
	/**
	 * Halla los usuarios que mas participaciones
	 * (comentarios y correcciones aceptadas) han publicado en el foro
	 * en el ultimo mes
	 * @return lista de los User cuyo UserInfo asociado tiene los mayores
	 * valores del atributo 'countCommentsMonth' sumado a
	 * 'countCorrectionsMonth'
	 */
	public List<User> getMostActiveUsersOfTheMonth(){
		List<User> result = new ArrayList<User>();
		try{
			result = Factories.getService().getServiceUser()
					.getMostActiveUsersOfTheMonthFromDB();
		}catch( Exception e ){
			log.error("Unexpected Exception at "
					+ "'getMostActiveUsersOfTheMonth()'");
		}
		return result;
	}
	
	/**
	 * Halla los hilos del foro pertenecientes a los lenguajes indicados
	 * y a la categoria dada, aplicando un offset y limitando su numero
	 * @return
	 * lista de ForumThread que pertenecen a los Language y al FeedCategory
	 * dados, aplicando el offset y el limite de elementos indicado
	 */
	public List<ForumThread> getListNews() {
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			List<Language> listLanguages = beanUserData.getListUserLanguages();
			if( listLanguages.isEmpty()==false ){
				// si no esta vacia, obtener las noticias mas recientes,
				// de dichos idiomas, de la categoria elegida
				result = Factories.getService().getServiceThread()
						.getThreadsByLanguagesAndCategoryFromDB(
								listLanguages, category, offsetPage,
								numNewsPerPage); //FromCache
				numNewsTotal = Factories.getService().getServiceThread()
						.getNumThreadsByLanguagesAndCategoryFromDB(
								listLanguages, category); //FromCache
			}else{
				// si esta vacia, obtener las noticias mas recientes,
				// de cualquier idioma, de la categoria elegida
				result = Factories.getService().getServiceThread()
						.getThreadsByCategoryFromDB(category,offsetPage,
								numNewsPerPage); //FromCache
				numNewsTotal = Factories.getService().getServiceThread()
						.getNumThreadsByCategoryFromDB(category); //FromCache
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'getListNews()'");
		}
		return result;
	}
	
	/**
	 * Inicializa el atributo {@link #numNewsTotal} asignandole
	 * el numero de hilos del foro pertenecientes a los lenguajes indicados
	 * y a la categoria dada
	 * @return valor del atributo {@link #numNewsTotal}, una vez inicializado
	 */
	public int loadNumNewsTotal() {
		numNewsTotal = 0;
		try{
			List<Language> listLanguages = beanUserData.getListUserLanguages();
			if( listLanguages.isEmpty()==false ){
				// si no esta vacia, obtener las noticias mas recientes,
				// de dichos idiomas, de la categoria elegida
				numNewsTotal = Factories.getService().getServiceThread()
						.getNumThreadsByLanguagesAndCategoryFromDB(
								listLanguages, category);
			}else{
				// si esta vacia, obtener las noticias mas recientes,
				// de cualquier idioma, de la categoria elegida
				numNewsTotal = Factories.getService().getServiceThread()
						.getNumThreadsByCategoryFromDB(category);
			}
		}catch( Exception e ){
			log.error("Unexpected Exception at 'loadNumNewsTotal()'");
		}
		return numNewsTotal;
	}
	
	/**
	 * Obtiene la URL necesaria para que los componentes OutpuLink de la vista
	 * que llaman a este metodo enlacen a la pagina 'forum.xhtml',
	 * indicando en la 'query string' de la URL la categoria se&ntilde;alada
	 * en el atributo {@link #category}. <br/>
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForum'.
	 * @param offset pagina del foro que se va a consultar.
	 * @return la URL de la pagina de 'forum.xhtml'.
	 */
	public String getOutputLinkToForum(Integer offset){
		// Este metodo se llama desde la barra de paginacion del foro
		return getOutputLinkToForum(category, offset);
	}
	
	/**
	 * Obtiene la URL necesaria para que los componentes OutpuLink de la vista
	 * que llaman a este metodo enlacen a la pagina 'forum.xhtml',
	 * indicando en la 'query string' de la URL la categoria dada. <br/>
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForum'.
	 * @param offset pagina del foro que se va a consultar.
	 * @return la URL de la pagina de 'forum.xhtml'.
	 */
	public String getOutputLinkToForum(Long category, Integer offset){
		// Este metodo se llama desde los links de categorias del foro
		String url = getLinkToForum();
		String queryString = "?";
		boolean existParams = false;
		if( category!=null && category!=0L ){
			queryString+="category="+category;
			existParams = true;
		}
		if( offset!=null && offset!=0 ){
			if( existParams ) queryString+="&";
			queryString+="page="+offset;
		}
		return url + queryString;
	}
	
	/**
	 * Halla la URL de la pagina 'forum.xhtml' del tipo del usuario
	 * logueado (sea administrador o usuario registrado, accede a dicha pagina
	 * con su rol correspondiente).
	 * @return la URL hallada
	 */
	private String getLinkToForum(){
		String url = BeanUtils.getUrlUserPages() + "forum.xhtml";
		return url;
	}
	
	/**
	 * Borra el estado del Bean sobreescribiendo las propiedades del mismo
	 * con sus valores por defecto.
	 */
	public void clearStatus() {
		category = null;
		offsetPage = null;
		numNewsTotal = 0;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public BeanLogin getBeanLogin() {
		return beanLogin;
	}
	public void setLoginBean( BeanLogin bLogin ) {
		beanLogin = bLogin;
	}
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long id) {
		category = id;
	}
	public Integer getOffsetPage() {
		if( offsetPage==null ) offsetPage=0;
		return offsetPage;
	}
	public void setOffsetPage(Integer o) {
		offsetPage = o;
	}
	public Integer getNumNewsTotal() {
		if( numNewsTotal==null ) numNewsTotal=0;
		return numNewsTotal;
	}
	public void setNumNewsTotal(int numNewsTotal) {
		this.numNewsTotal = numNewsTotal;
	}
}

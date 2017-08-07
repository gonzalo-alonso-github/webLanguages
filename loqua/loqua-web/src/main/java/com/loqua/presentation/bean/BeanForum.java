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
import com.loqua.model.Language;
import com.loqua.model.ForumThread;
import com.loqua.model.User;
import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;
import com.loqua.presentation.bean.applicationBean.BeanUtils;

public class BeanForum implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long category;
	private Integer offsetPage;
	private Integer numNewsPerPage;
	private Integer numNewsTotal;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanUserData}")
	private BeanUserData beanUserData;
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsForumPage}")
	private BeanSettingsForumPage beanSettingsForumPage;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanLogin();
		initBeanUser();
		initBeanSettingsForum();
		numNewsPerPage = beanSettingsForumPage.getNumNewsPerPage();
	}
	
	private void initBeanLogin() {
		// Buscamos el BeanLogin en la sesion.
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
	
	/**
	 * Inicializa el BeanSettingsForum perteneciente a esta clase.</br>
	 * Si el BeanSettingsForum ya fue inicializado,
	 * simplemente se obtiene del contexto de aplicacion.</br>
	 * Si el BeanSettingsForum no existe en el contexto de aplicacion,
	 * se crea y se guarda en sesion bajo la clave 'beanSettingsForum'.
	 */
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

	@PreDestroy
	public void end(){
		clearStatus();
	}

	// // // //
	// METODOS
	// // // //
	
	public List<FeedCategory> getAllFeedCategories(){
		List<FeedCategory> result = new ArrayList<FeedCategory>();
		try{
			result = Factories.getService().getServiceFeed()
					.getAllFeedCategoriesFromDB(); //FromCache()
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	public List<ForumThread> getLastNewsByCategory(Long categoryID){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = Factories.getService().getServiceThread()
					.getLastThreadsByCategoryFromDB(categoryID); //FromCache
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	public List<ForumThread> getMostValuedNewsOfTheMonth(){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = Factories.getService().getServiceThread()
					.getMostValuedThreadsOfTheMonthFromDB(); //FromCache
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	public List<ForumThread> getMostCommentedNewsOfTheMonth(){
		List<ForumThread> result = new ArrayList<ForumThread>();
		try{
			result = Factories.getService().getServiceThread()
					.getMostCommentedThreadsOfTheMonthFromDB(); //FromCache
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	public List<User> getMostValuedUsersOfTheMonth(){
		List<User> result = new ArrayList<User>();
		try{
			result = Factories.getService().getServiceUser()
					.getMostValuedUsersOfTheMonthFromDB(); // FromCache
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
	public List<User> getMostActiveUsersOfTheMonth(){
		List<User> result = new ArrayList<User>();
		try{
			result = Factories.getService().getServiceUser()
					.getMostActiveUsersOfTheMonthFromDB(); // FromCache
		}catch( Exception e ){
			// TODO Log
		}
		return result;
	}
	
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
			// TODO Log
		}
		return result;
	}
	
	public int loadNumNewsTotal() {
		numNewsTotal = 0;
		try{
			List<Language> listLanguages = beanUserData.getListUserLanguages();
			if( listLanguages.isEmpty()==false ){
				// si no esta vacia, obtener las noticias mas recientes,
				// de dichos idiomas, de la categoria elegida
				numNewsTotal = Factories.getService().getServiceThread()
						.getNumThreadsByLanguagesAndCategoryFromDB(
								listLanguages, category);  //FromCache
			}else{
				// si esta vacia, obtener las noticias mas recientes,
				// de cualquier idioma, de la categoria elegida
				numNewsTotal = Factories.getService().getServiceThread()
						.getNumThreadsByCategoryFromDB(category); //FromCache
			}
		}catch( Exception e ){
			// TODO Log
		}
		return numNewsTotal;
	}
	
	/**
	 * Obtiene la url necesaria para que los componentes 'h:outpuLink'
	 * que llaman a este metodo enlacen a la pagina 'forum.xhtml'.
	 * Antes de acceder a dicha pagina se aplicara el filtro
	 * 'FilterForum'.
	 * @param offset pagina del foro que se va a consultar.
	 * @return url de la pagina de 'forum.xhtml'.
	 */
	public String getOutputLinkToForum(Integer offset){
		// Este metodo se llama desde la barra de paginacion del foro
		return getOutputLinkToForum(category, offset);
	}
	
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
	
	private String getLinkToForum(){
		String url = BeanUtils.getUrlUserPages() + "forum.xhtml";
		return url;
	}
	
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

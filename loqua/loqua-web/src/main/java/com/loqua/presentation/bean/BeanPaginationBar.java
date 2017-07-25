package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;

public class BeanPaginationBar implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// Inyeccion de dependencia
	@ManagedProperty(value="#{beanSettingsForum}")
	private BeanSettingsForumPage beanSettingsForum;
		
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		initBeanSettingsForum();
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
		beanSettingsForum = null;
		beanSettingsForum = (BeanSettingsForumPage)
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("beanSettingsForum");
		
		// si no existe lo creamos e inicializamos:
		if (beanSettingsForum == null) { 
			beanSettingsForum = new BeanSettingsForumPage();
			FacesContext.getCurrentInstance().getExternalContext().
				getSessionMap().put(
						"beanSettingsForum", beanSettingsForum);
		}
	}
	
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA CARGAR LA BARRA PRINCIPAL DE PAGINACION
	// // // // // // // // // // // // // // // // // //
	
	public int loadSizeMainPaginationBar(int totalNumberOfListElements){
		Integer numberOfListElementsPerPage = 
				beanSettingsForum.getNumNewsPerPage();
		int sizePaginationBar = ((int)Math.ceil(
				(float)totalNumberOfListElements/numberOfListElementsPerPage));
		return sizePaginationBar;
	}
	
	public int loadTypeMainPaginationBar(
			int sizePaginationBar, Integer selectedPage){
		int typePaginationBar = loadTypePaginationBar(
				sizePaginationBar, selectedPage);
		return typePaginationBar;
	}
	
	// // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA CARGAR LA BARRA DE PAGINACION DE CADA NOTICIA DEL FORO
	// // // // // // // // // // // // // // // // // // // // // // //
	
	public int loadTypeSingleNewThreadPaginationBar(
			int sizePaginationBar, Integer selectedPage){
		int typePaginationBar = loadTypePaginationBar(
				sizePaginationBar, selectedPage);
		return typePaginationBar;
	}
	
	private int loadTypePaginationBar(
			int sizePaginationBar, Integer selectedPage) {
		int typePaginationBar = 0;
		if( sizePaginationBar<=1 ){
			typePaginationBar = 0;
		}else if( sizePaginationBar<=7 ){
			typePaginationBar = 1;
			//getListNumbersForSimpleTypePaginationBar();
		}else if( sizePaginationBar>7 ){
			if( selectedPage==null || selectedPage<=2 
					|| selectedPage>=sizePaginationBar-1 ){
				typePaginationBar = 2;
			}else if( selectedPage==3 ){
				typePaginationBar = 3;
			}else if( selectedPage==sizePaginationBar-2 ){
				typePaginationBar = 4;
			}else{
				typePaginationBar = 5;
			}
		}
		return typePaginationBar;
	}
	
	public List<Integer> loadListNumbersForSimpleTypePaginationBar(
			int sizePaginationBar){
		List<Integer> result = new ArrayList<Integer>();
		for( int i=1; i<=sizePaginationBar; i++ ){
			result.add(i);
		}
		return result;
	}
	
	public boolean markButton(int buttonBar, int currentPage) {
		if( buttonBar==currentPage ){
			return true;
		}
		return false;
	}
}

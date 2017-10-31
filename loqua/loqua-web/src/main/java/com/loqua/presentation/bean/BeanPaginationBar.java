package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.presentation.bean.applicationBean.BeanSettingsForumPage;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de la barra de paginacion principal del foro,
 * y la barra de paginacion de cada hilo del foro.
 * @author Gonzalo
 */
public class BeanPaginationBar implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Inyeccion de dependencia del {@link BeanSettingsForumPage} */
	@ManagedProperty(value="#{beanSettingsForum}")
	private BeanSettingsForumPage beanSettingsForum;
		
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado
	 * {@link BeanSettingsForumPage} */
	@PostConstruct
	public void init() {
		initBeanSettingsForum();
	}
	
	/** Inicializa el objeto {@link BeanSettingsForumPage} inyectado */
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
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // // // // // // // // // // // // // // // //
	// METODOS PARA CARGAR LA BARRA PRINCIPAL DE PAGINACION
	// // // // // // // // // // // // // // // // // //
	
	/**
	 * Calcula el numero maximo de paginas que debe incluir
	 * la barra de paginacion que se muestra en la lista de noticias del foro
	 * (vista 'forum.xhtml').
	 * @param totalNumberOfListElements numero total de noticias del foro
	 * @return numero maximo de paginas hallado
	 */
	public int loadSizeMainPaginationBar(int totalNumberOfListElements){
		Integer numberOfListElementsPerPage = 
				beanSettingsForum.getNumNewsPerPage();
		int sizePaginationBar = ((int)Math.ceil(
				(float)totalNumberOfListElements/numberOfListElementsPerPage));
		return sizePaginationBar;
	}
	
	/**
	 * Halla la manera en que se mostrara la barra de paginacion que se muestra
	 * en la lista de noticias del foro (vista 'forum.xhtml'). <br>
	 * Eso depende de la pagina que haya sido seleccionada por el usuario,
	 * y del tama&ntilde;o de la barra. Ejemplos: <br>
	 * <ul><li>Si hay 8 paginas, y el usuario selecciona la 1 o la 2,
	 * o la 7 o la 8, la barra se mostrara asi: <br> &nbsp;&nbsp;&nbsp;&nbsp;
	 * [1] [2] [3] [...] [6] [7] [8]
	 * </li><li>Si hay 8 paginas, y el usuario selecciona la 4,
	 * la barra se mostrara asi: <br> &nbsp;&nbsp;&nbsp;&nbsp;
	 * [1] [...] [3] [4] [5] [...] [8]
	 * </li><li>Si hay 8 paginas, y el usuario selecciona la 3,
	 * la barra se mostrara asi: <br> &nbsp;&nbsp;&nbsp;&nbsp;
	 * [1] [2] [3] [4] [...] [7] [8]
	 * </li></ul>
	 * @param sizePaginationBar numero maximo de paginas que incluye
	 * la barra de paginacion
	 * @param selectedPage pagina seleccionada por el usuario
	 * @return numero entero que identifica el tipo de la barra de paginacion
	 */
	public int loadTypeMainPaginationBar(
			int sizePaginationBar, Integer selectedPage){
		int typePaginationBar = loadTypePaginationBar(
				sizePaginationBar, selectedPage);
		return typePaginationBar;
	}
	
	// // // // // // // // // // // // // // // // // // // // // // //
	// METODOS PARA CARGAR LA BARRA DE PAGINACION DE CADA NOTICIA DEL FORO
	// // // // // // // // // // // // // // // // // // // // // // //
	
	/**
	 * Halla la manera en que se mostrara la barra de paginacion que se muestra
	 * en la lista de comentarios de un hilo del foro
	 * (vista 'forum_thread.xhtml'). <br>
	 * Eso depende de la pagina que haya sido seleccionada por el usuario,
	 * y del tama&ntilde;o de la barra. Ejemplos: <br>
	 * <ul><li>Si hay 8 paginas, y el usuario selecciona la 1 o la 2,
	 * o la 7 o la 8, la barra se mostrara asi: <br> &nbsp;&nbsp;&nbsp;&nbsp;
	 * [1] [2] [3] [...] [6] [7] [8]
	 * </li><li>Si hay 8 paginas, y el usuario selecciona la 4,
	 * la barra se mostrara asi: <br> &nbsp;&nbsp;&nbsp;&nbsp;
	 * [1] [...] [3] [4] [5] [...] [8]
	 * </li><li>Si hay 8 paginas, y el usuario selecciona la 3,
	 * la barra se mostrara asi: <br> &nbsp;&nbsp;&nbsp;&nbsp;
	 * [1] [2] [3] [4] [...] [7] [8]
	 * </li></ul>
	 * @param sizePaginationBar numero maximo de paginas que incluye
	 * la barra de paginacion
	 * @param selectedPage pagina seleccionada por el usuario
	 * @return numero entero que identifica el tipo de la barra de paginacion
	 */
	public int loadTypeSingleNewThreadPaginationBar(
			int sizePaginationBar, Integer selectedPage){
		int typePaginationBar = loadTypePaginationBar(
				sizePaginationBar, selectedPage);
		return typePaginationBar;
	}
	
	/**
	 * Halla la manera en que se mostrara cualquier barra de paginacion,
	 * ya sea la de la pagina principal del foro, o la de una noticia del foro.
	 * @param sizePaginationBar numero maximo de paginas que incluye
	 * la barra de paginacion
	 * @param selectedPage pagina seleccionada por el usuario
	 * @return numero entero que identifica el tipo de la barra de paginacion
	 */
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
	
	/**
	 * Carga la lista de paginas que debe incluir
	 * la barra de paginacion que se muestra en la lista de comentarios
	 * de una noticia ('hilo') del foro (vista 'forum_thread.xhtml'). <br>
	 * @param sizePaginationBar numero total de paginas que incluye la barra
	 * @return la lista generada
	 */
	public List<Integer> loadListNumbersForSimpleTypePaginationBar(
			int sizePaginationBar){
		List<Integer> result = new ArrayList<Integer>();
		for( int i=1; i<=sizePaginationBar; i++ ){
			result.add(i);
		}
		return result;
	}
	
	/**
	 * Indica si, al seleccionar uno de los botones (paginas) de una
	 * barra de paginacion, deberia realizarse alguna accion o no.
	 * @param buttonBar el boton (o numero de pagina) seleccionado en la barra
	 * @param currentPage la pagina que estaba seleccionada actualmente
	 * @return
	 * 'true': si el usuario ha seleccionado en la barra una pagina distinta
	 * a la que ya estaba seleccionada <br>
	 * 'false': si el usuario ha seleccionado en la barra la misma pagina
	 * que ya estaba seleccionada.
	 */
	public boolean markButton(int buttonBar, int currentPage) {
		if( buttonBar==currentPage ){
			return true;
		}
		return false;
	}
}

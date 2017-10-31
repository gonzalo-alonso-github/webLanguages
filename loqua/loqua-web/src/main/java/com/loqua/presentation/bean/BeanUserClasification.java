package com.loqua.presentation.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.loqua.infrastructure.Factories;
import com.loqua.model.User;

/**
 * Bean encargado de realizar todas las operaciones
 * relativas al manejo de la clasificacion de puntos de usuarios.
 * @author Gonzalo
 */
public class BeanUserClasification implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Indica la lista clasificatoria de los 5 usuarios mas proximos
	 * al usuario logueado, incluyendolo */
	Map<Integer, User> smallClasificationByLoggedUser;
	
	/** Inyeccion de dependencia del {@link BeanLogin} */
	@ManagedProperty(value="#{beanLogin}")
	private BeanLogin beanLogin;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa el bean inyectado {@link BeanLogin} */
	@PostConstruct
	public void init() {
		initBeanLogin();
		initSmallClasificationByLoggedUser();
	}
	
	/** Inicializa el objeto {@link BeanLogin} inyectado */
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

	/** Inicializa el atributo {@link #smallClasificationByLoggedUser} */
	private void initSmallClasificationByLoggedUser(){
		if( smallClasificationByLoggedUser==null ){
			smallClasificationByLoggedUser = getSmallClasificationFromDB(
					beanLogin.getLoggedUser() );
		}
	}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Halla la lista clasificatoria de los 5 usuarios mas proximos
	 * al usuario indicado.
	 * @param user usuario cuya clasificacion se desea consultar
	 * @return lista de User mas proximos al usuario dado
	 * en la clasificacion de puntos
	 */
	public Map<Integer, User> getSmallClasificationByUser(User user){
		if( beanLogin.getLoggedUser().getId()==user.getId() ){
			return getSmallClasificationByLoggedUser();
		}else{
			return getSmallClasificationFromDB(user);
		}
	}

	/**
	 * Refactoriza parte del codigo del metodo
	 * {@link #getSmallClasificationByUser}, consultando la clasificacion
	 * en el caso de que el usuario dado no sea el mismo que ha iniciado sesion
	 * @param user usuario cuya clasificacion se desea consultar
	 * @return lista de User mas proximos al usuario dado
	 * en la clasificacion de puntos
	 */
	private Map<Integer, User> getSmallClasificationFromDB(User user) {
		Map<Integer, User> mapClasification = new HashMap<Integer, User>();
		mapClasification = Factories.getService().getServiceUser()
				.getSmallClasificationByUser(user);
		return mapClasification;
	}
	
	/**
	 * Comprueba si el usuario que esta en la posicion indicada de la
	 * clasifiacion esta el en ultimo lugar.
	 * @param position posicion de un usuario en la clasificacion de puntos
	 * @return
	 * 'true' si el usuario es el ultimo clasificado <br>
	 * 'false' si el usuario no es el ultimo clasificado
	 */
	public boolean isTheVeryLastUser( int position ){
		int numRegisteredUsers = Factories.getService().getServiceUser()
				.getNumRegisteredUsersAndAdminFromDB(); // FromCache()
		if(position==numRegisteredUsers) return true;
		return false;
	}
	
	/**
	 * Actualiza el atributo {@link #smallClasificationByLoggedUser};
	 * esto es, la clasificacion mostrada en la pagina
	 */
	public void reloadSmallLoggedUserClasification(){
		smallClasificationByLoggedUser=null;
		smallClasificationByLoggedUser = getSmallClasificationFromDB(
				beanLogin.getLoggedUser() );
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Map<Integer, User> getSmallClasificationByLoggedUser(){
		initSmallClasificationByLoggedUser();
		return smallClasificationByLoggedUser;
	}
	public void setSmallClasificationByLoggedUser(Map<Integer, User> clasif){
		smallClasificationByLoggedUser = clasif;
	}
}

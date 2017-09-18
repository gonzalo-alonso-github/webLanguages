package com.loqua.presentation.bean.requestBean;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

/**
 * Bean encargado de guardar el resultado de cada accion
 * realizada por otros beans.
 * @author Gonzalo
 */
public class BeanActionResult implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	/** Indica si la accion realizada ha finalizado */
	private boolean finish;
	/** Indica si la accion realizada ha finalizado con exito */
	private boolean success;
	/** Mensaje que describe el resultado de la accion realizada,
	 * listo para mostrarse en la vista */
	private String msgActionResult;
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	/** Constructor del bean. Inicializa los atributos de la clase. */
	@PostConstruct
	public void init() {
		finish = false;
		success = false;
	}
	
	/** Destructor del bean. */
	@PreDestroy
	public void end(){}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Metodo 'get' del atributo {@link #msgActionResult}
	 * @return el atributo {@link #msgActionResult}
	 */
	public String getMsgActionResult() {
		String message = msgActionResult;
		// msgActionResult = null;
		return message;
	}
	
	/**
	 * Metodo 'set' del atributo {@link #msgActionResult}.
	 * @param message palabra clave que, en los ficheros .properties de
	 * internacionalizacion, indica el texto que sobrescribe el valor del
	 * atributo {@link #msgActionResult}
	 * {@link #msgActionResult}
	 */
	public void setMsgActionResult(String message) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ResourceBundle bundle = facesContext.getApplication()
	    		.getResourceBundle(facesContext, "msgs");
	    this.msgActionResult = bundle.getString(message);
	}
	
	/**
	 * Metodo 'set' del atributo {@link #msgActionResult}.
	 * @param message texto cuyo valor exacto (sin alteraciones, a diferencia
	 * del metodo {@link #setMsgActionResult}) sobrescribe el atributo
	 * {@link #msgActionResult}
	 */
	public void setMsgActionResultExact(String message) {
		this.msgActionResult = message;
	}
	
	/** Establece el atributo {@link #msgActionResult} a 'null' */
	public void clearMsgResult(){
		msgActionResult = null;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public boolean getFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
	    this.finish = finish;
	}
	
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
	    this.success = success;
	}
}

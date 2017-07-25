package com.loqua.presentation.bean.requestBean;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

public class BeanActionResult implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private boolean finish;
	private boolean success;
	private String msgActionResult;
	
	
	// // // // // // // // // // // //
	// CONSTRUCTORES E INICIALIZACIONES
	// // // // // // // // // // // //
	
	@PostConstruct
	public void init() {
		finish = false;
		success = false;
	}
	
	@PreDestroy
	public void end(){}
	
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
	
	public String getMsgActionResult() {
		String message = msgActionResult;
		// msgActionResult = null;
		return message;
	}
	public void setMsgActionResult(String message) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ResourceBundle bundle = facesContext.getApplication()
	    		.getResourceBundle(facesContext, "msgs");
	    this.msgActionResult = bundle.getString(message);
	}
	public void setMsgActionResultExact(String message) {
		this.msgActionResult = message;
	}
}

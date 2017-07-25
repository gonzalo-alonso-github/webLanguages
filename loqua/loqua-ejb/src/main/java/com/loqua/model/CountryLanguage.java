package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.model.compoundkeys.CountryLanguageKey;

@SuppressWarnings("serial")
@XmlRootElement(name = "countryLanguage")
@Entity
@Table(name="CountryLanguage")
@IdClass(CountryLanguageKey.class)
public class CountryLanguage implements Serializable{

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@Id @GeneratedValue @ManyToOne private Country country;
	@Id @GeneratedValue @ManyToOne private Language language;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public CountryLanguage(){}
	
	public CountryLanguage(Country coun, Language lan){
		country = coun;
		language = lan;
		coun._getCountryLanguages().add( this );
		lan._getCountryLanguages().add( this );
	}
	
	public void unlink(){
		country._getCountryLanguages().remove( this );
		language._getCountryLanguages().remove( this );
		
		country = null;
		language = null;
	}
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (GETTERS & SETTERS)
	// // // // // // // // // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/** Relacion entre entidades:<br>
	 *  1 Country <--> * CountryLanguages <--> 1 Language 
	 */
	public Country getCountry() {
		return country;
	}
	void _setCountry( Country coun ) {
		country = coun;
	}
	public Language getLanguage() {
		return language;
	}
	void _setLanguage( Language lan ) {
		language = lan;
	}
}

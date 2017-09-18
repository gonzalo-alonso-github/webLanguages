package com.loqua.remote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.loqua.remote.model.compoundkeys.CountryLanguageKey;

/**
 * Representa la informacion del idioma hablado en un pais.
 * Es una clase asociativa entre {@link Country} y {@link Language}
 * @author Gonzalo
 */
@XmlRootElement(name = "countryLanguage")
@Entity
@Table(name="CountryLanguage")
@IdClass(CountryLanguageKey.class)
public class CountryLanguage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Pais que se relaciona con el lenguaje */
	@Id @GeneratedValue @ManyToOne private Country country;
	
	/** Lenguaje que se relaciona con el pais */
	@Id @GeneratedValue @ManyToOne private Language language;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public CountryLanguage(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param country objeto Country asociado al CountryLanguage
	 * @param language objeto Language asociado al CountryLanguage
	 */
	public CountryLanguage(Country country, Language language){
		this.country = country;
		this.language = language;
		country._getCountryLanguages().add( this );
		language._getCountryLanguages().add( this );
	}

	/** Desasigna de esta entidad a las entidades asociadas */
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
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	/* Relacion entre entidades:
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

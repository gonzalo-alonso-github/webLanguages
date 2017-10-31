package com.loqua.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.loqua.business.services.impl.utils.nlp.ParserNLP;

/**
 * Representa una sugerencia, que la aplicacion puede ofrecer al usuario,
 * cuando este solicita ayuda respecto a la sintaxis de una frase dudosa
 * que introduce. <br> Las sugerencias son creadas a partir de los objetos
 * {@link Correction} generados previamente por otros usuarios, de tal manera
 * que son presentadas como ejemplos de correcciones que pueden servir de ayuda
 * a la hora de conocer si una determinada frase esta sintacticamente
 * bien construida o no.
 * @author Gonzalo
 */
@XmlRootElement(name = "suggestion")
@Entity
@Table(name="Suggestion")
public class Suggestion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	
	/** Identificador del objeto y clave primaria de la entidad. */
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	/** Texto de la frase corregida de la Correction. */
	private String correctText;
	/** Texto de la frase original de la Correction. */
	private String wrongText;
	/** Sintaxis de la frase corregida. <br> Es el valor de
	 * {@link #correctText} convertido a 'part-of-speech tagging'
	 * (tambien llamado 'POS tagging'), segun la API CoreNLP. */
	private String parsedCorrectText;
	/** Sintaxis de la frase original. <br> Es el valor de
	 * {@link #wrongText} convertido a 'part-of-speech tagging'
	 * (tambien llamado 'POS tagging'), segun la API CoreNLP. */
	private String parsedWrongText;
	/** Valor decimal de la frase corregida. Resulta util
	 * como indice para agilizar las busquedas de Suggestions. <br>
	 * Es el valor de {@link #parsedCorrectText} tras convertirlo
	 * a su valor decimal. */
	private BigInteger decimalCorrectText = BigInteger.valueOf(0);
	/** Valor decimal de la frase original de la Correction. Resulta util
	 * como indice para agilizar las busquedas de Suggestions. <br>
	 * Es el valor de {@link #parsedWrongText} tras convertirlo
	 * a su valor decimal. */
	private BigInteger decimalWrongText = BigInteger.valueOf(0);
	/** Codigo de dos letras que identifica el lenguaje de la sugerencia.
	 * No tiene por que estar relacionado con la entidad {@link Language}.
	 * Es util para agilizar las busquedas de Suggestions: cuando el usuario
	 * solicita sugerencias para una frase, se procesan unicamente las
	 * Suggestion cuyo atributo 'language' coincida con la frase introducida */
	private String language;
	/**
	 * Si es 'true', indica que la sugerencia ha sido introducida por el
	 * el administrador directamente a la base de datos, de forma externa
	 * a la aplicacion. En tal caso no es necesario que tenga una correccion
	 * asociada. <br> Si es 'false', indica que la sugerencia ha sido
	 * introducida corrientemente por cualquier usuario registrado al publicar
	 * una correccion. */
	private boolean generatedByAdmin;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	
	/** Correccion que dio lugar a la Suggestion */
	@ManyToOne
	@JoinColumn(name="correction_id", referencedColumnName="id")
	private Correction correction;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	/** Constructor sin parametros de la clase */
	public Suggestion(){}
	
	/**
	 * Constructor que recibe las entidades asociadas a esta
	 * @param correction objeto Correction asociado a la Suggestion
	 */
	public Suggestion(Correction correction){
		this.correction = correction;
	}
	
	/**
	 * Constructor que inicializa todos los atributos
	 * @param wrongText atributo {@link #wrongText}
	 * del objeto Suggestion generado
	 * @param correctText atributo {@link #correctText}
	 * del objeto Suggestion generado
	 * @param correction atributo {@link #correction}
	 * del objeto Suggestion generado
	 * @param language atributo {@link #language}
	 * del objeto Suggestion generado
	 * @param generatedByAdmin 
	 */
	public Suggestion(String wrongText, String correctText,
				Correction correction, String language,
				boolean generatedByAdmin){
		this.correction = correction;
		this.language = language;
		this.generatedByAdmin = generatedByAdmin;
		
		this.wrongText = wrongText;
		this.parsedWrongText =
				ParserNLP.getInstance().parseToPOSstring(wrongText, language);
		this.decimalWrongText = ParserNLP.getInstance().convertParsedToDecimal(
				parsedWrongText);
		
		this.correctText = correctText;
		this.parsedCorrectText =
				ParserNLP.getInstance().parseToPOSstring(correctText, language);
		this.decimalCorrectText = ParserNLP.getInstance().convertParsedToDecimal(
				parsedCorrectText);
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
	 *  * Suggestion <--> 1 Correction
	 */
	@XmlTransient
	public Correction getCorrection() {
		return correction;
	}
	void _setCorrection(Correction correction) {
		this.correction = correction;
	}
	public void setCorrectionThis(Correction correction) {
		this.correction = correction;
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'interfaces fluidas'
	no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	@XmlElement
	public String getCorrectText() {
		return correctText;
	}
	public void setCorrectText(String correctText) {
		this.correctText = correctText;
	}
	public Suggestion setCorrectTextThis(String correctText) {
		this.correctText = correctText;
		return this;
	}
	
	@XmlElement
	public String getWrongText() {
		return wrongText;
	}
	public void setWrongText(String wrongText) {
		this.wrongText = wrongText;
	}
	public Suggestion setWrongTextThis(String wrongText) {
		this.wrongText = wrongText;
		return this;
	}
	
	@XmlElement
	public String getParsedCorrectText() {
		return parsedCorrectText;
	}
	public void setParsedCorrectText(String parsedCorrectText) {
		this.parsedCorrectText = parsedCorrectText;
	}
	public Suggestion setParsedCorrectTextThis(String parsedCorrectText) {
		this.parsedCorrectText = parsedCorrectText;
		return this;
	}
	
	@XmlElement
	public String getParsedWrongText() {
		return parsedWrongText;
	}
	public void setParsedWrongText(String parsedWrongText) {
		this.parsedWrongText = parsedWrongText;
	}
	public Suggestion setParsedWrongTextThis(String parsedWrongText) {
		this.parsedWrongText = parsedWrongText;
		return this;
	}
	
	@XmlElement
	public BigInteger getDecimalCorrectText() {
		return decimalCorrectText;
	}
	public void setDecimalCorrectText(BigInteger decimalCorrectText) {
		this.decimalCorrectText = decimalCorrectText;
	}
	public Suggestion setDecimalCorrectTextThis(BigInteger decimalCorrectText) {
		this.decimalCorrectText = decimalCorrectText;
		return this;
	}
	
	@XmlElement
	public BigInteger getDecimalWrongText() {
		return decimalWrongText;
	}
	public void setDecimalWrongText(BigInteger decimalWrongText) {
		this.decimalWrongText = decimalWrongText;
	}
	public Suggestion setDecimalWrongTextThis(BigInteger decimalWrongText) {
		this.decimalWrongText = decimalWrongText;
		return this;
	}
	
	@XmlElement
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String lang) {
		this.language = lang;
	}
	public Suggestion setLanguageThis(String lang) {
		this.language = lang;
		return this;
	}
	
	@XmlElement
	public boolean getGeneratedByAdmin() {
		return generatedByAdmin;
	}
	public void setGeneratedByAdmin(boolean generatedByAdmin) {
		this.generatedByAdmin = generatedByAdmin;
	}
	public Suggestion setGeneratedByAdminThis(boolean generatedByAdmin) {
		this.generatedByAdmin = generatedByAdmin;
		return this;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Suggestion setIdThis(Long id) {
		this.id = id;
		return this;
	}
	
	// // // //
	// METODOS
	// // // //
	
	/**
	 * Comprueba si las propiedades {@link #decimalWrongText} y
	 * {@link #decimalCorrectText} han sido correctamente inicializadas.
	 * @return
	 * 'true' si ambos atributos {@link #decimalWrongText} y
	 * {@link #decimalCorrectText} son distintos de -1. <br>
	 * 'false' si alguno de los atributos {@link #decimalWrongText} y
	 * {@link #decimalCorrectText} tiene valor -1.
	 */
	public boolean verifyDecimalCodes() {
		BigInteger defaultVal = BigInteger.valueOf(0);
		return (decimalWrongText.equals(defaultVal) 
				|| decimalCorrectText.equals(defaultVal));
	}
	
	// // // // // // // //
	// HASH CODE & EQUALS
	// // // // // // // //
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Suggestion other = (Suggestion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// // // // // // //
	// TO STRING
	// // // // // // //
	@Override
	public String toString() {
		return "Suggestion ["
				+ "decimalCorrectText=" + decimalCorrectText
				+ ", decimalWrongText=" + decimalWrongText
				+ ", language=" + language + "]";
	}
}

package com.loqua.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "credentials")
@Entity
@Table(name="Credentials")
public class Credentials implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String credentialType;
	private String credentialKey1;
	private String credentialKey2;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public Credentials(){}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCredentialType() {
		return credentialType;
	}
	public void setCredentialType(String credentialType) {
		this.credentialType = credentialType;
	}

	public String getCredentialKey1() {
		return credentialKey1;
	}
	public void setCredentialKey1(String credentialKey1) {
		this.credentialKey1 = credentialKey1;
	}

	public String getCredentialKey2() {
		return credentialKey2;
	}
	public void setCredentialKey2(String credentialKey2) {
		this.credentialKey2 = credentialKey2;
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
		Credentials other = (Credentials) obj;
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
		return "Credential [credentialType=" + credentialType
				+ ", credentialKey1=" + credentialKey1 + ", credentialKey2="
				+ credentialKey2 + "]";
	}
}

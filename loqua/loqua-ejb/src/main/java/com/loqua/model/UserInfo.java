package com.loqua.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userInfo")
@Entity
@Table(name="UserInfo")
public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private int countComments;
	private int countCorrections;
	private int countVotesComments;
	private int points;
	private Date dateLastModificationPoints;
	private int countCommentsMonth;
	private int countCorrectionsMonth;
	private int countVotesCommentsMonth;
	private int pointsMonth;
	private Date dateLastModificationPointsMonth;
	private int countCommentsYear;
	private int countCorrectionsYear;
	private int countVotesCommentsYear;
	private int pointsYear;
	private Date dateLastModificationPointsYear;

	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public UserInfo(){
		initData();
	}

	public UserInfo(User user){
		this.user = user;
		user.setUserInfo(this);
		
		initData();
	}
	
	public void unlink(){
		user.setUserInfo( null );
		user = null;
		
		initData();
	}
	
	private void initData() {
		this.dateLastModificationPoints = new Date();
		this.dateLastModificationPointsMonth = new Date();
		this.dateLastModificationPointsYear = new Date();
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
	
	/**
	 * Relacion entre entidades:<br>
	 *  1 UserInfoPrivacity <--> 1 User
	 */
	/*@XmlTransient*/@XmlElement
	public User getUser() {
		return user;
	}
	User _getUser() {
		return user;
	}
	public void setUser(User u) {
		user = u;
	}
	
	// // // //
	// METODOS
	// // // //
	
	public void incrementPointsBySentComment() {
		countComments += 1;
		countCommentsMonth += 1;
		countCommentsYear += 1;
		incrementPoints(1);
	}
	
	public void incrementVotesComments() {
		countVotesComments += 1;
		countVotesCommentsMonth += 1;
		countVotesCommentsYear += 1;
		incrementPoints(1);
	}
	
	public void incrementPointsByAcceptedCorrection() {
		countCorrections += 1;
		countCorrectionsMonth += 1;
		countCorrectionsYear += 1;
		incrementPoints(25);
	}
	
	public void incrementPoints(int increment){
		points += increment;
		dateLastModificationPoints = new Date();
		
		pointsMonth += increment;
		dateLastModificationPointsMonth = new Date();
		
		pointsYear += increment;
		dateLastModificationPointsYear = new Date();
	}
	
	public void decrementPointsByDeletedCorrection(Correction correction) {
		points -= 1;
		dateLastModificationPoints = new Date();
		if( correction.getDateApproved()==null ){
			// Nunca deberia alcanzar esta condicion: se llama a este metodo
			// cuando hay que sustituir una correccion que esta aprobada
			// y que, por tanto, tenia una 'dateApproved' distinta de null
			return;
		}
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(correction.getDateApproved());
		int correctionMonth = calendar.get(Calendar.MONTH);
		int correctionYear = calendar.get(Calendar.YEAR);
		
		calendar.setTime(new Date());
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentYear = calendar.get(Calendar.YEAR);
		
		if( correctionMonth == currentMonth ){
			pointsMonth -= 1;
			dateLastModificationPointsMonth = new Date();
		}
		if( correctionYear == currentYear ){
			pointsYear -= 1;
			dateLastModificationPointsYear = new Date();
		}
	}
	
	// // // // // // //
	// GETTERS & SETTERS
	// // // // // // //
	
	/* A la hora de acceder a una propiedad de una clase o de un bean,
	JSF requiere que exista un getter y un setter de dicha propiedad,
	y ademas los setter deben devolver obligatoriamente 'void'.
	Por tanto si se quiere crear setters que implementen 'method chainning'
	(que hagan 'return this') no deben modificarse los setter convencionales,
	sino agregar a la clase estos nuevos setter con un nombre distinto */
	
	@XmlElement
	public int getCountComments() {
		return countComments;
	}
	public void setCountComments(int countComments) {
		this.countComments = countComments;
	}

	@XmlElement
	public int getCountCorrections() {
		return countCorrections;
	}
	public void setCountCorrections(int countCorrections) {
		this.countCorrections = countCorrections;
	}

	@XmlElement
	public int getCountVotesComments() {
		return countVotesComments;
	}
	public void setCountVotesComments(int countVotesComments) {
		this.countVotesComments = countVotesComments;
	}

	@XmlElement
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	
	@XmlElement
	public Date getDateLastModificationPoints() {
		return dateLastModificationPoints;
	}
	public void setDateLastModificationPoints(Date date) {
		dateLastModificationPoints = date;
	}
	
	@XmlElement
	public int getCountCommentsMonth() {
		return countCommentsMonth;
	}
	public void setCountCommentsMonth(int countComments) {
		this.countCommentsMonth = countComments;
	}

	@XmlElement
	public int getCountCorrectionsMonth() {
		return countCorrectionsMonth;
	}
	public void setCountCorrectionsMonth(int countCorrections) {
		this.countCorrectionsMonth = countCorrections;
	}

	@XmlElement
	public int getCountVotesCommentsMonth() {
		return countVotesCommentsMonth;
	}
	public void setCountVotesCommentsMonth(int countVotesComments) {
		this.countVotesCommentsMonth = countVotesComments;
	}

	@XmlElement
	public int getPointsMonth() {
		return pointsMonth;
	}
	public void setPointsMonth(int points) {
		this.pointsMonth = points;
	}
	
	@XmlElement
	public Date getDateLastModificationPointsMonth() {
		return dateLastModificationPointsMonth;
	}
	public void setDateLastModificationPointsMonth(Date date) {
		dateLastModificationPointsMonth = date;
	}
	
	@XmlElement
	public int getCountCommentsYear() {
		return countCommentsYear;
	}
	public void setCountCommentsYear(int countComments) {
		this.countCommentsYear = countComments;
	}

	@XmlElement
	public int getCountCorrectionsYear() {
		return countCorrectionsYear;
	}
	public void setCountCorrectionsYear(int countCorrections) {
		this.countCorrectionsYear = countCorrections;
	}

	@XmlElement
	public int getCountVotesCommentsYear() {
		return countVotesCommentsYear;
	}
	public void setCountVotesCommentsYear(int countVotesComments) {
		this.countVotesCommentsYear = countVotesComments;
	}

	@XmlElement
	public int getPointsYear() {
		return pointsYear;
	}
	public void setPointsYear(int points) {
		this.pointsYear = points;
	}
	
	@XmlElement
	public Date getDateLastModificationPointsYear() {
		return dateLastModificationPointsYear;
	}
	public void setDateLastModificationPointsYear(Date date) {
		dateLastModificationPointsYear = date;
	}
	
	@XmlElement
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
		UserInfo other = (UserInfo) obj;
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
		return "UserInfo [points=" + points
				+ ", countComments=" + countComments
				+ ", countCorrections=" + countCorrections
				+ ", countVotesComments=" + countVotesComments
				+ ", points=" + points
				+ ", countCommentsMonth=" + countCommentsMonth
				+ ", countCorrectionsMonth=" + countCorrectionsMonth
				+ ", countVotesCommentsMonth=" + countVotesCommentsMonth
				+ ", pointsMonth=" + pointsMonth
				+ ", countCommentsYear=" + countCommentsYear
				+ ", countCorrectionsYear=" + countCorrectionsYear
				+ ", countVotesCommentsYear=" + countVotesCommentsYear
				+ ", pointsYear=" + pointsYear + "]";
	}
}

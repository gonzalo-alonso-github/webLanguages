package com.loqua.remote.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "forumThread")
@Entity
@Table(name="ForumThread")
public class ForumThread implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// // // // // // //
	// ATRIBUTOS
	// // // // // // //
	@Id @GeneratedValue( strategy = GenerationType.IDENTITY ) private Long id;
	private String guid;
	private String url;
	private String title;
	private String content;
	private Date date;
	private Date dateLastComment;
	private Date dateAggregated;
	private String feedName;
	
	// // // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (ATRIBUTOS)
	// // // // // // // // // // // // // //
	@OneToOne(mappedBy = "forumThread")
	private ForumThreadInfo forumThreadInfo;
	
	@ManyToOne @JoinColumn(name="feed_id")
	private Feed feed;
	
	@OneToMany(mappedBy="forumThread")
	private Set<ForumPost> forumPosts = new HashSet<ForumPost>();
	
	@OneToMany(mappedBy="forumThread")
	private Set<ForumThreadVoter> forumThreadVoters = 
		new HashSet<ForumThreadVoter>();
	
	// // // // // // //
	// CONSTRUCTORES
	// // // // // // //
	
	public ForumThread(){
		forumThreadInfo = new ForumThreadInfo();
		forumThreadInfo.setForumThread(this);
	}
	
	public ForumThread( ForumThreadInfo threadInfo, Feed feed ){
		this.forumThreadInfo = threadInfo;
		threadInfo.setForumThread(this);
		
		this.feed = feed;
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
	 *  1 Thread <--> 1 ThreadInfo
	 */
	@XmlTransient
	public ForumThreadInfo getForumThreadInfo() {
		return forumThreadInfo;
	}
	ForumThreadInfo _setForumThreadInfo() {
		return forumThreadInfo;
	}
	public void setForumThreadInfo(ForumThreadInfo info) {
		forumThreadInfo = info;
	}
	public ForumThread setForumThreadInfoThis(ForumThreadInfo info) {
		forumThreadInfo = info;
		return this;
	}
	
	/** Relacion entre entidades:<br>
	 *  * Thread <--> 1 Feed
	 */
	public Feed getFeed() {
		return feed;
	}
	void _setFeed(Feed f) {
		feed = f;
	}
	public void setFeed(Feed f) {
		feed = f;
	}
	public ForumThread setFeedThis(Feed f) {
		feed = f;
		return this;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 Thread <--> * ForumPosts
	 */
	@XmlTransient
	public Set<ForumPost> getForumPosts() {
		return Collections.unmodifiableSet(forumPosts);
	}
	Set<ForumPost> _getForumPosts() {
		return forumPosts;
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ThreadVoter <--> 1 Thread
	 */
	@XmlTransient
	public Set<ForumThreadVoter> getForumThreadVoters() {
		return Collections.unmodifiableSet(forumThreadVoters);
	}
	Set<ForumThreadVoter> _getForumThreadVoters() {
		return forumThreadVoters;
	}
	
	// // // // // // // // // // // // //
	// RELACION ENTRE ENTIDADES (METODOS)
	// // // // // // // // // // // // //
	/** Relacion entre entidades:<br>
	 *  1 ThreadInfo <--> * ForumPosts
	 */
	public void addForumPost(ForumPost p){
		forumPosts.add(p);
		p._setForumThread(this);
	}
	public void removeForumPost(ForumPost p){
		forumPosts.remove(p);
		p._setForumThread(null);
	}
	
	/** Relacion entre entidades:<br>
	 *  1 User <--> * ThreadVoters <--> 1 Thread
	 */
	public void addForumThreadVoter(ForumThreadVoter tv){
		forumThreadVoters.add(tv);
		tv._setForumThread(this);
	}
	public void removeForumThreadVoter(ForumThreadVoter tv){
		forumThreadVoters.remove(tv);
		tv._setForumThread(null);
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
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public ForumThread setGuidThis(String guid) {
		this.guid = guid;
		return this;
	}
	
	@XmlElement
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ForumThread setUrlThis(String url) {
		this.url = url;
		return this;
	}
	
	@XmlElement
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ForumThread setTitleThis(String title) {
		this.title = title;
		return this;
	}
	
	@XmlElement
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ForumThread setContentThis(String content) {
		this.content = content;
		return this;
	}
	
	@XmlElement
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ForumThread setDateThis(Date date) {
		this.date = date;
		return this;
	}
	
	@XmlElement
	public Date getDateLastComment() {
		return dateLastComment;
	}
	public void setDateLastComment(Date dateLastComment) {
		this.dateLastComment = dateLastComment;
	}
	public ForumThread setDateLastCommentThis(Date dateLastComment) {
		this.dateLastComment = dateLastComment;
		return this;
	}
	
	@XmlElement
	public Date getDateAggregated() {
		return dateAggregated;
	}
	public void setDateAggregated(Date dateAggregated) {
		this.dateAggregated = dateAggregated;
	}
	public ForumThread setDateAggregatedThis(Date dateAggregated) {
		this.dateAggregated = dateAggregated;
		return this;
	}
	
	@XmlElement
	public String getFeedName() {
		return feedName;
	}
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}
	public ForumThread setFeedNameThis(String feedName) {
		this.feedName = feedName;
		return this;
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
		ForumThread other = (ForumThread) obj;
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
		return "ForumThread [guid=" + guid + ", title=" + title
				+ ", url=" + url + ", date=" + date.toString()
				+ ", dateLastComment=" + dateLastComment.toString()
				+ ", dateAggregated=" + dateAggregated.toString() + "]";
	}
}

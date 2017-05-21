/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author sdai
 */
@Entity
@Table
@Indexed
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1512813050243600546L;
	private String username;
	private byte[] credential;
	private Long id;
	private Set<String> associates;
	private List<LSPayload> unsentMessages;

	public User() {
		username = new String();
		credential = null;
		id = null;
		associates = new HashSet<>();
		unsentMessages = new LinkedList<>();
	}

	public User(String username, byte[] credential) {
		this();
		this.username = username;
		this.credential = credential.clone();
		associates = new HashSet<>();
	}

	@Basic
	@Field
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Basic
	public byte[] getCredential() {
		return credential.clone();
	}

	public void setCredential(byte[] credential) {
		this.credential = credential;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "User_Associate", joinColumns = {
			@JoinColumn(referencedColumnName = "Id", name = "UserId") })
	@Column(name = "name")
	public Set<String> getAssociates() {
		return associates;
	}

	public void setAssociates(Set<String> associates) {
		this.associates = associates;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "UserId", nullable = false)
	public List<LSPayload> getUnsentMessages() {
		return unsentMessages;
	}

	public void setUnsentMessages(List<LSPayload> unsentMessages) {
		this.unsentMessages = unsentMessages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associates == null) ? 0 : associates.hashCode());
		result = prime * result + Arrays.hashCode(credential);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((unsentMessages == null) ? 0 : unsentMessages.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (associates == null) {
			if (other.associates != null)
				return false;
		} else if (!associates.equals(other.associates))
			return false;
		if (!Arrays.equals(credential, other.credential))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (unsentMessages == null) {
			if (other.unsentMessages != null)
				return false;
		} else if (!unsentMessages.equals(other.unsentMessages))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}

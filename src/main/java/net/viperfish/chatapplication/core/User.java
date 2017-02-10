/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

    public User() {
        username = new String();
        credential = null;
        id = null;
        associates = new HashSet<>();
    }

    public User(String username, byte[] credential) {
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
        @JoinColumn(referencedColumnName = "Id", name = "UserId")})
    @Column(name = "name")
    public Set<String> getAssociates() {
        return associates;
    }

    public void setAssociates(Set<String> associates) {
        this.associates = associates;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.username);
        hash = 73 * hash + Arrays.hashCode(this.credential);
        hash = 73 * hash + Objects.hashCode(this.id);
        hash = 73 * hash + Objects.hashCode(this.associates);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Arrays.equals(this.credential, other.credential)) {
            return false;
        }
        if (!Objects.equals(this.associates, other.associates)) {
            return false;
        }
        return true;
    }

   

}

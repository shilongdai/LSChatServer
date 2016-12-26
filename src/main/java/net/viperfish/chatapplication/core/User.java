/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

/**
 *
 * @author sdai
 */
@Entity
@Table
public class User implements Serializable {

    private String username;
    private byte[] credential;
    private Long id;
    private List<String> associates;

    public User() {
        username = new String();
        credential = null;
        id = null;
        associates = new LinkedList<>();
    }

    public User(String username, byte[] credential) {
        this.username = username;
        this.credential = credential.clone();
        associates = new LinkedList<>();
    }

    @Basic
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
    public List<String> getAssociates() {
        return associates;
    }

    public void setAssociates(List<String> associates) {
        this.associates = associates;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.username);
        hash = 17 * hash + Arrays.hashCode(this.credential);
        hash = 17 * hash + Objects.hashCode(this.id);
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}

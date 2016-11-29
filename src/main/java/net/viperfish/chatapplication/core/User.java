/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author sdai
 */
@Entity
@Table
public class User implements Serializable{

    private String username;
    private byte[] credential;
    private Long id;

    public User() {
        username = new String();
        credential = null;
        id = null;
    }

    public User(String username, byte[] credential) {
        this.username = username;
        this.credential = credential.clone();
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

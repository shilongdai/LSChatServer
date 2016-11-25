/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sdai
**/
public class DefaultLSSession implements LSSession {

    private String user;
    private Map<String, Object> attrs;
    private long creationTime;
    private boolean isNew;

    public DefaultLSSession(String user) {
        this.user = user;
        attrs = new HashMap<>();
        creationTime = new Date().getTime();
        isNew = true;
    }
    
    @Override
    public <T> T getAttribute(String name, Class<? extends T> type) {
        if(attrs.containsKey(name)) {
            Object result = attrs.get(name);
            if(type.isInstance(result)) {
                return type.cast(result);
            }   
        }
        return null;
    }

    @Override
    public Set<String> getAttributeNames() {
        Set<String> result = new HashSet<>();
        attrs.entrySet().stream().forEach((i) -> {
            result.add(i.getKey());
        });
        return result;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean isNew() {
        if(isNew) {
            isNew = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeAttribute(String name) {
        attrs.remove(name);
    }

    @Override
    public boolean containsAttribute(String name) {
        return attrs.containsKey(name);
    }

    @Override
    public void setAttribute(String name, Object attr) {
        this.attrs.put(name, attr);
    }
    
    
    
}

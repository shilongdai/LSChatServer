/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Set;

/**
 *
 * @author sdai
 */
public interface LSSession {
        
    public <T> T getAttribute(String name, Class<? extends T> type);
    public Set<String> getAttributeNames();
    public String getUser();
    public long getCreationTime();
    public boolean isNew();
    public void removeAttribute(String name);
    public boolean containsAttribute(String name);
    public void setAttribute(String name, Object attr);
    public void invalidate();
}

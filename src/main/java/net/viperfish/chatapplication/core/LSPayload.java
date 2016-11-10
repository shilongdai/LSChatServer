/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

/**
 *
 * @author sdai
 */
public interface LSPayload {
    
    public static final int LS_STATUS=1;
    public static final int LS_MESSAGE=2;
    
    public void setType(int type);
    public void setSource(String source);
    public void setTarget(String target);
    public void setAttribute(String attrName, String attr);
    public void setData(String data);
}

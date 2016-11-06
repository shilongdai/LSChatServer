/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;


/**
 *
 * @author sdai
 */
public interface LSStatus extends Serializable {
    public static final int SUCCESS = 200;
    public static final int LOGIN_FAIL = 201;
    public static final int NO_HANDLER=202;
    public static final int USER_OFFLINE = 203;
    
    public void setStatus(int status);
    
    public void setStatus(int status, String reason);
    
    public int getStatus();
    
    public String getReason();
    
    
}

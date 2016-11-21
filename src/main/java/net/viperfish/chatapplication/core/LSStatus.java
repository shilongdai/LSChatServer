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
public class LSStatus implements Serializable {

    public static final int SUCCESS = 200;
    public static final int LOGIN_FAIL = 201;
    public static final int NO_HANDLER=202;
    public static final int USER_OFFLINE = 203;
    public static final int INTERNAL_ERROR = 204;
    
    private int status;
    
    private String reason;

    public LSStatus(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    
    public LSStatus() {
        status = 200;
        this.reason = "Success";
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sdai
 */
public class LSResponse implements Serializable {

    public static final int SUCCESS = 200;
    public static final int LOGIN_FAIL = 201;
    public static final int NO_HANDLER=202;
    public static final int USER_OFFLINE = 203;
    public static final int INTERNAL_ERROR = 204;
    public static final int CHALLENGE = 205;
    public static final int AUTHENTICATE_FAIL = 206;
    public static final int USER_NOT_FOUND = 207;
    public static final int INVALID_REQUEST = 208;
    
    private int status;
    
    private String reason;
    
    private String data;
    
    private Map<String, String> attributes;

    public LSResponse(int status, String reason, String data) {
        this.status = status;
        this.reason = reason;
        this.data = data;
        attributes = new HashMap<>();
    }
    
    
    public LSResponse() {
        status = 200;
        this.reason = "";
        data = "";
        attributes = new HashMap<>();
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    

    
    
}

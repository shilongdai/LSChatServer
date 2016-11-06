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
public class DefaultLSStatus implements LSStatus {

    private int status;
    
    private String reason;

    public DefaultLSStatus(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    
    public DefaultLSStatus() {
        status = 200;
        this.reason = "Success";
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setStatus(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    
}

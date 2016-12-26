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
public class FilterException extends Exception {
    private LSResponse status;

    public FilterException(LSResponse status) {
        this.status = status;
    }

    public LSResponse getStatus() {
        return status;
    }
    
    
}

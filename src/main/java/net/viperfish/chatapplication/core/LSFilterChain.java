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
public interface LSFilterChain {
    public void doFilter(LSRequest req, LSPayload resp);
    
}

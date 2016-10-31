/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.HashMap;
import java.util.Map;
import org.glassfish.grizzly.websockets.WebSocket;

/**
 *
 * @author sdai
 */
public class UserRegister implements UserSocketMapper, UserSocketRegister {

    private Map<String, WebSocket> mapping;

    public UserRegister() {
        mapping = new HashMap<>();
    }
    
    
    
    @Override
    public WebSocket getSocket(String username) {
        return mapping.get(username);
    }

    @Override
    public void register(String username, WebSocket socket) {
        mapping.put(username, socket);
    }
    
}

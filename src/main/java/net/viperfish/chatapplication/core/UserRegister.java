/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.websockets.WebSocket;

/**
 *
 * @author sdai
 */
public class UserRegister {

    private Map<String, WebSocket> mapping;
    private Map<WebSocket, String> reverseMapping;
    private Logger logger;

    public UserRegister() {
        mapping = new HashMap<>();
        reverseMapping = new HashMap<>();
        logger = LogManager.getLogger();
    }
    
    
    
    public WebSocket getSocket(String username) {
        return mapping.get(username);
    }

    public void register(String username, WebSocket socket) {
        mapping.put(username, socket);
        reverseMapping.put(socket, username);
        logger.info("Registering Socket for:" + username);
    }
    
    
    public void unregister(String username) {
        mapping.remove(username);
        logger.info("Unregistering Socket for " + username);
    }
    
    public void unregister(WebSocket socket) {
        if(reverseMapping.containsKey(socket)) {
            logger.info("Unregistering Socket for " + reverseMapping.get(socket));
            mapping.remove(reverseMapping.get(socket));
            reverseMapping.remove(socket);
        }
    }
    
    public String getUsername(WebSocket socket) {
        return reverseMapping.get(socket);
    }
    
    public void clear() {
        mapping.clear();
        reverseMapping.clear();
    }
    
    public boolean isOnline(String user) {
        return mapping.containsKey(user);
    }
}

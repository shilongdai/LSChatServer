/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import org.glassfish.grizzly.websockets.WebSocket;

/**
 *
 * @author sdai
 */
public interface UserSocketMapper {
    public WebSocket getSocket(String username);
}

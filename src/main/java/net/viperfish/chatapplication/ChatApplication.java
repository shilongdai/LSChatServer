/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import net.viperfish.chatapplication.core.UserDatabase;
import org.glassfish.grizzly.websockets.WebSocketApplication;

/**
 *
 * @author sdai
 */
public class ChatApplication extends WebSocketApplication {

    private UserDatabase userDB;

    public void setUserDB(UserDatabase userDB) {
        this.userDB = userDB;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserSocketRegister;

/**
 *
 * @author sdai
 */
public final class LoginHandler implements RequestHandler {

    private UserDatabase userDB;
    private UserSocketRegister reg;

    public LoginHandler(UserDatabase userDB, UserSocketRegister reg) {
        this.userDB = userDB;
        this.reg = reg;
    }

    @Override
    public void init() {
    }

    @Override
    public void handleRequest(LSRequest req, LSResponse resp) {

    }

}

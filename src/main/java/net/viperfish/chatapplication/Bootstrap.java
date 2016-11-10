/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.io.IOException;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;

/**
 *
 * @author sdai
 */
public class Bootstrap {
    public static void main(String[] args) {
        HttpServer server = HttpServer.createSimpleServer("./", 8080);
        WebSocketAddOn addon = new WebSocketAddOn();
        server.getListeners().stream().forEach((listen) -> {
            listen.registerAddOn(addon);
        });
        ChatApplication application = new ChatApplication();
        application.setSocketMapper(new UserRegister());
        application.setUserDB(new RAMUserDatabase());
        
        WebSocketEngine.getEngine().register("", "/messenger", application);
        
        System.out.println("shut down server by pressing any button");
        try {
            System.in.read();
        } catch (IOException ex) {
            System.err.println("error on reading input, shutting down now");
        } finally {
            server.shutdown();
        }
        
    }
}

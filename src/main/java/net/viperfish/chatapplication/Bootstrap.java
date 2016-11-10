/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.util.Scanner;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
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
        UserDatabase userDB = new RAMUserDatabase();
        ChatApplication application = new ChatApplication();
        application.setSocketMapper(new UserRegister());
        application.setUserDB(userDB);

        WebSocketEngine.getEngine().register("", "/messenger", application);

        Scanner inputReader = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("command:");
                String command = inputReader.nextLine();
                if(command.equalsIgnoreCase("shutdown")) {
                    break;
                }
                String[] parts = command.split(" ");
                switch (parts[0]) {
                    case "addUser": {
                        String username = parts[1];
                        String password = parts[2];
                        User newUser = new User(username, password);
                        userDB.save(newUser);
                        break;
                    }
                    default: {
                        System.out.println("The command " + parts[0] + " is not supported");
                    }
                }
            }
        } finally {
            server.shutdown();
        }

    }
}

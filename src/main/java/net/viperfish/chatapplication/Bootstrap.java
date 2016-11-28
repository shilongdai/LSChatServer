/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import org.apache.logging.log4j.ThreadContext;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author sdai
 */
public class Bootstrap {

    public static void main(String[] args) {
        ThreadContext.put("id", UUID.randomUUID().toString());
        ThreadContext.put("username", "journalUser");
        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext(
                ApplicationRootContext.class);
        rootContext.start();
        rootContext.registerShutdownHook();
        HttpServer server = HttpServer.createSimpleServer("./", 8080);
        WebSocketAddOn addon = new WebSocketAddOn();
        server.getListeners().stream().forEach((listen) -> {
            listen.registerAddOn(addon);
        });
        
        ChatApplication application = rootContext.getBean(ChatApplication.class);
        WebSocketEngine.getEngine().register("", "/messenger", application);

        Scanner inputReader = new Scanner(System.in);
        try {
            server.start();
            while (true) {
                System.out.print("command:");
                String command = inputReader.nextLine();
                if (command.equalsIgnoreCase("shutdown")) {
                    break;
                }
                command = command.trim();
                if(command.length() == 0) {
                    continue;
                }
                String[] parts = command.split(" ");
                switch (parts[0]) {
                    case "addUser": {
                        String username = parts[1];
                        String password = parts[2];
                        User newUser = new User(username, password);
                        rootContext.getBean(UserDatabase.class).save(newUser);
                        System.out.println("User " + username + " added");
                        break;
                    }
                    default: {
                        System.out.println("The command " + parts[0] + " is not supported");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Bootstrap.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            server.shutdown();
        }

    }
}

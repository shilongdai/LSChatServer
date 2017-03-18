/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Scanner;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.glassfish.grizzly.http.server.HttpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import net.viperfish.chatapplication.core.KeyUtils;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;

/**
 *
 * @author sdai
 */
public class Bootstrap {

	public static void main(String[] args) {
		Logger logger = LogManager.getLogger();
		ThreadContext.put("id", UUID.randomUUID().toString());
		ThreadContext.put("username", "journalUser");
		AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext(
				ApplicationRootContext.class);
		rootContext.start();
		rootContext.registerShutdownHook();
		HttpServer server = rootContext.getBean(HttpServer.class);

		UserDatabase userDB = rootContext.getBean(UserDatabase.class);
		try (Scanner inputReader = new Scanner(System.in)) {
			server.start();
			while (true) {
				System.out.print("command:");
				String command = inputReader.nextLine();
				if (command.equalsIgnoreCase("shutdown")) {
					break;
				}
				command = command.trim();
				if (command.length() == 0) {
					continue;
				}
				String[] parts = command.split(" ");
				switch (parts[0]) {
				case "createUser": {
					if (parts.length != 3) {
						System.out.println("createUser <username> <public key url>");
						break;
					}
					try {
						URI u = new URI(parts[2]);
						Certificate cert = KeyUtils.INSTANCE.readCertificate(Paths.get(u));
						User user = new User();
						user.setUsername(parts[1]);
						user.setCredential(cert.getEncoded());
						userDB.save(user);
					} catch (URISyntaxException e) {
						System.out.println("Invalid Syntax");
						break;
					} catch (CertificateException ex) {
						System.out.println("Invalid Key");
						break;
					} catch (IOException e) {
						System.out.println("Unable to read file");
						break;
					}
					break;
				}
				case "clearUser": {
					userDB.deleteAll();
					break;
				}
				case "writePublicKey": {
					KeyUtils.INSTANCE.writePublicKey(Paths.get("server.pub"),
							rootContext.getBean(KeyPair.class).getPublic());
					break;
				}
				default: {
					System.out.println("The command " + parts[0] + " is not supported");
				}
				}
			}
		} catch (IOException ex) {
			logger.warn("error", ex);
		} finally {
			server.shutdown();
		}

	}
}

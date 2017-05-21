/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Collection;

/**
 *
 * @author sdai
 */
public interface RequestHandler {

	public void init();

	public LSResponse handleRequest(LSRequest req, Collection<LSPayload> resp);
}

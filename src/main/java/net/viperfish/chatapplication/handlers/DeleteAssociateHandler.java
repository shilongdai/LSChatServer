/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;

/**
 *
 * @author sdai
 */
public class DeleteAssociateHandler implements RequestHandler {

    private UserDatabase db;
    
    public DeleteAssociateHandler(UserDatabase db) {
        this.db = db;
    }

    @Override
    public void init() {
    }

    @Override
    public LSResponse handleRequest(LSRequest req, LSPayload resp) {
        String toDel = req.getData();
        String user = req.getSource();
        
        User u = db.findByUsername(user);
        u.getAssociates().remove(toDel);
        db.save(u);
        
        LSResponse response = new LSResponse();
        response.setStatus(LSResponse.SUCCESS);
        return response;
    }
    
}

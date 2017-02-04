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
public class AddAssociateHandler implements RequestHandler {

    private UserDatabase db;

    public AddAssociateHandler(UserDatabase db) {
        this.db = db;
    }
    
    @Override
    public void init() {
    }

    @Override
    public LSResponse handleRequest(LSRequest req, LSPayload resp) {
        User target = db.findByUsername(req.getData());
        LSResponse response = new LSResponse();
        if(target != null) {
            User src = db.findByUsername(req.getSource());
            src.getAssociates().add(target.getUsername());
            db.save(src);
            response.setStatus(LSResponse.SUCCESS);
        } else {
            response.setStatus(LSResponse.USER_NOT_FOUND);
        }
        return response;
    }
    
}

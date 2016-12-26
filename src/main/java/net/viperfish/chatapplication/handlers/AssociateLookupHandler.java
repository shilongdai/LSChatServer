/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.HashSet;
import java.util.Set;
import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;

/**
 *
 * @author sdai
 */
public class AssociateLookupHandler implements RequestHandler {

    private UserDatabase db;
    private UserRegister register;
    private JsonGenerator jGenerator;
    
    public AssociateLookupHandler(UserDatabase userDb, UserRegister userRegister) {
        this.db = userDb;
        this.register = userRegister;
        jGenerator = new JsonGenerator();
    }

    
    
    @Override
    public void init() {
    }

    @Override
    public LSResponse handleRequest(LSRequest req, LSPayload resp) {
        boolean onlineFilter = Boolean.getBoolean(req.getAttribute("checkOnline"));
        if(!onlineFilter) {
            try {
                Set<String> result = new HashSet<>(db.findByUsername(req.getSource()).getAssociates());
                LSResponse response = new LSResponse();
                response.setStatus(LSResponse.SUCCESS);
                response.setData(jGenerator.toJson(result));
                return response;
            } catch (JsonGenerationException | JsonMappingException ex) {
                LSResponse response = new LSResponse(LSResponse.INTERNAL_ERROR, ex.getMessage(), "");
                return response;
            }
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
}

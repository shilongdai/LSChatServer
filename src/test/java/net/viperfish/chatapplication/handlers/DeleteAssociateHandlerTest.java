/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class DeleteAssociateHandlerTest {
    private final UserDatabase db;
    private final DeleteAssociateHandler handler;

    public DeleteAssociateHandlerTest() {
        db = new RAMUserDatabase();
        
        User test = new User();
        test.setUsername("testUser");
        test.getAssociates().add("test1");
        test.getAssociates().add("test2");
        db.save(test);
        
        handler = new DeleteAssociateHandler(db);
    }
    
    @Test
    public void testDeleteAssociate() {
        LSRequest req = new LSRequest();
        req.setType(LSRequest.LS_DELETE_ASSOCIATE);
        req.setSource("testUser");
        req.setData("test1");
        LSResponse resp;
        
        LSPayload payload = new LSPayload();
        
        resp = handler.handleRequest(req, payload);
        
        Assert.assertEquals(LSResponse.SUCCESS, resp.getStatus());
        Assert.assertEquals(false, db.findByUsername("testUser").getAssociates().contains("test1"));
        Assert.assertEquals(true, db.findByUsername("testUser").getAssociates().contains("test2"));
        
    }
    
}

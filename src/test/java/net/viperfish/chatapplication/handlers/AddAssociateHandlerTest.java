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
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class AddAssociateHandlerTest {
    private static UserDatabase db;
    
    @BeforeClass
    public static void init() {
        db = new RAMUserDatabase();
        
        User testUser = new User();
        testUser.setUsername("testUser");
        db.save(testUser);
        
        User test1 = new User();
        test1.setUsername("test1");
        
        db.save(test1);
    }
    
    @Test
    public void testAddAssociate() {
        AddAssociateHandler handler = new AddAssociateHandler(db);
        
        LSRequest req = new LSRequest();
        req.setType(LSRequest.LS_ADD_ASSOCIATE);
        req.setSource("testUser");
        req.setData("test1");
        LSPayload resp = new LSPayload();
        LSResponse response = handler.handleRequest(req, resp);
        
        Assert.assertEquals(true, db.findByUsername("testUser").getAssociates().contains("test1"));
        Assert.assertEquals(LSResponse.SUCCESS, response.getStatus());
        
        req.setData("testNoexist");
        
        response = handler.handleRequest(req, resp);
        
        Assert.assertEquals(false, db.findByUsername("testUser").getAssociates().contains("testNoexist"));
        Assert.assertEquals(LSResponse.USER_NOT_FOUND, response.getStatus());
    }
    
}

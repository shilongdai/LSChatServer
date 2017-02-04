/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sdai
 */
public class SearchUserHandler implements RequestHandler {

    private final UserDatabase userDB;
    private final JsonGenerator jGenerator;
    private final Logger logger;

    public SearchUserHandler(UserDatabase userDB) {
        this.userDB = userDB;
        jGenerator = new JsonGenerator();
        logger = LogManager.getLogger();
    }
    
    @Override
    public void init() {
    }

    @Override
    public LSResponse handleRequest(LSRequest req, LSPayload resp) {
        String keyword = req.getAttribute("keyword");
        logger.info("Keyword:" + keyword);
        LSResponse response = new LSResponse();
        if(keyword == null || keyword.isEmpty()) {
            response.setStatus(LSResponse.INVALID_REQUEST, "Keyword cannot be empty");
            return response;
        }
        logger.info("Searching for:" + keyword);
        Set<String> result = new HashSet<>();
        for(User u :userDB.search(keyword)) {
            result.add(u.getUsername());
        }
        logger.info("Search Found:" + Arrays.toString(result.toArray()));
        try {
            String serialized = jGenerator.toJson(result);
            if(serialized.length() == 0) {
                response.setData("[ ]");
            } else {
                response.setData(serialized);
            }
            response.setStatus(LSResponse.SUCCESS);
            return response;
        } catch (JsonGenerationException | JsonMappingException ex) {
            response.setStatus(LSResponse.INTERNAL_ERROR);
            logger.warn("Serialization Error", ex);
            return response;
        }
        
    }
    
}

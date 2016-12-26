/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.util.LinkedList;
import java.util.List;
import net.viperfish.chatapplication.core.FilterException;
import net.viperfish.chatapplication.core.LSFilter;
import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sdai
 */
class DefaultFilterChain implements LSFilterChain {

    private RequestHandler endpoint;
    private final List<LSFilter> filters;
    private int current;
    private Logger logger;
    
    public DefaultFilterChain() {
        filters = new LinkedList<>();
        logger = LogManager.getLogger();
    }

    
    
    public RequestHandler getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(RequestHandler endpoint) {
        this.endpoint = endpoint;
    }
    
    public void addFilter(LSFilter filter) {
        filters.add(filter);
    }
    
    public LSResponse process(LSRequest req, LSPayload payload) {
        current = 0;
        try {
            return doFilter(req, payload);
        } catch (FilterException ex) {
            logger.info("exceptional filter status", ex);
            return ex.getStatus();
        }
    }
    
    
    @Override
    public LSResponse doFilter(LSRequest req, LSPayload resp) throws FilterException {
        if(current < filters.size()) {
            LSFilter filter = filters.get(current);
            current+=1;
            return filter.doFilter(req, resp, this);
        } else {
            return endpoint.handleRequest(req, resp);
        }
    }
    
}

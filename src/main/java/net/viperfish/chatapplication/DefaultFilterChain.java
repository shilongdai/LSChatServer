/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.util.LinkedList;
import java.util.List;
import net.viperfish.chatapplication.core.LSFilter;
import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.core.RequestHandler;

/**
 *
 * @author sdai
 */
class DefaultFilterChain implements LSFilterChain {

    private RequestHandler endpoint;
    private final List<LSFilter> filters;
    private int current;

    public DefaultFilterChain() {
        filters = new LinkedList<>();
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
    
    public LSStatus process(LSRequest req, LSPayload payload) {
        current = 0;
        doFilter(req, payload);
        return endpoint.handleRequest(req, payload);
    }
    
    
    @Override
    public void doFilter(LSRequest req, LSPayload resp) {
        if(current < filters.size()) {
            LSFilter filter = filters.get(current);
            current+=1;
            filter.doFilter(req, resp, this);
        }
    }
    
}

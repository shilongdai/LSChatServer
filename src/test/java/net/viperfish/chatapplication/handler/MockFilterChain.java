/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handler;

import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;

/**
 *
 * @author sdai
 */
public class MockFilterChain implements LSFilterChain {

    @Override
    public LSStatus doFilter(LSRequest req, LSPayload resp) {
        resp.setType(LSPayload.LS_MESSAGE);
        resp.setSource(req.getSource());
        resp.setTarget(req.getAttribute("target"));
        resp.setData(req.getData());
        return new LSStatus(LSStatus.SUCCESS, "", "");
    }
    
}

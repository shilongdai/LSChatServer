/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.glassfish.grizzly.websockets.WebSocket;

/**
 *
 * @author sdai
 */
public interface LSRequest {

    public String getSource();

    public String getAttribute(String key);

    public Date getTimeStamp();

    public String getData();

    public InputStream getInputStream();

    public Long getType();

    public WebSocket getSocket();

    public Long getContentLenth();

    public Set<String> getAttributeNames();

    public Map<String, String> getAttributes();

}

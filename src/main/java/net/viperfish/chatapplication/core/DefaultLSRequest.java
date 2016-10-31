/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.glassfish.grizzly.websockets.WebSocket;

/**
 *
 * @author sdai
 */
public class DefaultLSRequest implements LSRequest {

    private String from;
    private String to;
    private Map<String, String> attributes;
    private Date timeStamp;
    private Long type;
    private String data;
    private WebSocket socket;

    public DefaultLSRequest(String from, String to, Map<String, String> attributes, Date timeStamp, Long type, String data, WebSocket sock) {
        this.from = from;
        this.to = to;
        this.attributes = attributes;
        this.timeStamp = timeStamp;
        this.type = type;
        this.data = data;
        this.socket = sock;
    }

    public DefaultLSRequest() {
        from = "anonymous";
        to = "server";
        attributes = new HashMap<>();
        timeStamp = new Date();
        type = 0L;
        data = "";
        socket = null;
    }

    public void setSource(String from) {
        this.from = from;
    }

    public String getTarget() {
        return to;
    }

    public void setTarget(String to) {
        this.to = to;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    @Override
    public String getSource() {
        return this.from;
    }

    @Override
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Long getType() {
        return type;
    }

    @Override
    public Long getContentLenth() {
        return (long) data.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public Set<String> getAttributeNames() {
        Set<String> result = new HashSet<>();
        attributes.entrySet().stream().forEach((e) -> {
            result.add(e.getKey());
        });
        return result;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.from);
        hash = 17 * hash + Objects.hashCode(this.to);
        hash = 17 * hash + Objects.hashCode(this.attributes);
        hash = 17 * hash + Objects.hashCode(this.timeStamp);
        hash = 17 * hash + Objects.hashCode(this.type);
        hash = 17 * hash + Objects.hashCode(this.data);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultLSRequest other = (DefaultLSRequest) obj;
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        if (!Objects.equals(this.attributes, other.attributes)) {
            return false;
        }
        if (!Objects.equals(this.timeStamp, other.timeStamp)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public WebSocket getSocket() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

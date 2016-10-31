/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sdai
 */
public class DefaultLSResponse implements LSResponse {

    private int status;
    
    private String additional;
    
    private ByteArrayOutputStream buffer;
    
    private Map<String, String> attributes;

    public ByteArrayOutputStream getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteArrayOutputStream buffer) {
        this.buffer = buffer;
    }

    public int getStatus() {
        return status;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    
    
    public DefaultLSResponse() {
        status = 1;
        buffer = new ByteArrayOutputStream();
        this.additional = "Success";
        this.attributes = new HashMap<>();
    }
    
    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setStatus(int status, String reason) {
        this.status = status;
        this.additional = reason;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.buffer;
    }

    @Override
    public Writer getWriter() {
        return new OutputStreamWriter(buffer, StandardCharsets.UTF_8);
    }

    @Override
    public void addAttribute(String key, String attr) {
        this.attributes.put(key, attr);
    }

    @Override
    public void reset() {
        this.status = 200;
        this.attributes.clear();
        this.buffer = new ByteArrayOutputStream();
        this.additional = "Success";
    }

    
}

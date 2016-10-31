/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.OutputStream;
import java.io.Writer;

/**
 *
 * @author sdai
 */
public interface LSResponse {
    
    public void setStatus(int status);
    
    public void setStatus(int status, String reason);
    
    public OutputStream getOutputStream();
    
    public Writer getWriter();
    
    public void addAttribute(String key, String attr);
    
    public void reset();
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketListener;

/**
 *
 * @author sdai
 */
public class ChatWebSocket implements WebSocket {

    private WebSocket delegateTarget;
    private String user;
    
    public ChatWebSocket(WebSocket delegateTarget, String user) {
        this.delegateTarget = delegateTarget;
        this.user = user;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    @Override
    public GrizzlyFuture<DataFrame> send(String data) {
        return delegateTarget.send(data);
    }

    @Override
    public GrizzlyFuture<DataFrame> send(byte[] data) {
        return delegateTarget.send(data);
    }

    @Override
    public void broadcast(Iterable<? extends WebSocket> arg0, String arg1) {
        delegateTarget.broadcast(arg0, arg1);
    }

    @Override
    public void broadcast(Iterable<? extends WebSocket> arg0, byte[] arg1) {
        delegateTarget.broadcast(arg0, arg1);
    }

    @Override
    public void broadcastFragment(Iterable<? extends WebSocket> arg0, String arg1, boolean arg2) {
        delegateTarget.broadcastFragment(arg0, arg1, arg2);
    }

    @Override
    public void broadcastFragment(Iterable<? extends WebSocket> arg0, byte[] arg1, boolean arg2) {
        delegateTarget.broadcastFragment(arg0, arg1, arg2);
    }

    @Override
    public GrizzlyFuture<DataFrame> sendPing(byte[] data) {
        return delegateTarget.sendPing(data);
    }

    @Override
    public GrizzlyFuture<DataFrame> sendPong(byte[] data) {
        return delegateTarget.sendPong(data);
    }

    @Override
    public GrizzlyFuture<DataFrame> stream(boolean arg0, String arg1) {
        return delegateTarget.stream(arg0, arg1);
    }

    @Override
    public GrizzlyFuture<DataFrame> stream(boolean arg0, byte[] arg1, int arg2, int arg3) {
        return delegateTarget.stream(arg0, arg1, arg2, arg3);
    }

    @Override
    public void close() {
        delegateTarget.close();
    }

    @Override
    public void close(int code) {
        delegateTarget.close(code);
    }

    @Override
    public void close(int arg0, String arg1) {
        delegateTarget.close(arg0, arg1);
    }

    @Override
    public boolean isConnected() {
        return delegateTarget.isConnected();
    }

    @Override
    public void onConnect() {
        delegateTarget.onConnect();
    }

    @Override
    public void onMessage(String text) {
        delegateTarget.onMessage(text);
    }

    @Override
    public void onMessage(byte[] data) {
        delegateTarget.onMessage(data);
    }

    @Override
    public void onFragment(boolean arg0, String arg1) {
        delegateTarget.onFragment(arg0, arg1);
    }

    @Override
    public void onFragment(boolean arg0, byte[] arg1) {
        delegateTarget.onFragment(arg0, arg1);
    }

    @Override
    public void onClose(DataFrame frame) {
        delegateTarget.onClose(frame);
    }

    @Override
    public void onPing(DataFrame frame) {
        delegateTarget.onPing(frame);
    }

    @Override
    public void onPong(DataFrame frame) {
        delegateTarget.onPong(frame);
    }

    @Override
    public boolean add(WebSocketListener listener) {
        return delegateTarget.add(listener);
    }

    @Override
    public boolean remove(WebSocketListener listener) {
        return delegateTarget.remove(listener);
    }

    
    
}

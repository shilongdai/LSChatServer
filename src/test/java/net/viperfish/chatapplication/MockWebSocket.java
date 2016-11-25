/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.impl.SafeFutureImpl;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketListener;

/**
 *
 * @author sdai
 */
public class MockWebSocket implements WebSocket {

    private List<String> sentData;
    private List<String> broadCastData;
    private List<String> receivedData;

    public MockWebSocket() {
        sentData = new LinkedList<>();
        broadCastData = new LinkedList<>();
        receivedData = new LinkedList<>();
    }
    
    public List<String> getSentData() {
        return sentData;
    }

    public List<String> getBroadCastData() {
        return broadCastData;
    }

    public List<String> getReceivedData() {
        return receivedData;
    }

    @Override
    public GrizzlyFuture<DataFrame> send(String data) {
        sentData.add(data);
        return new SafeFutureImpl<>();
    }

    @Override
    public GrizzlyFuture<DataFrame> send(byte[] data) {
        sentData.add(new String(data, StandardCharsets.UTF_8));
        return new SafeFutureImpl<>();
    }

    @Override
    public void broadcast(Iterable<? extends WebSocket> arg0, String arg1) {
        broadCastData.add(arg1);
    }

    @Override
    public void broadcast(Iterable<? extends WebSocket> arg0, byte[] arg1) {
        broadCastData.add(new String(arg1, StandardCharsets.UTF_8));
    }

    @Override
    public void broadcastFragment(Iterable<? extends WebSocket> arg0, String arg1, boolean arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void broadcastFragment(Iterable<? extends WebSocket> arg0, byte[] arg1, boolean arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GrizzlyFuture<DataFrame> sendPing(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GrizzlyFuture<DataFrame> sendPong(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GrizzlyFuture<DataFrame> stream(boolean arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GrizzlyFuture<DataFrame> stream(boolean arg0, byte[] arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {

    }

    @Override
    public void close(int code) {

    }

    @Override
    public void close(int arg0, String arg1) {

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onMessage(String text) {
        this.receivedData.add(text);
    }

    @Override
    public void onMessage(byte[] data) {
        receivedData.add(new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public void onFragment(boolean arg0, String arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFragment(boolean arg0, byte[] arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onClose(DataFrame frame) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPing(DataFrame frame) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPong(DataFrame frame) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(WebSocketListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(WebSocketListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

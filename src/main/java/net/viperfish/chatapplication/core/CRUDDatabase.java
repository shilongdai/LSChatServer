/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

/**
 *
 * @author sdai
 */
public interface CRUDDatabase<ID, T> {

    public T get(ID id);

    public T save(T data);

    public void remove(ID id);

    public Iterable<T> findAll();
}

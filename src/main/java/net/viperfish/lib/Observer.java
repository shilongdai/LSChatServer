/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.lib;

/**
 *
 * @author sdai
 */
public interface Observer<T> {
    public <T> void observe(T data);

}

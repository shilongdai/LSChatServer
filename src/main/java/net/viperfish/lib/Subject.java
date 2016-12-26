/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.lib;

import java.util.Collection;

/**
 *
 * @author sdai
 */
public abstract class Subject<T> {
    private Collection<Observer<? extends T>> observers;
    
    public void subscribe(Observer<? extends T> o) {
        observers.add(o);
    }
    
    public <S extends T> void notifyObserver(S data) {
        observers.stream().forEach((o) -> {
            o.observe(data);
        });
        
    }
}

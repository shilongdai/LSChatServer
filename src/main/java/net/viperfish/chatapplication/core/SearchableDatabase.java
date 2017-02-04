/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Collection;

public interface SearchableDatabase<T> {
        public Collection<T> search(String query);
}

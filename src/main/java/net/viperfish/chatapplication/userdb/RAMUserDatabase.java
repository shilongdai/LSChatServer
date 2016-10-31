/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.userdb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;

/**
 *
 * @author sdai
 */
public class RAMUserDatabase implements UserDatabase {

    private Map<Long, User> mapping;
    private long currentMax;

    public RAMUserDatabase() {
        mapping = new HashMap<>();
        currentMax = 0;
    }

    @Override
    public User get(Long id) {
        return mapping.get(id);
    }

    @Override
    public User save(User data) {
        data.setId(currentMax);
        return mapping.put(currentMax++, data);
    }

    @Override
    public void remove(Long id) {
        mapping.remove(id);
    }

    @Override
    public Iterable<User> findAll() {
        List<User> result = new LinkedList<>();
        for (Map.Entry<Long, User> i : mapping.entrySet()) {
            result.add(i.getValue());
        }
        return result;
    }

}

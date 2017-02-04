/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.userdb;

import java.util.Collection;
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

    private Map<String, User> mapping;
    private Map<Long, User> idMapping;
    private long currentMax;

    public RAMUserDatabase() {
        mapping = new HashMap<>();
        idMapping = new HashMap<>();
        currentMax = 0;
    }

    @Override
    public <S extends User> S save(S entity) {
        entity.setId(currentMax);
        mapping.put(entity.getUsername(), entity);
        idMapping.put(currentMax++, entity);
        return entity;
    }

    @Override
    public <S extends User> Iterable<S> save(Iterable<S> entities) {
        for (User i : entities) {
            this.save(i);
        }
        return entities;
    }

    @Override
    public User findOne(Long id) {
        return idMapping.get(id);
    }

    @Override
    public boolean exists(Long id) {
        return idMapping.containsKey(id);
    }

    @Override
    public Iterable<User> findAll() {
        List<User> result = new LinkedList<User>();
        for (Map.Entry<Long, User> i : idMapping.entrySet()) {
            result.add(i.getValue());
        }
        return result;
    }

    @Override
    public Iterable<User> findAll(Iterable<Long> ids) {
        List<User> result = new LinkedList<>();
        for (Long id : ids) {
            result.add(this.findOne(id));
        }
        return result;
    }

    @Override
    public long count() {
        return idMapping.size();
    }

    @Override
    public void delete(Long id) {
        User toDelete = idMapping.remove(id);
        mapping.remove(toDelete.getUsername());
    }

    @Override
    public void delete(User entity) {
        if (entity.getId() != null && idMapping.containsKey(entity.getId())) {
            mapping.remove(idMapping.remove(entity.getId()).getUsername());
        } else if (entity.getUsername() != null && mapping.containsKey(entity.getUsername())) {
            idMapping.remove(mapping.remove(entity.getUsername()).getId());
        }
        return;
    }

    @Override
    public void delete(Iterable<? extends User> entities) {
        for (User i : entities) {
            this.delete(i);
        }

    }

    @Override
    public void deleteAll() {
        idMapping.clear();
        mapping.clear();
    }

    @Override
    public User findByUsername(String username) {
        return mapping.get(username);
    }

    @Override
    public Collection<User> search(String query) {
        List<User> result = new LinkedList<>();
        for (Map.Entry<String, User> e : mapping.entrySet()) {
            if (e.getValue().getUsername().contains(query)) {
                result.add(e.getValue());
            }
        }
        return result;
    }

}

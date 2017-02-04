/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author sdai
 */
@Transactional
public interface UserDatabase extends CrudRepository<User, Long>, SearchableDatabase<User> {
    public User findByUsername(String username);
}

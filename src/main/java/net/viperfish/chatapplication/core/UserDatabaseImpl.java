/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.FatalBeanException;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author sdai
 */
public class UserDatabaseImpl implements SearchableDatabase<User> {

    @PersistenceContext
    private EntityManager entityManager;

    private EntityManagerProxy proxy;

    private FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(proxy.getTargetEntityManager());
    }

    @PostConstruct
    public void init() {
        if (!(this.entityManager instanceof EntityManagerProxy)) {
            throw new FatalBeanException("Entity Manager" + this.entityManager + " is not a proxy");
        }
        this.proxy = (EntityManagerProxy) this.entityManager;
    }

    @Transactional
    @Override
    public Collection<User> search(String query) {
        FullTextEntityManager manager = this.getFullTextEntityManager();

        QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(User.class).get();

        Query luceneQuery = builder.keyword().fuzzy()
                .onFields("username").matching(query)
                .createQuery();

        FullTextQuery q = manager.createFullTextQuery(luceneQuery, User.class);
        q.setProjection(FullTextQuery.THIS, FullTextQuery.SCORE);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.getResultList();
        List<User> fine = new LinkedList<>();
        result.stream().forEach((i) -> {
            fine.add((User) i[0]);
        });
        return fine;
    }

}

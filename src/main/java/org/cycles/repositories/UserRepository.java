package org.cycles.repositories;

import org.cycles.entites.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserRepository {
    @Inject
    EntityManager em;

    @Transactional
    public void createdUser(User p){
        em.persist(p);
    }

    @Transactional
    public void deleteUser(User p){
        em.remove(em.contains(p) ? p : em.merge(p));
    }

    @Transactional
    public List<User> listUser(){
        List<User> users = em.createQuery("select p from User p").getResultList();
        return users;
    }
    @Transactional
    public User findUser(Long Id){
        return em.find(User.class, Id);
    }
    @Transactional
    public void updateUser(User p){
        em.merge(p);
    }
}

package com.main.services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    void ajouter(T t) throws Exception;
    void modifier(T t) throws Exception;
    boolean supprimer(T t) throws Exception;
    List<T> rechercher() throws Exception;
/*
    public boolean Ajouter(T t) ;
    public boolean Modifier(T t);
    public boolean Supprimer(int Id);

    boolean Supprimer(String email);

    public List<T> Recuperer();
    /*
 */
}

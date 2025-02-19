package services;
import entities.User;

import java.util.List;

public interface IService<T> {
        public void Ajouter(T t) ;
        public void Modifier(T t);
        public boolean Supprimer(int Id);

        boolean Supprimer(String email);

        public List<T> Recuperer();



}

package services;

import java.util.List;

public interface IService<T> {
        public boolean Ajouter(T t) ;
        public boolean Modifier(T t);
        public boolean Supprimer(int Id);

        boolean Supprimer(String email);

        public List<T> Recuperer();



}

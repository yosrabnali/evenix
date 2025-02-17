package services;
import java.util.List;

public interface IService <T> {
        public void Ajouter(T t) ;
        public void Modifier(T t);
        public boolean Supprimer(int Id);
        public List<T> Recuperer();

}

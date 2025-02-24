package util;

import entities.User;

public class Session {
    private static User currentUser;

    // Récupérer l'utilisateur actuel
    public static User getUser() {
        return currentUser;
    }

    // Définir l'utilisateur actuel
    public static void setUser(User user) {
        currentUser = user;
    }

    // Vider la session
    public static void clear() {
        currentUser = null;
    }
}

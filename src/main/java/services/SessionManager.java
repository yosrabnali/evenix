package services;

import entities.User;

public class SessionManager {
    private static SessionManager instance;
    private User user;

    private SessionManager() { }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void clearSession() {
        user = null;
    }
}

package Entity.Users;

public class UserSingleton {
    // The unique instance of UserSingleton (singleton)
    private static UserSingleton instance;

    // The single instance of User managed by this class
    private User user;

    // Private constructor prevents instantiation from other classes
    private UserSingleton() {
        // Optionally initialize the user here or leave it null
    }

    // Thread-safe lazy initialization of the singleton instance
    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    // Initialize the User object with full details
    public void initializeUser(User user) {
        this.user = user;
    }

    // Retrieve the single User instance
    public User getUser() {
        return user;
    }

    // Replace the current User instance if needed
    public void setUser(User user) {
        this.user = user;
    }

    // Logout method to clear the user instance (set it to null)
    public void logout() {
        this.user = null;
    }
}

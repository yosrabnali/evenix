package entities;

public enum Role {
    ADMIN("Admin"),
    CLIENT("Client"),
    ORGANISATEUR("Organisateur"),
    PRESTATAIRE("Prestataire");

    private String roleName;

    // Constructeur pour initialiser le nom du rôle
    Role(String roleName) {
        this.roleName = roleName;
    }

    // Méthode pour obtenir le nom du rôle
    public String getRoleName() {
        return roleName;
    }

    // Méthode pour obtenir un rôle à partir du nom de rôle
    public static Role fromString(String roleName) {
        for (Role role : Role.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Rôle inconnu: " + roleName);
    }}

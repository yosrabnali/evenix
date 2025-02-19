package Main;

import entities.Role;
import entities.User;
import services.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        Scanner scanner = new Scanner(System.in);
        int choix;

        do {
            System.out.println("\n========= Gestion des Utilisateurs =========");
            System.out.println("1️⃣ Ajouter un utilisateur");
            System.out.println("2️⃣ Afficher tous les utilisateurs");
            System.out.println("3️⃣ Modifier un utilisateur");
            System.out.println("4️⃣ Supprimer un utilisateur");
            System.out.println("5️⃣ Quitter");
            System.out.print("👉 Choisissez une option : ");
            choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne

            switch (choix) {
                case 1:
                    System.out.println("\n🔹 Ajout d'un utilisateur 🔹");
                    System.out.print("Nom : ");
                    String nom = scanner.nextLine();
                    System.out.print("Prénom : ");
                    String prenom = scanner.nextLine();
                    System.out.print("Email : ");
                    String email = scanner.nextLine();
                    System.out.print("Mot de passe : ");
                    String motDePasse = scanner.nextLine();
                    System.out.print("Téléphone : ");
                    String telephone = scanner.nextLine();

                    System.out.print("Rôle (ADMIN, CLIENT, ORGANISATEUR, PRESTATAIRE) : ");
                    String roleStr = scanner.nextLine();
                    Role role = Role.fromString(roleStr);

                    User newUser = new User(nom, prenom, email, motDePasse, telephone, role);
                    userService.Ajouter(newUser);
                    break;

                case 2:
                    System.out.println("\n📋 Liste des utilisateurs :");
                    userService.Recuperer().forEach(System.out::println);
                    break;

                case 3:
                    System.out.println("\n✏️ Modification d'un utilisateur ✏️");
                    System.out.print("Email de l'utilisateur à modifier : ");
                    String emailModif = scanner.nextLine();

                    System.out.print("Nouveau nom : ");
                    String newNom = scanner.nextLine();
                    System.out.print("Nouveau prénom : ");
                    String newPrenom = scanner.nextLine();
                    System.out.print("Nouveau mot de passe : ");
                    String newMotDePasse = scanner.nextLine();
                    System.out.print("Nouveau téléphone : ");
                    String newTelephone = scanner.nextLine();

                    System.out.print("Nouveau rôle (ADMIN, CLIENT, ORGANISATEUR, PRESTATAIRE) : ");
                    String newRoleStr = scanner.nextLine();
                    Role newRole = Role.fromString(newRoleStr);

                    User updatedUser = new User(newNom, newPrenom, emailModif, newMotDePasse, newTelephone, newRole);
                    userService.Modifier(updatedUser);
                    break;

                case 4:
                    System.out.println("\n🗑 Suppression d'un utilisateur 🗑");
                    System.out.print("Email de l'utilisateur à supprimer : ");
                    String emailSuppr = scanner.nextLine();
                    userService.Supprimer(emailSuppr);
                    break;

                case 5:
                    System.out.println("👋 Programme terminé !");
                    break;

                default:
                    System.out.println("❌ Choix invalide, veuillez réessayer !");
            }

        } while (choix != 5);

        scanner.close();
    }
}

# evenix
🎯 Application Desktop – Gestion d'Événements (Evenix)

📌 Description
Evenix Desktop est une application riche en fonctionnalités, développée en JavaFX, offrant une interface utilisateur fluide et moderne pour la gestion complète des événements. Elle permet à l’administrateur comme aux utilisateurs de gérer :

👤 Utilisateurs : inscription, authentification, gestion des rôles et profils.

📅 Événements & Réservations : visualisation, participation, paiements, historique.

📝 Posts & Commentaires : publication, discussions, système de likes.

📣 Réclamations & Feedbacks : interface de dépôt de réclamations, suivi, réponses administratives.

🧾 Matériel & Catégories : ajout, gestion, QR codes, vocal.

📦 Locations & lignes de location : suivi, prédictions, dashboard interactif.

🧩 Table des Matières
Fonctionnalités

Architecture

Prérequis

Installation & Configuration

Usage

Tests

Contribution

Licence

Équipe

Mots-clés

🚀 Fonctionnalités
Gestion des utilisateurs : JavaFX Forms, JWT, Captcha, vérification 2FA (SMS ou mail).

Gestion des événements : intégration météo, paiement (Stripe API via backend), PDF ticket, Map intégrée.

Posts et commentaires : CRUD complet avec analyse des sentiments.

Réclamations : upload de fichiers, feedbacks administratifs, détection de tonalité émotionnelle.

Matériel :  QR code, organisation par catégorie.

Locations : gestion contractuelle , email intégré.

🏗️ Architecture
Frontend et Backend Desktop : JavaFX , FXML, SceneBuilder



Base de données : MySQL 




📦 Prérequis
JDK 17+

JavaFX SDK

SceneBuilder 

IDE : IntelliJ 

MySQL 

⚙️ Installation & Configuration
bash
Copier
Modifier
# Cloner le projet
git clone (https://github.com/yosrabnali/evenix.git)
cd EvenixDesktop

# Ouvrir dans IntelliJ 
# Configurer les chemins JavaFX 

# Lancer la classe principale
Main.java
Si vous utilisez un backend Spring Boot local :

bash
Copier
Modifier
cd backend/
mvn clean install

🖥️ Usage
Lancer l'application depuis votre IDE.

Connexion avec un utilisateur 

Naviguer via le tableau de bord : événements, utilisateurs, etc.

Utiliser les fonctionnalités avancées depuis l’interface.


📄 Licence
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus d'infos.

👨‍💻 Équipe
Lunatics Team – Conception & Développement

Merci à tous les testeurs

🔍 Mots-clés
evenix, javafx, gestion événements, desktop app, springboot, qr code, pdf, stripe, chatbot, reclamation, calendrier, login, reservation, map, météo, fxml, ai


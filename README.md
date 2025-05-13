🎯 Evenix Desktop – Application de Gestion d'Événements

📌 Description
Evenix Desktop est une application JavaFX moderne et complète dédiée à la gestion d'événements. Elle offre une interface fluide et intuitive, adaptée aux utilisateurs comme aux administrateurs, pour gérer tous les aspects d’un écosystème événementiel :

👤 Utilisateurs : inscription, authentification, gestion des rôles et profils.

📅 Événements & Réservations : visualisation, inscription, paiements Stripe, historique.

📝 Posts & Commentaires : publication, discussion, likes, analyse de sentiments.

📣 Réclamations & Feedbacks : gestion des plaintes avec pièces jointes, réponse, suivi.

🧾 Matériel & Catégories : ajout de matériel, gestion par catégorie, QR codes.

📦 Locations & Lignes de Location : contrats, suivi, prédictions, e-mails intégrés.

🧩 Table des Matières
🚀 Fonctionnalités

🏗️ Architecture

📦 Prérequis

⚙️ Installation & Configuration

🖥️ Usage

🧪 Tests

🤝 Contribution

📄 Licence

👨‍💻 Équipe

🔍 Mots-clés

🚀 Fonctionnalités
Utilisateurs : Formulaires JavaFX, JWT, Captcha, vérification 2FA par SMS ou e-mail.

Événements : CRUD, météo, cartes interactives, paiement en ligne (Stripe), PDF ticket.

Posts & Commentaires : CRUD complet, système de likes, analyse de sentiment intégrée.

Réclamations : dépôt de plainte avec fichiers, feedback, détecteur émotionnel.

Matériel : gestion des objets via vocal, QR code, tri par catégories.

Locations : contrats, prédictions, tableau de bord, envoi de mail automatisé.

🏗️ Architecture
Composant	Technologie
Frontend	JavaFX 20, FXML, SceneBuilder
Backend	(Optionnel) Spring Boot REST API
Base de données	MySQL (local ou distant)
Autres libs	JFoenix, Apache POI, ZXing, JasperReports

📦 Prérequis
☕ JDK 17+

💻 JavaFX SDK

🛠️ SceneBuilder (optionnel mais recommandé)

🧠 IntelliJ IDEA ou Eclipse

🗃️ MySQL Server (local ou distant)

⚙️ Installation & Configuration
bash
Copier
Modifier
# Cloner le projet
git clone https://github.com/yosrabnali/evenix.git
cd EvenixDesktop
🔧 Étapes
Ouvrir le projet dans IntelliJ IDEA

Configurer JavaFX (chemins SDK dans les paramètres du projet)

Lancer la classe principale : Main.java

🚀 Backend (optionnel – Spring Boot)
bash
Copier
Modifier
cd backend/
mvn clean install
mvn spring-boot:run
🖥️ Usage
Démarrer l’application depuis votre IDE.

Connexion avec un utilisateur (ex: admin / admin).

Naviguer dans le tableau de bord pour :

Gérer les événements, utilisateurs, réservations, etc.

Visualiser les statistiques et utiliser les fonctionnalités avancées.

🧪 Tests
✅ Frontend (JavaFX) : TestFX, JUnit5

✅ Backend : Postman, Spring Boot Test, MockMvc

🤝 Contribution
Forker le projet

Créer une branche : feature/ma-fonctionnalite

Commiter : git commit -m "feat: nouvelle fonctionnalité"

Pousser la branche : git push origin feature/ma-fonctionnalite

Ouvrir une Pull Request

Respecter la convention de commit type: description (ex: fix: correction du bug login).

📄 Licence
Ce projet est sous licence MIT.
Consultez le fichier LICENSE pour plus d'informations.

👨‍💻 Équipe
🧠 Lunatics Team – Conception, développement & tests

🙏 Merci à tous les collaborateurs & testeurs

🔍 Mots-clés
evenix • javafx • gestion événements • desktop app • spring boot • qr code • pdf • stripe • chatbot • reclamation • calendrier • reservation • météo • ai • fxml


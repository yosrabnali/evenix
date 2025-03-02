package services.ReclamationsServices;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private final String username;  //Votre adresse email
    private final String password;  //Votre mot de passe email

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        // Configuration des propriétés pour la connexion au serveur SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Serveur SMTP (Exemple: Gmail)
        props.put("mail.smtp.port", "587"); // Port SMTP
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  //Pour Gmail

        // Création de la session
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            // Envoi du message
            Transport.send(message);

            System.out.println("Email successfully sent to : " + to);

        } catch (MessagingException e) {
            System.out.println("Error while sending the email to : " + to);
            e.printStackTrace();
            throw e; // Re-throw pour que l'appelant puisse gérer l'erreur.
        }
    }
}
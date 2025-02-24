package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    // Remplace par tes propres identifiants Twilio
    public static final String ACCOUNT_SID = "AC7b244df0a98e38c1c8c4d8ba56510ffe";
    public static final String AUTH_TOKEN = "c1028c1ca70e36f8f1e48c19e3c8feb1";
    public static final String TWILIO_PHONE_NUMBER = "+15807067139";

    public static void sendSms(String to, String message) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message sms = Message.creator(
                new PhoneNumber(to), // Numéro du destinataire
                new PhoneNumber(TWILIO_PHONE_NUMBER), // Numéro Twilio
                message // Contenu du SMS
        ).create();

        System.out.println("SMS envoyé avec SID : " + sms.getSid());
    }
}

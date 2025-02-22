package services.EventsServices;

import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import java.math.BigDecimal;

public class PaymentService {
    public PaymentIntent createPaymentIntent(long amount, String currency) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .build();
        return PaymentIntent.create(params);
    }

    public Session createCheckoutSession(double amount, String currency, int eventId, String eventName) throws Exception {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // For local development, use HTTP if HTTPS isn’t configured
                .setSuccessUrl("http://localhost/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost/cancel.html")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount((long) amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(eventName) // Display the event name
                                                                .build())
                                                .build())
                                .setQuantity(1L)
                                .build())
                // Add event-specific metadata for later processing
                .putMetadata("event_id", String.valueOf(eventId))
                .putMetadata("event_name", eventName)
                .build();
        return Session.create(params);
    }

}
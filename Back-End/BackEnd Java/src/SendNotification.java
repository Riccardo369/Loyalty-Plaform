import java.sql.Timestamp;


/**
 * Questa interfaccia manda le notifiche direttamente dal sito
 */
public interface SendNotification {

    /**
     *
     * @param Text to send
     * @param Destination to send email
     *
     * @return true if email is sent correctly, otherwise false
     */
    default boolean SendEmail(String Text, String Destination){

        String OwnEmail = ""; //L' email della piattaforma di fidelizzazione

        Email email = new Email(OwnEmail, Destination, Text, new Timestamp(System.currentTimeMillis()));

        //MOCK: Invio dell' email

        return true;
    }

    /**
     *
     * @param sms to send
     *
     * @return true if SMS is sent correctly, otherwise false
     */
    private boolean SendSMS(SMS sms){

        //TODO
        return true;
    }
}

package es.tena.foundation.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class MailUtil {

    private static final String SMTP_HOST_NAME = "";
    private static final String SMTP_AUTH_USER = "";
    private static final String SMTP_AUTH_PWD = "";

    /**
     * Sends an email to the especified "to"
     *
     * @param from
     * @param to comma separated values (,)
     * @param subject
     * @param cc
     * @param cco
     * @param message
     * @throws Exception
     */
    public static void sendMessage(String from, String to,
            String subject, String cc, String cco, String message) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
//		props.put("mail.smtp.user", SMTP_AUTH_USER);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session sendMailSession = Session.getInstance(props, auth);

        Message newMessage = new MimeMessage(sendMailSession);

        newMessage.setFrom(new InternetAddress(from));

        newMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
                to));
        if (cc != null && !cc.equals("")) {
            newMessage.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));
        }

        if (cco != null && !cco.equals("")) {
            newMessage.setRecipients(Message.RecipientType.BCC,
                    InternetAddress.parse(cco));
        }

        newMessage.setSubject(subject);
        newMessage.setText(message);
        Transport transport = sendMailSession.getTransport("smtp");
        transport.connect(SMTP_HOST_NAME, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        transport.send(newMessage);
    }

    /**
     * Sends a HTML formatted mail to the specified "to"
     *
     * @param from
     * @param to comma separated values (,)
     * @param subject
     * @param cc
     * @param cco
     * @param message
     * @throws Exception
     */
    public static void sendHtmlMessage(String from, String to, String subject, String cc, String cco, String message) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
//		props.put("mail.smtp.user", SMTP_AUTH_USER);
        props.put("mail.smtp.auth", "true");

        Session sendMailSession = Session.getInstance(props, null);

        Message newMessage = new MimeMessage(sendMailSession);

        newMessage.setFrom(new InternetAddress(from));
        newMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
                to));
        if (cc != null && !cc.equals("")) {
            newMessage.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));
        }

        if (cco != null && !cco.equals("")) {
            newMessage.setRecipients(Message.RecipientType.BCC,
                    InternetAddress.parse(cco));
        }

        newMessage.setSubject(subject);
        newMessage.setContent(message, "text/html");

        Transport transport = sendMailSession.getTransport("smtp");
        transport.connect(SMTP_HOST_NAME, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        transport.send(newMessage);
    }

    /**
     * Sends email authenticating the user specified
     *
     * @param recipients
     * @param subject
     * @param message
     * @param from
     * @throws MessagingException
     */
    public void postMail(String recipients[], String subject, String message, String from) throws MessagingException {
        boolean debug = false;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);

        session.setDebug(debug);
        Message msg = new MimeMessage(session);

        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        msg.setContent(message, "text/plain");

        Transport transport = session.getTransport("smtp");
        transport.connect(SMTP_HOST_NAME, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        transport.send(msg);
    }

    private static class SMTPAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }

}

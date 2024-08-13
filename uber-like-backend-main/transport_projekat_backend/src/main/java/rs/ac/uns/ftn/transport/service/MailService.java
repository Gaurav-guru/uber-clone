package rs.ac.uns.ftn.transport.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.UserActivation;
import rs.ac.uns.ftn.transport.service.interfaces.IMailService;

import java.io.UnsupportedEncodingException;

@Service
public class MailService implements IMailService {
    private final JavaMailSender mailSender;

    String activationMessage =
            "<p>Hello,</p>"
                    + "<p>You can activate your account by clicking on the following link:</p>";

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String recipientEmail, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("gauravdw9096@gmail.com", "CarGoBrr");
        helper.setTo(recipientEmail);

        String subject = "Password Reset Token";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>This is the token you need to reset your password:</p>"
                + token + "<br>"
                + "<p>Ignore this email if you remember your password, "
                + "or if you did not make this request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void sendActivationEmail(String recipientEmail, UserActivation activation) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("gauravdw9096@gmail.com", "CarGoBrr");
        helper.setTo(recipientEmail);

        String subject = "Account Activation";

        String activationLink = "http://localhost:8080/api/passenger/activate/" + activation.getId();
        String body = this.activationMessage +
                "<a href='" + activationLink + "'>" + activationLink + "</a>";

        helper.setSubject(subject);

        helper.setText(body, true);

        mailSender.send(message);
    }
}

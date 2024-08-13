package rs.ac.uns.ftn.transport.service.interfaces;

import javax.mail.MessagingException;
import rs.ac.uns.ftn.transport.model.UserActivation;

import java.io.UnsupportedEncodingException;

public interface IMailService {
    void sendMail(String recipientEmail, String token) throws MessagingException, UnsupportedEncodingException;
    void sendActivationEmail(String recipientEmail, UserActivation activation) throws MessagingException,UnsupportedEncodingException;
}

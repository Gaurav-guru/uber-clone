package rs.ac.uns.ftn.transport.service.interfaces;

import javax.mail.MessagingException;
import rs.ac.uns.ftn.transport.model.User;
import rs.ac.uns.ftn.transport.model.UserActivation;

import java.io.UnsupportedEncodingException;

public interface IUserActivationService {

    UserActivation findOne(Integer id);
    UserActivation save(UserActivation activation) throws MessagingException, UnsupportedEncodingException;
    void delete(UserActivation activation);
    UserActivation findByUser(User user);

}

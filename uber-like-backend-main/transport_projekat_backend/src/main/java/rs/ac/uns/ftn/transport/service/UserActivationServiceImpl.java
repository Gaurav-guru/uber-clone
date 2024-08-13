package rs.ac.uns.ftn.transport.service;

import javax.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.User;
import rs.ac.uns.ftn.transport.model.UserActivation;
import rs.ac.uns.ftn.transport.repository.UserActivationRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IMailService;
import rs.ac.uns.ftn.transport.service.interfaces.IUserActivationService;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
public class UserActivationServiceImpl implements IUserActivationService {

    private final UserActivationRepository userActivationRepository;
    private final IMailService mailService;

    public UserActivationServiceImpl(UserActivationRepository userActivationRepository, IMailService mailService) {
        this.userActivationRepository = userActivationRepository;
        this.mailService = mailService;
    }

    @Override
    public UserActivation findOne(Integer id) {
        Optional<UserActivation> activation = userActivationRepository.findById(id);
        if(activation.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return activation.get();
    }

    @Override
    public UserActivation save(UserActivation activation) throws MessagingException, UnsupportedEncodingException {
        UserActivation created = userActivationRepository.save(activation);
        mailService.sendActivationEmail(activation.getUser().getEmail(),created);
        return created;
    }

    @Override
    public void delete(UserActivation activation) {
        userActivationRepository.delete(activation);
    }

    @Override
    public UserActivation findByUser(User user) {
        return userActivationRepository.findByUser(user);
    }
}
